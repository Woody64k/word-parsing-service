package de.woody64k.services.word.service.parser;

import java.util.List;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import de.woody64k.services.word.model.content.ContentTable;
import de.woody64k.services.word.model.content.ContentTable.TABLE_TYPE;
import de.woody64k.services.word.model.content.WordContent;
import de.woody64k.services.word.model.content.elements.ParsedTableRow;

public class FlatTableParser {

    public static ContentTable parseTable(XWPFTable table, Integer headingNumId) {
        ContentTable contentTable = new ContentTable();
        contentTable.setTableType(TABLE_TYPE.FLAT);
        for (XWPFTableRow row : table.getRows()) {
            ParsedTableRow contentRow = parseRow(row, headingNumId);
            // @implements FR-05
            if (contentRow.isFilled()) {
                contentTable.add(contentRow);
                contentTable.setFilled(true);
            }
        }
        return contentTable;
    }

    public static ParsedTableRow parseRow(XWPFTableRow row, Integer headingNumId) {
        ParsedTableRow contentRow = new ParsedTableRow();
        for (XWPFTableCell cell : row.getTableCells()) {
            List<IBodyElement> elemnts = cell.getBodyElements();

            // FR-10: handle embedded Documents
            BodyElementParser parser = new BodyElementParser(headingNumId);
            WordContent cellContent = parser.parseBodyElements(elemnts);
            contentRow.add(cellContent);
            if (!cellContent.isEmpty()) {
                // @implements FR-05
                contentRow.setFilled(true);
            }
        }
        return contentRow;
    }
}
