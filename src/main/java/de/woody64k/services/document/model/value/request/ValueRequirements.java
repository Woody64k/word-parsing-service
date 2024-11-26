package de.woody64k.services.document.model.value.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ValueRequirements {
    List<SearchRequirement> values = new ArrayList<>();
}
