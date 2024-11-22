package de.woody64k.services.document.model.content;

import java.util.ArrayList;
import java.util.List;

public interface IContent {
    public ContentCategory getContentCategory();

    public default List<IContent> getAllByCathegory(ContentCategory category) {
        return new ArrayList<>();
    }

    public String flattenToString();

    public static enum ContentCategory {
        BLOCK, TEXT, TABLE, DOCUMENT
    }
}
