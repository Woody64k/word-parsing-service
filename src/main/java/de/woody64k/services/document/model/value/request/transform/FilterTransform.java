package de.woody64k.services.document.model.value.request.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FilterTransform extends FilterForValue {
    List<FilterTransform> and;
    List<FilterTransform> or;

    public Collection<FilterTransform> getAllSubRules() {
        if (and != null) {
            return and;
        } else if (or != null) {
            return or;
        } else {
            return new ArrayList<>();
        }
    }
}
