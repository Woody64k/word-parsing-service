package de.woody64k.services.word.model.content;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@JsonPropertyOrder({ "heading", "level", "contentCategory", "content" })
@EqualsAndHashCode(callSuper = true)
public class ContentBlock extends WordContent {
    private String heading;
    private int level;

    public static ContentBlock create(String heading, int level) {
        ContentBlock contentBlock = new ContentBlock();
        contentBlock.setHeading(heading);
        contentBlock.setLevel(level);
        return contentBlock;
    }

    @Override
    public ContentCategory getContentCategory() {
        return ContentCategory.BLOCK;
    }

    @Override
    public List<IContent> getAllByCathegory(ContentCategory category) {
        List<IContent> results = new ArrayList<>();
        for (IContent contentBlock : getContent()) {
            if (contentBlock.getContentCategory().equals(category)) {
                results.add(contentBlock);
            }
            results.addAll(contentBlock.getAllByCathegory(category));
        }
        return results;
    }

    public enum TABLE_TYPE {
        FLAT, OLE
    }
}
