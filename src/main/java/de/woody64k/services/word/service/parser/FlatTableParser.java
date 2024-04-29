package de.woody64k.services.word.service.parser;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import de.woody64k.services.word.model.ContentTable;
import de.woody64k.services.word.model.ContentTableRow;

public class FlatTableParser {

    public static ContentTable parseTable(XWPFTable table) {
        ContentTable tableContent = new ContentTable();
        for (XWPFTableRow row : table.getRows()) {
            tableContent.addRow(parseRow(row));
        }
        return tableContent;
    }

    public static ContentTableRow parseRow(XWPFTableRow row) {
        ContentTableRow rowContent = new ContentTableRow();
        for (XWPFTableCell cell : row.getTableCells()) {
            rowContent.add(cell.getText());
        }
        return rowContent;
    }
}
