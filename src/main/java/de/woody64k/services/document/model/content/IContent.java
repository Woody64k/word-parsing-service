package de.woody64k.services.document.model.content;

import java.util.ArrayList;
import java.util.List;

public interface IContent {
    public ContentCategory getContentCategory();

    public default List<IContent> getAllByCategory(ContentCategory category) {
        return new ArrayList<>();
    }

    public String flattenToString();

    public static enum ContentCategory {
        TEXT, TABLE, DOCUMENT
    }
}
