package de.woody64k.services.word.service.parser;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import de.woody64k.services.word.model.content.ContentTable;
import de.woody64k.services.word.model.content.ContentTable.TABLE_TYPE;
import de.woody64k.services.word.model.content.elements.ParsedTableRow;

public class FlatTableParser {

    public static ContentTable parseTable(XWPFTable table) {
        ContentTable contentTable = new ContentTable();
        contentTable.setTableType(TABLE_TYPE.FLAT);
        for (XWPFTableRow row : table.getRows()) {
            ParsedTableRow contentRow = parseRow(row);
            // @implements FR-05
            if (contentRow.isFilled()) {
                contentTable.add(contentRow);
                contentTable.setFilled(true);
            }
        }
        return contentTable;
    }

    public static ParsedTableRow parseRow(XWPFTableRow row) {
        ParsedTableRow contentRow = new ParsedTableRow();
        for (XWPFTableCell cell : row.getTableCells()) {
            List<String> paragraphs = cell.getParagraphs().stream().map(x -> x.getParagraphText().trim()).collect(Collectors.toList());
            String value = String.join("\n", paragraphs);
            contentRow.add(value);
            if (!value.isBlank()) {
                // @implements FR-05
                contentRow.setFilled(true);
            }
        }
        return contentRow;
    }
}
