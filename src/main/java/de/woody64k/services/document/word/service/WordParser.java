package de.woody64k.services.document.word.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.woody64k.services.document.model.content.DocumentContent;
import de.woody64k.services.document.word.service.parser.BodyElementParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WordParser {

    public DocumentContent parseContent(MultipartFile uploadFile) {
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(uploadFile.getBytes()))) {
            BodyElementParser bodyParser = new BodyElementParser();
            DocumentContent content = bodyParser.parseBodyElements(doc.getBodyElements());
            content.setFileName(uploadFile.getOriginalFilename());
            return content;
        } catch (IOException e) {
            log.error("Error while parse Document.", e);
            throw new RuntimeException(e);
        }
    }
}