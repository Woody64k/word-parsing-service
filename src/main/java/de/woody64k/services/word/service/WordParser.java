package de.woody64k.services.word.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.woody64k.services.word.model.content.WordContent;
import de.woody64k.services.word.service.parser.BodyElementParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WordParser {

    public WordContent parseConent(MultipartFile uploadFile) {
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(uploadFile.getBytes()))) {
            BodyElementParser bodyParser = new BodyElementParser();
            return bodyParser.parseBodyElements(doc.getBodyElements());
        } catch (IOException e) {
            log.error("Error while parse Document.", e);
            throw new RuntimeException(e);
        }
    }
}