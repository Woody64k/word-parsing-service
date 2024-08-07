package de.woody64k.services.word.model.content;

import lombok.Data;

@Data
public class ContentText implements IContent {
    private String text;

    public static ContentText create(String text) {
        ContentText contentText = new ContentText();
        contentText.setText(text);
        return contentText;
    }

    @Override
    public ContentCategory getContentCategory() {
        return ContentCategory.TEXT;
    }
}
