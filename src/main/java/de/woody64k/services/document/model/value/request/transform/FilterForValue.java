package de.woody64k.services.document.model.value.request.transform;

import lombok.Data;

@Data
public class FilterForValue {
    protected String value;
    protected String equals;
    protected Boolean isEmpty;
}
