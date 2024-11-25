package de.woody64k.services.document.pdf.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.woody64k.services.document.model.content.ContentTable;
import de.woody64k.services.document.model.content.ContentText;
import de.woody64k.services.document.model.content.DocumentContent;
import de.woody64k.services.document.model.content.IContent;
import de.woody64k.services.document.model.content.IContent.ContentCategory;
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
            if (ContentCategory.TEXT == firstBlock.getContentCategory() && ContentCategory.TEXT == lastBlockOfLastPage.getContentCategory()) {
                ((ContentText) lastBlockOfLastPage).appendText("/n")
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
                ContentTable lastTableOfLastPage = lastPageTables.getLast();
                ContentTable firstTable = pageTables.getFirst();

                boolean noHeaderFound = firstTable.getTable()
                        .getFirst()
                        .stream()
                        .filter(headLine -> tableHeaderIndicator.contains(headLine))
                        .collect(Collectors.toList())
                        .isEmpty();
                boolean withMatches = firstTable.getTable()
                        .maxWith() == lastTableOfLastPage.getTable()
                                .maxWith();
                if (noHeaderFound && withMatches) {
                    mergeFirstLine(lastTableOfLastPage, firstTable);
                    lastTableOfLastPage.getTable()
                            .append(firstTable.getTable());
                    removeContent(contentByPage, contentByPage.get(i), firstTable);
                }
            }
        }
    }

    private void mergeFirstLine(ContentTable lastTableOfLastPage, ContentTable firstTable) {
        ParsedTableRow lineToMerge = firstTable.getTable()
                .getFirst();
        ParsedTableRow leadingLine = lastTableOfLastPage.getTable()
                .getLast();

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
            firstTable.getTable()
                    .removeFirst();
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