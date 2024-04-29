package de.woody64k.services.word.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.woody64k.services.word.model.WordContent;
import de.woody64k.services.word.service.parser.FlatTableParser;
import de.woody64k.services.word.service.parser.OleTableParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WordParser {

    public WordContent parseConent(MultipartFile uploadFile) {
        WordContent docContent = new WordContent();
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(uploadFile.getBytes()))) {
            for (XWPFTable table : doc.getTables()) {
                docContent.addTable(FlatTableParser.parseTable(table));
            }
            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    docContent.addTables(OleTableParser.parseOLEObjects(run));
                }
            }
            XWPFRun run;
            return docContent;
        } catch (IOException e) {
            log.error("Error while parse Document.", e);
            throw new RuntimeException(e);
        }
    }

}
