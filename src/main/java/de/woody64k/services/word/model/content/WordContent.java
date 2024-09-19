package de.woody64k.services.word.model.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({ "contentCategory", "content" })
public class WordContent implements IContent {
    @JsonIgnore
    private static final long serialVersionUID = 1L;
    @JsonIgnore
    private Stack<ContentBlock> chapterStack = new Stack<>();
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
        getBlock().add(table);
    }

    public void addTables(Collection<ContentTable> tables) {
        getBlock().getContent()
                .addAll(tables);
    }

    public void addText(String text) {
        getBlock().add(ContentText.create(text));
    }

    public void addBlock(String text, Integer headingLevel) {
        ContentBlock block = ContentBlock.create(text, headingLevel);
        for (; !chapterStack.isEmpty() && chapterStack.lastElement()
                .getLevel() >= block.getLevel(); chapterStack.removeLast())
            ;
        if (chapterStack.isEmpty()) {
            content.add(block);
        } else {
            chapterStack.lastElement()
                    .add(block);
        }
        chapterStack.push(block);
    }

    private WordContent getBlock() {
        if (chapterStack.isEmpty()) {
            return this;
        } else {
            return chapterStack.lastElement();
        }
    }

    @JsonIgnore
    public List<ContentTable> getTables() {
        List<IContent> content = getAllByCathegory(ContentCategory.TABLE);
        return content.stream()
                .map(block -> (ContentTable) block)
                .collect(Collectors.toList());
    }
    // END of Legacy Stuff

    @Override
    public List<IContent> getAllByCathegory(ContentCategory category) {
        List<IContent> results = new ArrayList<>();
        for (IContent content : getContent()) {
            if (content.getContentCategory()
                    .equals(category)) {
                results.add(content);
            }
            results.addAll(content.getAllByCathegory(category));
        }
        return results;
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
        return chapterStack.isEmpty() && content.isEmpty();
    }
}
