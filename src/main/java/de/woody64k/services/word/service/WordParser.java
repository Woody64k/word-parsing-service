package de.woody64k.services.word.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.woody64k.services.word.model.content.ContentTable;
import de.woody64k.services.word.model.content.WordContent;
import de.woody64k.services.word.service.parser.FlatTableParser;
import de.woody64k.services.word.service.parser.OleTableParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WordParser {

    public WordContent parseConent(MultipartFile uploadFile) {
        WordContent docContent = new WordContent();
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(uploadFile.getBytes()))) {
            for (IBodyElement part : doc.getBodyElements()) {
                log.info(String.format("Element: %s -> %s", part.getElementType(), part.getBody()));
            }
            for (XWPFTable table : doc.getTables()) {
                ContentTable contentTable = FlatTableParser.parseTable(table);
                if (contentTable.isFilled()) {
                    docContent.addTable(contentTable);
                }
            }
            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    docContent.addTables(OleTableParser.parseOLEObjects(run));
                }
            }
            return docContent;
        } catch (IOException e) {
            log.error("Error while parse Document.", e);
            throw new RuntimeException(e);
        }
    }

}
