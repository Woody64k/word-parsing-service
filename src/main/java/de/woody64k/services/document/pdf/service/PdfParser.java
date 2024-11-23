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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PdfParser {
    public DocumentContent parseConent(List<String> tableHeaderIndicator, MultipartFile uploadFile) {
        List<DocumentContent> contentByPage = PlainPdfParser.collectDocumentContent(uploadFile);

        contentByPage.stream()
                .filter(table -> !table.isEmpty())
                .collect(Collectors.toList());

        mergeTextsAcrossPages(contentByPage);
        mergeTablesAcrossPages(contentByPage, tableHeaderIndicator);
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
                    lastTableOfLastPage.getTable()
                            .append(firstTable.getTable());
                    removeContent(contentByPage, contentByPage.get(i), firstTable);
                }
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