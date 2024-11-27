package de.woody64k.services.document.pdf.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.woody64k.services.document.model.content.ContentTable;
import de.woody64k.services.document.model.content.ContentText;
import de.woody64k.services.document.model.content.DocumentContent;
import de.woody64k.services.document.model.content.IContent;
import de.woody64k.services.document.model.content.IContent.ContentCategory;
import de.woody64k.services.document.model.content.elements.ParsedTable;
import de.woody64k.services.document.model.content.elements.ParsedTableRow;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PdfParser {
    private final static int PERCENTAGE_OF_FILLED_ROW_BREAK = 50;

    public DocumentContent parseContent(List<String> tableHeaderIndicator, MultipartFile uploadFile) {
        List<DocumentContent> contentByPage = PlainPdfParser.collectDocumentContent(uploadFile);

        contentByPage.stream()
                .filter(table -> !table.isEmpty())
                .collect(Collectors.toList());

        mergeTablesAcrossPages(contentByPage, tableHeaderIndicator);
        mergeTextsAcrossPages(contentByPage);

        DocumentContent content = DocumentContent.from(contentByPage);
        content.setFileName(uploadFile.getOriginalFilename());
        return content;
    }

    private void mergeTextsAcrossPages(List<DocumentContent> contentByPage) {
        for (int i = contentByPage.size() - 1; i > 0; i--) {
            DocumentContent lastPageContent = contentByPage.get(i - 1);
            DocumentContent pageContent = contentByPage.get(i);
            IContent lastBlockOfLastPage = lastPageContent.getContent()
                    .getLast();
            IContent firstBlock = pageContent.getContent()
                    .getFirst();
            if (ContentCategory.TEXT == firstBlock.getContentCategory() && ContentCategory.TEXT == lastBlockOfLastPage.getContentCategory() && !lastBlockOfLastPage.flattenToString()
                    .contains(":")) {
                ((ContentText) lastBlockOfLastPage).appendText("\n")
                        .appendText(firstBlock.flattenToString());
                removeContent(contentByPage, pageContent, firstBlock);
            }
        }
    }

    private void mergeTablesAcrossPages(List<DocumentContent> contentByPage, List<String> tableHeaderIndicator) {
        for (int i = contentByPage.size() - 1; i > 0; i--) {
            List<ContentTable> lastPageTables = contentByPage.get(i - 1)
                    .getTables();
            List<ContentTable> pageTables = contentByPage.get(i)
                    .getTables();
            if (lastPageTables.size() > 0 && pageTables.size() > 0) {
                ParsedTable lastTableOfLastPage = findLastMatchingTable(lastPageTables, tableHeaderIndicator);
                if (lastTableOfLastPage != null) {
                    ContentTable continueTableBlock = findPropableContinuingTable(pageTables, tableHeaderIndicator, lastTableOfLastPage.maxWith());
                    if (continueTableBlock != null) {
                        ParsedTable continueTable = continueTableBlock.getTable();
                        mergeFirstLine(lastTableOfLastPage, continueTable);
                        lastTableOfLastPage.append(continueTable);
                        removeContent(contentByPage, contentByPage.get(i), continueTableBlock);
                    }
                }
            }
        }
    }

    private ContentTable findPropableContinuingTable(List<ContentTable> pageTables, List<String> tableHeaderIndicator, int withOfLead) {
        for (ContentTable table : pageTables) {
            boolean noHeaderFound = !matchesHeaderDetection(tableHeaderIndicator, table.getTable());
            boolean withMatches = table.getTable()
                    .maxWith() == withOfLead;
            if (noHeaderFound && withMatches) {
                return table;
            }
        }
        for (ContentTable table : pageTables) {
            boolean hasOnlyOneRow = table.getTable()
                    .size() == 1;
            if (hasOnlyOneRow) {
                return table;
            }
        }
        return null;
    }

    private ParsedTable findLastMatchingTable(List<ContentTable> lastPageTables, List<String> tableHeaderIndicator) {
        for (int i = lastPageTables.size() - 1; i >= 0; i--) {
            if (matchesHeaderDetection(tableHeaderIndicator, lastPageTables.get(i)
                    .getTable())) {
                return lastPageTables.get(i)
                        .getTable();
            }
        }
        return null;
    }

    public boolean matchesHeaderDetection(List<String> tableHeaderIndicator, ParsedTable firstTable) {
        return !firstTable.getFirst()
                .stream()
                .filter(headLine -> tableHeaderIndicator.contains(headLine))
                .collect(Collectors.toList())
                .isEmpty();
    }

    private void mergeFirstLine(ParsedTable lastTableOfLastPage, ParsedTable firstTable) {
        ParsedTableRow lineToMerge = firstTable.getFirst();
        ParsedTableRow leadingLine = lastTableOfLastPage.getLast();

        List<Integer> filledCellsOfLineToMerge = lineToMerge.filledCells();
        List<Integer> filledCellsOfLeadingLine = leadingLine.filledCells();

        boolean lessFilled = (filledCellsOfLineToMerge.size() / lineToMerge.size() * 100 < PERCENTAGE_OF_FILLED_ROW_BREAK);
        boolean leadingLinesAreFilled = filledCellsOfLeadingLine.stream()
                .filter(nr -> !filledCellsOfLeadingLine.contains(nr))
                .collect(Collectors.toList())
                .isEmpty();

        if (lessFilled && leadingLinesAreFilled) {
            for (int nr : filledCellsOfLineToMerge) {
                leadingLine.appendTo(nr, lineToMerge.get(nr));
            }
            firstTable.removeFirst();
        } else {
            if (firstTable.size() == 1) {
                // Special Case for table stubs
                Map<Integer, DescriptiveStatistics> statics = lastTableOfLastPage.calculateColumnStatistics();

                // Expect the Columns wit the most text in average will be splitted
                Map<Integer, DescriptiveStatistics> filteredStatistcs = statics.entrySet()
                        .stream()
                        .sorted((e1, e2) -> (e2.getValue()
                                .getGeometricMean()
                                - e1.getValue()
                                        .getGeometricMean()) > 0 ? 1 : -1)
                        .limit(filledCellsOfLineToMerge.size())
                        .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
                for (int i : filteredStatistcs.keySet()) {
                    Object toMerge = lineToMerge.get(filledCellsOfLineToMerge.removeFirst());
                    leadingLine.appendTo(i, toMerge);
                }
                firstTable.removeFirst();
            }
        }
    }

    public void removeContent(List<DocumentContent> contentByPage, DocumentContent pageContent, IContent firstBlock) {
        pageContent.getContent()
                .remove(firstBlock);
        purgeEmptyPages(contentByPage, pageContent);
    }

    public void purgeEmptyPages(List<DocumentContent> contentByPage, DocumentContent pageContent) {
        if (pageContent.isEmpty()) {
            contentByPage.remove(pageContent);
        }
    }
}