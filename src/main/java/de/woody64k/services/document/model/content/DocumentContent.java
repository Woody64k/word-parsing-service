package de.woody64k.services.document.model.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({ "contentCategory", "content" })
public class DocumentContent implements IContent {
    @JsonIgnore
    private static final long serialVersionUID = 1L;
    private ArrayList<IContent> content = new ArrayList<>();

    @JsonIgnore
    private String flatString;

    @Override
    public ContentCategory getContentCategory() {
        return ContentCategory.DOCUMENT;
    }

    public void add(IContent contentBlock) {
        content.add(contentBlock);
    }

    public void addTable(ContentTable table) {
        add(table);
    }

    public void addTables(Collection<ContentTable> tables) {
        getContent().addAll(tables);
    }

    public void addText(String text) {
        if (text != null && !text.isBlank()) {
            add(ContentText.create(text));
        }
    }

    @JsonIgnore
    public List<ContentTable> getTables() {
        List<IContent> content = getAllByCategory(ContentCategory.TABLE);
        return content.stream()
                .map(block -> (ContentTable) block)
                .collect(Collectors.toList());
    }
    // END of Legacy Stuff

    @Override
    public List<IContent> getAllByCategory(ContentCategory category) {
        List<IContent> results = new ArrayList<>();
        for (IContent content : getContent()) {
            if (content.getContentCategory()
                    .equals(category)) {
                results.add(content);
            }
            results.addAll(content.getAllByCategory(category));
        }
        return results;
    }

    public boolean isOnlyText() {
        for (IContent element : getContent()) {
            if (!element.getContentCategory()
                    .equals(ContentCategory.TEXT)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String flattenToString() {
        if (flatString == null) {
            List<String> paragraphs = getContent().stream()
                    .map(x -> x.flattenToString()
                            .trim())
                    .collect(Collectors.toList());
            flatString = String.join("\n", paragraphs);
        }
        return flatString;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return content.isEmpty();
    }
}
