package de.woody64k.services.document.model.content.elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import de.woody64k.services.document.util.Checker;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ParsedTable extends ArrayList<ParsedTableRow> {
    public ParsedTableRow newRow() {
        ParsedTableRow row = new ParsedTableRow();
        add(row);
        return row;
    }

    /**
     * Deletes all columns, which are compleatly empty.
     * 
     * @return the indexed of the keeped ones
     */
    public Set<Integer> removeEmptyColumns() {
        Set<Integer> columnsToKeep = getFilledColumns();
        for (ParsedTableRow row : this) {
            for (int i = row.size() - 1; i >= 0; i--) {
                if (!columnsToKeep.contains(i)) {
                    row.remove(i);
                }
            }
        }
        return columnsToKeep;
    }

    /**
     * Deletes all columns, which are compleatly empty.
     * 
     * @return the indexed of the keept ones
     */
    public Set<Integer> removeEmptyRows() {
        Set<Integer> rowsKeept = new HashSet<>();
        for (int i = this.size() - 1; i >= 0; i--) {
            ParsedTableRow row = this.get(i);
            if (row.isBlank()) {
                this.remove(i);
            } else {
                rowsKeept.add(i);
            }
        }
        return rowsKeept;
    }

    public Set<Integer> getFilledColumns() {
        Set<Integer> columnsToKeep = new HashSet<Integer>();
        for (ParsedTableRow row : this) {
            for (int i = 0; i < row.size(); i++) {
                if (Checker.isNotEmpty(row.get(i))) {
                    columnsToKeep.add(i);
                }
            }
        }
        return columnsToKeep;
    }

    public int maxWith() {
        int max = 0;
        for (ParsedTableRow row : this) {
            if (max < row.size()) {
                max = row.size();
            }
        }
        return max;
    }

    public Map<Integer, DescriptiveStatistics> calculateColumnStatistics() {
        Map<Integer, DescriptiveStatistics> statistics = new LinkedHashMap<>();
        for (ParsedTableRow row : this) {
            for (int i = 0; i < row.size(); i++) {
                if (!statistics.containsKey(i)) {
                    statistics.put(i, new DescriptiveStatistics());
                }
                statistics.get(i)
                        .addValue(row.get(i)
                                .toString()
                                .length());
            }
        }
        return statistics;
    }

    public void append(ParsedTable table) {
        addAll(table);
    }
}
