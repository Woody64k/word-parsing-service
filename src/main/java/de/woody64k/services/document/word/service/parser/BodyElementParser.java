package de.woody64k.services.document.word.service.parser;

import java.util.Collection;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import de.woody64k.services.document.model.content.ContentTable;
import de.woody64k.services.document.model.content.DocumentContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BodyElementParser {
    Integer headingNumId;

    public DocumentContent parseBodyElements(Collection<IBodyElement> bodyElements) {
        DocumentContent docContent = new DocumentContent();
        for (IBodyElement cnt : bodyElements) {
            switch (cnt.getElementType()) {
                case BodyElementType.TABLE: {
                    ContentTable contentTable = FlatTableParser.parseTable((XWPFTable) cnt, headingNumId);
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
                        docContent.addText(text);
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
}
