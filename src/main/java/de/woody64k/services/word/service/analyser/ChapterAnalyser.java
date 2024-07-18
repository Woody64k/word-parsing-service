package de.woody64k.services.word.service.analyser;

import de.woody64k.services.word.model.content.ContentBlock;
import de.woody64k.services.word.model.content.ContentTable;
import de.woody64k.services.word.model.content.ContentText;
import de.woody64k.services.word.model.content.IContent;
import de.woody64k.services.word.model.content.IContent.ContentCategory;
import de.woody64k.services.word.model.content.WordContent;
import de.woody64k.services.word.model.content.elements.ParsedTableRow;
import de.woody64k.services.word.model.value.request.SearchRequirement;
import de.woody64k.services.word.model.value.response.GenericObject;

/**
 * @implements FR-12
 */
public class ChapterAnalyser {

    public static GenericObject analyse(WordContent parsedData, SearchRequirement searchRequirement) {
        GenericObject result = new GenericObject();
        for (IContent content : parsedData.getContent()) {
            result.putAll(analyseContent(content, searchRequirement));
        }
        return result;
    }

    public static GenericObject analyseContent(IContent content, SearchRequirement searchRequirement) {
        GenericObject result = new GenericObject();
        if (ContentCategory.BLOCK == content.getContentCategory()) {
            ContentBlock block = (ContentBlock) content;
            if (block.getHeading()
                    .contentEquals(searchRequirement.getSearchTerm())) {
                String value = flattenValues(block);
                result.put(searchRequirement.getResultName(), value);
            } else {
                // Deep-Search
                for (IContent subContent : block.getContent()) {
                    result.putAll(analyseContent(subContent, searchRequirement));
                }
            }
        } else if (ContentCategory.TABLE == content.getContentCategory()) {
            ContentTable subTable = (ContentTable) content;
            for (ParsedTableRow row : subTable.getTable()) {
                for (Object cell : row) {
                    if (cell instanceof WordContent) {
                        for (IContent subContent : ((WordContent) cell).getContent()) {
                            result.putAll(analyseContent(subContent, searchRequirement));
                        }
                    }
                }
            }
        }
        return result;
    }

    // ====================================================================================================================================
    // flatten Values
    // ====================================================================================================================================
    private static String flattenValues(WordContent block) {
        StringBuilder sb = new StringBuilder();
        for (IContent content : block.getContent()) {
            if (ContentCategory.BLOCK == content.getContentCategory()) {
                ContentBlock subBlock = (ContentBlock) content;
                sb.append(String.format("%s %s\n", "=".repeat(subBlock.getLevel()), subBlock.getHeading()));
                sb.append(flattenValues(subBlock));
            } else if (ContentCategory.TEXT == content.getContentCategory()) {
                ContentText subText = (ContentText) content;
                sb.append(String.format("%s\n", subText.getText()));
            } else if (ContentCategory.TABLE == content.getContentCategory()) {
                ContentTable subTable = (ContentTable) content;
                sb.append(String.format("%s\n", getDocumentsInTable(subTable)));
            }
        }
        return sb.toString();
    }

    private static String getDocumentsInTable(ContentTable subTable) {
        StringBuilder sb = new StringBuilder();
        for (ParsedTableRow row : subTable.getTable()) {
            for (Object cell : row) {
                if (cell instanceof WordContent) {
                    sb.append(String.format("%s\n", flattenValues((WordContent) cell)));
                } else if (cell instanceof String) {
                    sb.append(String.format("%s\n", (String) cell));
                }
            }
        }
        return sb.toString();
    }
}
