package de.woody64k.services.document.msg.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.woody64k.services.document.model.content.ContentText;
import de.woody64k.services.document.model.content.DocumentContent;
import de.woody64k.services.document.model.content.IContent;
import de.woody64k.services.document.msg.service.model.Attachment;
import de.woody64k.services.document.pdf.service.PdfParser;
import de.woody64k.services.document.word.service.WordParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MsgParser {
    private final static int PERCENTAGE_OF_FILLED_ROW_BREAK = 50;

    @Autowired
    WordParser wordParser;

    @Autowired
    PdfParser pdfParser;

    public DocumentContent parseContent(List<String> tableHeaderIndicator, MultipartFile uploadFile) {
        try (MAPIMessage msg = new MAPIMessage(new ByteArrayInputStream(uploadFile.getBytes()))) {
            DocumentContent mail = convertMail(msg);

            // Handle Attachments.
            AttachmentChunks[] attachments = msg.getAttachmentFiles();
            List<MultipartFile> listOfAttachedFiles = convertAttachments(attachments);
            mail.addAll(parseAllAttachmants(tableHeaderIndicator, listOfAttachedFiles));
            mail.setFileName(uploadFile.getOriginalFilename());
            return mail;
        } catch (IOException | ChunkNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private DocumentContent convertMail(MAPIMessage msg) throws ChunkNotFoundException {
        DocumentContent mail = new DocumentContent();
        msg.setReturnNullOnMissingChunk(true);
        mail.addText(String.format("Subject: %s", msg.getSubject()));
        mail.addAll(processBody(msg.getTextBody()));

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

    public List<MultipartFile> getAttachments(MultipartFile uploadFile) {
        try (MAPIMessage msg = new MAPIMessage(new ByteArrayInputStream(uploadFile.getBytes()))) {
            AttachmentChunks[] attachments = msg.getAttachmentFiles();
            List<MultipartFile> listOfAttachedFiles = convertAttachments(attachments);
            return listOfAttachedFiles;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                default:
                    break;
            }
        }
        return result;
    }

    public List<MultipartFile> convertAttachments(AttachmentChunks[] attachments) {
        List<MultipartFile> listOfAttachedFiles = new ArrayList<>();
        for (AttachmentChunks attachment : attachments) {
            listOfAttachedFiles.add(new Attachment(attachment));
        }
        return listOfAttachedFiles;
    }

    private Filetypes detectFileType(MultipartFile file) {
        for (Filetypes type : Filetypes.values()) {
            if (type.mimeType.equalsIgnoreCase(file.getContentType())) {
                return type;
            }
        }
        return Filetypes.OTHERS;
    }

    public enum Filetypes {
        DOC("application/msword"), DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"), PDF(MediaType.APPLICATION_PDF.toString()), OTHERS("");

        private final String mimeType;

        private Filetypes(String mimeType) {
            this.mimeType = mimeType;
        }
    }
}