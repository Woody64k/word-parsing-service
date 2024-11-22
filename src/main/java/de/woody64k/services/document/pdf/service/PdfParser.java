package de.woody64k.services.document.pdf.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.woody64k.services.document.model.content.DocumentContent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PdfParser {
    public DocumentContent parseConent(MultipartFile uploadFile) {
        DocumentContent content = PlainPdfParser.collectDocumentContent(uploadFile);
        return content;
    }

}