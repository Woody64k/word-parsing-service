package de.woody64k.services.word.model.content;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContentBlock extends WordContent {
    private final ContentCategory contentCategory = ContentCategory.BLOCK;
    private String heading;
    private int level;

    @Override
    public List<IContent> getAllByCathegory(ContentCategory category) {
        List<IContent> results = new ArrayList<>();
        for (IContent content : this) {
            if (content.getContentCategory().equals(category)) {
                results.add(content);
            }
            results.addAll(content.getAllByCathegory(category));
        }
        return results;
    }

    public enum TABLE_TYPE {
        FLAT, OLE
    }
}
