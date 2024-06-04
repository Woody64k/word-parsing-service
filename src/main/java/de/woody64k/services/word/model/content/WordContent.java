package de.woody64k.services.word.model.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WordContent extends ArrayList<IContent> implements IContent {
    @JsonIgnore
    private static final long serialVersionUID = 1L;

    // BEGIN of Legacy stuff
    private List<ContentTable> tables = new ArrayList<>();

    public void addTable(ContentTable table) {
        tables.add(table);
    }

    public void addTables(Collection<ContentTable> table) {
        tables.addAll(table);
    }

    public List<ContentTable> getTable() {
        List<IContent> content = getAllByCathegory(ContentCategory.TABLE);
        return content.stream().map(block -> (ContentTable) block).collect(Collectors.toList());
    }
    // END of Legacy Stuff

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

    @Override
    public ContentCategory getContentCategory() {
        return null;
    }
}
