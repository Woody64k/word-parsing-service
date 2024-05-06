package de.woody64k.services.word.service.parser;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import de.woody64k.services.word.model.content.ContentTable;
import de.woody64k.services.word.model.content.ContentTable.TABLE_TYPE;
import de.woody64k.services.word.model.content.ContentTableRow;

public class FlatTableParser {

    public static ContentTable parseTable(XWPFTable table) {
        ContentTable contentTable = new ContentTable();
        contentTable.setType(TABLE_TYPE.FLAT);
        for (XWPFTableRow row : table.getRows()) {
            ContentTableRow contentRow = parseRow(row);
            // @implements FR-05
            if (contentRow.isFilled()) {
                contentTable.addRow(contentRow);
                contentTable.setFilled(true);
            }
        }
        return contentTable;
    }

    public static ContentTableRow parseRow(XWPFTableRow row) {
        ContentTableRow contentRow = new ContentTableRow();
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
