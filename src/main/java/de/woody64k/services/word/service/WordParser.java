package de.woody64k.services.word.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.woody64k.services.word.model.Table;
import de.woody64k.services.word.model.TableRow;
import de.woody64k.services.word.model.WordContent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WordParser {

    public WordContent parseConent(MultipartFile uploadFile) {
        WordContent docContent = new WordContent();
        try {
            XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(uploadFile.getBytes()));
            for (XWPFTable table : doc.getTables()) {
                docContent.addTable(parseTable(table));
            }
            return docContent;
        } catch (IOException e) {
            log.error("Error while parse Document.", e);
            throw new RuntimeException(e);
        }
    }

    private Table parseTable(XWPFTable table) {
        Table tableContent = new Table();
        for (XWPFTableRow row : table.getRows()) {
            tableContent.addRow(parseRow(row));
        }
        return tableContent;
    }

    private TableRow parseRow(XWPFTableRow row) {
        TableRow rowContent = new TableRow();
        for (XWPFTableCell cell : row.getTableCells()) {
            rowContent.add(cell.getText());
        }
        return rowContent;
    }
}
