package de.woody64k.services.document.msg.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.simplejavamail.api.email.AttachmentResource;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.converter.EmailConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.woody64k.services.document.model.content.ContentText;
import de.woody64k.services.document.model.content.DocumentContent;
import de.woody64k.services.document.model.content.IContent;
import de.woody64k.services.document.msg.service.model.Attachment;
import de.woody64k.services.document.msg.service.model.AttachmentFromZip;
import de.woody64k.services.document.pdf.service.PdfParser;
import de.woody64k.services.document.word.service.WordParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MsgParser {

    @Autowired
    WordParser wordParser;

    @Autowired
    PdfParser pdfParser;

    public DocumentContent parseContent(List<String> tableHeaderIndicator, MultipartFile uploadFile) {
        try {
            Email email = EmailConverter.outlookMsgToEmail(uploadFile.getInputStream());
            DocumentContent mail = convertMail(email);

            // Handle Attachments.
            @NotNull
            List<AttachmentResource> attachments = email.getDecryptedAttachments();
            List<MultipartFile> listOfAttachedFiles = convertAttachments(attachments);
            mail.addAll(parseAllAttachmants(tableHeaderIndicator, listOfAttachedFiles));
            mail.setFileName(uploadFile.getOriginalFilename());
            return mail;
        } catch (IOException | ChunkNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private DocumentContent convertMail(Email email) throws ChunkNotFoundException {
        DocumentContent mail = new DocumentContent();
        mail.addText(String.format("Subject: %s", email.getSubject()));
        mail.addAll(processBody(email.getPlainText()));
        return mail;
    }

    private List<IContent> processBody(String textBody) {
        List<String> lines = textBody.lines()
                .collect(Collectors.toList());
        List<String> newLines = new ArrayList<>();
        String lastDoublepointLine = "";
        for (String line : lines) {
            if (line.trim()
                    .length() > 0) {
                if (line.contains(":")) {
                    store(newLines, lastDoublepointLine);
                    lastDoublepointLine = line.trim();
                } else {
                    if (!lastDoublepointLine.isBlank() && line.trim()
                            .length() > 0) {
                        lastDoublepointLine = lastDoublepointLine.concat(" " + line.trim());
                    } else {
                        lastDoublepointLine = store(newLines, lastDoublepointLine);
                        newLines.add(line);
                    }
                }
            }
        }
        store(newLines, lastDoublepointLine);
        return newLines.stream()
                .map(str -> ContentText.create(str))
                .collect(Collectors.toList());
    }

    public String store(List<String> newLines, String lastDoublepointLine) {
        if (lastDoublepointLine.length() > 0) {
            newLines.add(lastDoublepointLine);
        }
        return "";
    }

    public List<IContent> parseAllAttachmants(List<String> tableHeaderIndicator, List<MultipartFile> files) {
        List<IContent> result = new ArrayList<>();
        for (MultipartFile file : files) {
            switch (detectFileType(file)) {
                case DOC:
                case DOCX:
                    result.add(wordParser.parseContent(file));
                    break;
                case PDF:
                    result.add(pdfParser.parseContent(tableHeaderIndicator, file));
                    break;
                case ZIP:
                    List<MultipartFile> filesOfZip = unZip(file);
                    result.addAll(parseAllAttachmants(tableHeaderIndicator, filesOfZip));
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    private List<MultipartFile> unZip(MultipartFile zipFile) {
        List<MultipartFile> returnList = new ArrayList<>();
        try (ZipInputStream zipIn = new ZipInputStream(zipFile.getInputStream())) {
            for (ZipEntry entry = zipIn.getNextEntry(); entry != null; entry = zipIn.getNextEntry()) {
                if (!entry.isDirectory()) {
                    returnList.add(new AttachmentFromZip(entry, zipIn));
                }
            }
            return returnList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MultipartFile> convertAttachments(@NotNull List<AttachmentResource> attachments) throws IOException {
        List<MultipartFile> listOfAttachedFiles = new ArrayList<>();
        for (AttachmentResource attachment : attachments) {
            listOfAttachedFiles.add(new Attachment(attachment));
        }
        return listOfAttachedFiles;
    }

    private Filetypes detectFileType(MultipartFile file) {
        for (Filetypes type : Filetypes.values()) {
            if (file.getContentType()
                    .contains(type.mimeType)) {
                return type;
            }
        }
        return Filetypes.OTHERS;
    }

    public enum Filetypes {
        DOC("application/msword"), DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"), PDF(MediaType.APPLICATION_PDF.toString()), ZIP("application/x-zip"), SIGNATURE("multipart/signed"), OTHERS("");

        private final String mimeType;

        private Filetypes(String mimeType) {
            this.mimeType = mimeType;
        }
    }
}