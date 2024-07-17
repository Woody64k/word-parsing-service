package de.woody64k.services.word.service.parser;

import java.util.Collection;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import de.woody64k.services.word.model.content.ContentTable;
import de.woody64k.services.word.model.content.WordContent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class BodyElementParser {
    Integer headingNumId;

    public WordContent parseBodyElements(Collection<IBodyElement> bodyElements) {
        WordContent docContent = new WordContent();
        for (IBodyElement cnt : bodyElements) {
            switch (cnt.getElementType()) {
                case BodyElementType.TABLE: {
                    ContentTable contentTable = FlatTableParser.parseTable((XWPFTable) cnt);
                    if (contentTable.isFilled()) {
                        docContent.addTable(contentTable);
                    }
                    break;
                }
                case BodyElementType.PARAGRAPH: {
                    XWPFParagraph paragraph = (XWPFParagraph) cnt;
                    String text = paragraph.getParagraphText()
                            .trim();
                    if (text.isBlank()) {
                        // check for OLE Embedding
                        for (XWPFRun run : paragraph.getRuns()) {
                            docContent.addTables(OleTableParser.parseOLEObjects(run));
                        }
                    } else {
                        NumberingInformation headingLevel = getHeadingLevel(paragraph);
                        if (headingLevel == null || (headingNumId != null && !headingNumId.equals(headingLevel.getId()))) {
                            docContent.addText(text);
                        } else {
                            headingNumId = headingLevel.getId();
                            docContent.addBlock(text, headingLevel.getLevel());
                        }
                    }
                    break;
                }
                default: {
                    // Do nothing
                    log.info(String.format(" - Not used: %s", cnt.toString()));
                    break;
                }
            }
        }
        return docContent;
    }

    private NumberingInformation getHeadingLevel(XWPFParagraph paragraph) {
        if (paragraph.getNumID() != null) {
            int id = paragraph.getNumID()
                    .intValue();
            int lvl = paragraph.getNumIlvl()
                    .intValue();
            return NumberingInformation.create(id, ++lvl);
        }
        return null; // Means no heading
    }

    @Data
    public static class NumberingInformation {
        int level;
        int id;

        public static NumberingInformation create(int id, int level) {
            NumberingInformation num = new NumberingInformation();
            num.setId(id);
            num.setLevel(level);
            return num;
        }
    }
}
