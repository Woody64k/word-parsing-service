package de.woody64k.services.word.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.woody64k.services.word.model.content.ContentTable;
import de.woody64k.services.word.model.content.WordContent;
import de.woody64k.services.word.service.parser.FlatTableParser;
import de.woody64k.services.word.service.parser.OleTableParser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WordParser {

    public WordContent parseConent(MultipartFile uploadFile) {
        WordContent docContent = new WordContent();
        Integer headingNumId = null;
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(uploadFile.getBytes()))) {
            for (IBodyElement cnt : doc.getBodyElements()) {
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
                        String text = paragraph.getParagraphText().trim();
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
                    default:
                        // Do nothing
                        log.info(String.format(" - Not used: %s", cnt.toString()));
                        break;
                }
            }
            return docContent;
        } catch (IOException e) {
            log.error("Error while parse Document.", e);
            throw new RuntimeException(e);
        }
    }

    private NumberingInformation getHeadingLevel(XWPFParagraph paragraph) {
        String styleName = paragraph.getStyle();
        if (paragraph.getNumID() != null) {
            int id = paragraph.getNumID().intValue();
            int lvl = paragraph.getNumIlvl().intValue();
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