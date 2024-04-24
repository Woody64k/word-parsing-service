package de.woody64k.services.word.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WordParser {

    public void parseConent(MultipartFile uploadFile) {
        try {
            XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(uploadFile.getBytes()));
            for (XWPFTable table : doc.getTables()) {
                table.getBody();
            }
        } catch (IOException e) {
            log.error("Error while parse Document.", e);
        }
    }
}
