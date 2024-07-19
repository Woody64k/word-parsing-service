package de.woody64k.services.word.service.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;

import de.woody64k.services.word.model.content.ContentTable;
import de.woody64k.services.word.model.content.ContentTable.TABLE_TYPE;
import de.woody64k.services.word.model.content.elements.ParsedTableRow;

/**
 * Parser to handle OLE Embedded Tables.
 */
public class OleTableParser {

    /**
     * Checks if a run contains an OLE Embedded Table and parses it as
     * ContentTables.
     * 
     * @param run
     * @return
     */
    public static Collection<ContentTable> parseOLEObjects(XWPFRun run) {
        CTR ctr = run.getCTR();
        String declareNameSpaces = "declare namespace o='urn:schemas-microsoft-com:office:office'";
        XmlObject[] oleObjects = ctr.selectPath(declareNameSpaces + ".//o:OLEObject");
        Collection<ContentTable> contentTables = new ArrayList<>();
        for (XmlObject oleObject : oleObjects) {
            XmlObject rIdAttribute = oleObject.selectAttribute("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id");
            if (rIdAttribute != null) {
                String rId = rIdAttribute.newCursor()
                        .getTextValue();
                contentTables.addAll(parseOLEObject(run.getDocument(), rId));
            }
        }
        return contentTables;
    }

    private static Collection<? extends ContentTable> parseOLEObject(XWPFDocument document, String rId) {
        POIXMLDocumentPart documentPart = document.getRelationById(rId);
        if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(documentPart.getPackagePart()
                .getContentType())) {
            return parseXSSFWorkbook(documentPart.getPackagePart());
        } else if ("application/vnd.ms-excel".equals(documentPart.getPackagePart()
                .getContentType())) {
            return parseHSSFWorkbook(documentPart.getPackagePart());
        } else {
            return new ArrayList<>();
        }
    }

    private static List<ContentTable> parseXSSFWorkbook(PackagePart part) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(part)) {
            return parseWorkbook(workbook);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static List<ContentTable> parseHSSFWorkbook(PackagePart part) {
        try (HSSFWorkbook workbook = new HSSFWorkbook(part.getInputStream())) {
            return parseWorkbook(workbook);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Parses the content of a Workbook into a Content Table. (Own Table per sheet)
     * 
     * @param workbook from ApachePOI
     * @return List of the Tables
     */
    public static List<ContentTable> parseWorkbook(Workbook workbook) {
        List<ContentTable> tables = new ArrayList<>();
        for (Sheet sheet : workbook) {
            ContentTable contentTable = new ContentTable();
            contentTable.setTableType(TABLE_TYPE.OLE);
            for (Row row : sheet) {
                ParsedTableRow contentRow = new ParsedTableRow();
                for (Cell cell : row) {
                    contentRow.add(getCellValue(cell));
                    if (!cell.toString()
                            .isBlank()) {
                        // @implements FR-05
                        contentRow.setFilled(true);
                    }
                }
                if (contentRow.isFilled()) {
                    // @implements FR-05
                    contentTable.add(contentRow);
                    contentTable.setFilled(true);
                }
            }
            if (contentTable.isFilled()) {
                // @implements FR-05
                tables.add(contentTable);
            }
        }
        return tables;
    }

    private static String getCellValue(Cell cell) {
        if (cell.getCellType() == CellType.FORMULA) {
            switch (cell.getCachedFormulaResultType()) {
                case CellType.NUMERIC:
                    return String.valueOf(cell.getNumericCellValue());
                case CellType.STRING:
                    return String.valueOf(cell.getRichStringCellValue())
                            .trim();
                case CellType.BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                default:
                    return null;
            }
        } else {
            return cell.toString()
                    .trim();
        }
    }
}
