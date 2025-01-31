package de.woody64k.services.document.pdf.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.web.multipart.MultipartFile;

import de.woody64k.services.document.model.content.ContentTable;
import de.woody64k.services.document.model.content.DocumentContent;
import de.woody64k.services.document.model.content.elements.ParsedTableRow;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.BasicExtractionAlgorithm;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

public class PlainPdfParser {

    public static List<DocumentContent> collectDocumentContent(MultipartFile uploadFile) {
        try (PDDocument pdf = PDDocument.load(new ByteArrayInputStream(uploadFile.getBytes()))) {
            PageIterator pi = new ObjectExtractor(pdf).extract();
            List<DocumentContent> contentByPages = new ArrayList<>();
            DocumentContent lastPageContent;
            pi.forEachRemaining(page -> {
                contentByPages.add(readPage(page));
            });
            return contentByPages;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static DocumentContent readPage(Page page) {
        DocumentContent content = new DocumentContent();
        SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
        List<Table> tableList = sea.extract(page);
        List<RectangularTextContainer> textsOfPage = getPageTexts(page);

        if (tableList.isEmpty()) {
            content.addTexts(combineText(textsOfPage));
        } else {
            for (Table table : tableList) {
                // Collect Text before Table
                List<RectangularTextContainer> textBeforeTable = findTextBeforeTable(textsOfPage, table);
                content.addTexts(combineText(textBeforeTable));
                textsOfPage.removeAll(textBeforeTable);

                ContentTable contentTable = readTable(table);

                cleanupTable(contentTable);
                if (!contentTable.isBlank()) {
                    content.add(contentTable);
                }
                textsOfPage = findTextAfterTable(textsOfPage, table);
            }
            // Collect Text After Tables
            content.addTexts(combineText(textsOfPage));
        }
        return content;
    }

    public static ContentTable readTable(Table table) {
        ContentTable contentTable = new ContentTable();

        List<List<RectangularTextContainer>> rows = table.getRows();
        // iterate over the rows of the table
        int lineCount = 0;
        for (List<RectangularTextContainer> cells : rows) {
            cells = cellsContainedInSorunding(cells, table, lineCount);
            ParsedTableRow contentRow = readRow(cells);
            contentTable.add(contentRow);
            lineCount++;
        }
        return cleanupTable(contentTable);
    }

    private static List<RectangularTextContainer> cellsContainedInSorunding(List<RectangularTextContainer> cells, Table table, int lineIndex) {
        List<RectangularTextContainer> columnsToRemove = new ArrayList<>();
        for (int i = 0; i < cells.size(); i++) {
            RectangularTextContainer cell = cells.get(i);
            if (containedInLeftCells(cells, i)) {
                columnsToRemove.add(cell);
            } else if (containedInAbothCells(table, lineIndex, i)) {
                columnsToRemove.add(cell);
            }
        }
        cells.removeAll(columnsToRemove);
        return cells;
    }

    private static boolean containedInAbothCells(Table table, int lineIndex, int i) {
        RectangularTextContainer actualCell = table.getCell(lineIndex, i);
        for (int j = lineIndex - 1; j >= 0; j--) {
            List<RectangularTextContainer> rowAboth = table.getRows()
                    .get(j);
            for (RectangularTextContainer cell : rowAboth) {
                if (cell.contains(actualCell)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean containedInLeftCells(List<RectangularTextContainer> cells, int i) {
        RectangularTextContainer actualCell = cells.get(i);
        for (int j = i - 1; j >= 0; j--) {
            if (cells.get(j)
                    .contains(actualCell)) {
                return true;
            }
        }
        return false;
    }

    public static ParsedTableRow readRow(List<RectangularTextContainer> cells) {
        ParsedTableRow contentRow = new ParsedTableRow();
        // print all column-cells of the row plus linefeed
        for (RectangularTextContainer cellContent : cells) {
            contentRow.add(getOneLineText(cellContent));
        }
        return contentRow;
    }

    /**
     * Cleans empty rows and lines from the table.
     * 
     * @implements FR-13: Cleanup artifacts in pdf
     * @param contentTable
     * @return
     */
    public static ContentTable cleanupTable(ContentTable contentTable) {
        contentTable.getTable()
                .removeEmptyColumns();
        contentTable.getTable()
                .removeEmptyRows();
        return contentTable;
    }

    private static List<RectangularTextContainer> findTextBeforeTable(List<RectangularTextContainer> textsOfPage, Table table) {
        List<RectangularTextContainer> textBeforeTable = new ArrayList<>();
        for (RectangularTextContainer textBlock : textsOfPage) {
            if (!table.contains(textBlock)) {
                textBeforeTable.add(textBlock);
            } else {
                return textBeforeTable;
            }
        }
        return textBeforeTable;
    }

    private static List<RectangularTextContainer> findTextAfterTable(List<RectangularTextContainer> textsOfPage, Table table) {
        List<RectangularTextContainer> textsKeep = new ArrayList<>(textsOfPage);
        List<RectangularTextContainer> textsRead = new ArrayList<>();
        for (RectangularTextContainer textBlock : textsOfPage) {
            textsRead.add(textBlock);
            if (table.contains(textBlock)) {
                textsKeep.removeAll(textsRead);
                textsRead.clear();
            }
        }
        return textsKeep;
    }

    private static List<String> combineText(List<RectangularTextContainer> textsOfPage) {
        List<String> texts = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (RectangularTextContainer textBlock : textsOfPage) {
            String text = getOneLineText(textBlock);
            if (text != null && !text.isBlank()) {
                if (text.contains(":")) {
                    if (!sb.isEmpty()) {
                        texts.add(createStringWithoutWordBreaks(sb));
                        sb.setLength(0);
                        texts.add(text);
                    }
                } else {
                    if (!sb.isEmpty()) {
                        sb.append('\n');
                    }
                    sb.append(text);
                }
            }
        }
        texts.add(createStringWithoutWordBreaks(sb));
        return texts;
    }

    public static String createStringWithoutWordBreaks(StringBuilder sb) {
        return sb.toString()
                .replace("-\n", "");
    }

    private static List<RectangularTextContainer> getPageTexts(Page page) {
        Map<Integer, List<RectangularTextContainer>> pageTexts = new HashMap<>();
        BasicExtractionAlgorithm basicExtraction = new BasicExtractionAlgorithm();
        List<RectangularTextContainer> textsOnPage = new ArrayList<>();
        List<Table> tableList = basicExtraction.extract(page);
        // iterate over the tables of the page
        for (Table table : tableList) {
            List<List<RectangularTextContainer>> rows = table.getRows();
            for (List<RectangularTextContainer> cells : rows) {
                textsOnPage.addAll(cells);
            }
        }
        return textsOnPage;
    }

    private static String getOneLineText(RectangularTextContainer cellContent) {
        // Handle Linebreaks
        String text = cellContent.getText()
                .replace("-\r", "")
                .replace("\r", " ");
        return text;
    }
}
