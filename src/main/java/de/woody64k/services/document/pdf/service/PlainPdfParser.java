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

    public static DocumentContent collectDocumentContent(MultipartFile uploadFile) {
        try (PDDocument pdf = PDDocument.load(new ByteArrayInputStream(uploadFile.getBytes()))) {
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            PageIterator pi = new ObjectExtractor(pdf).extract();
            DocumentContent content = new DocumentContent();
            pi.forEachRemaining(page -> {
                List<Table> tableList = sea.extract(page);
                List<RectangularTextContainer> textsOfPage = getPageTexts(page);

                ContentTable lastTable;
                if (tableList.isEmpty()) {
                    content.addText(combineText(textsOfPage));
                } else {
                    for (Table table : tableList) {
                        // Collect Text before Table
                        List<RectangularTextContainer> textBeforeTable = findTextBeforeTable(textsOfPage, table);
                        content.addText(combineText(textBeforeTable));
                        textsOfPage.removeAll(textBeforeTable);

                        ContentTable contentTable = new ContentTable();

                        List<List<RectangularTextContainer>> rows = table.getRows();
                        // iterate over the rows of the table
                        for (List<RectangularTextContainer> cells : rows) {
                            ParsedTableRow contentRow = new ParsedTableRow();
                            // print all column-cells of the row plus linefeed
                            for (RectangularTextContainer cellContent : cells) {
                                contentRow.add(getOneLineText(cellContent));
                            }
                            contentTable.add(contentRow);
                            System.out.println();
                        }
                        content.addTable(contentTable);
                        textsOfPage = findTextAfterTable(textsOfPage, table);
                    }
                    // Collect Text After Tables
                    content.addText(combineText(textsOfPage));
                }
            });
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private static String combineText(List<RectangularTextContainer> textsOfPage) {
        StringBuilder sb = new StringBuilder();
        for (RectangularTextContainer textBlock : textsOfPage) {
            String text = getOneLineText(textBlock);
            if (text != null && !text.isBlank()) {
                if (!sb.isEmpty()) {
                    sb.append('\n');
                }
                sb.append(text);
            }
        }
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
