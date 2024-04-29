package de.woody64k.services.word.service.parser;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import de.woody64k.services.word.model.content.ContentTable;
import de.woody64k.services.word.model.content.ContentTableRow;
import de.woody64k.services.word.model.content.ContentTable.TABLE_TYPE;

public class FlatTableParser {

    public static ContentTable parseTable(XWPFTable table) {
        ContentTable contentTable = new ContentTable();
        contentTable.setType(TABLE_TYPE.FLAT);
        for (XWPFTableRow row : table.getRows()) {
            contentTable.addRow(parseRow(row));
        }
        return contentTable;
    }

    public static ContentTableRow parseRow(XWPFTableRow row) {
        ContentTableRow contentRow = new ContentTableRow();
        for (XWPFTableCell cell : row.getTableCells()) {
            contentRow.add(cell.getText());
        }
        return contentRow;
    }
}
