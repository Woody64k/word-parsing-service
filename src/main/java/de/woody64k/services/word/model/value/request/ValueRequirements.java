package de.woody64k.services.word.model.value.request;

import java.util.Set;

import lombok.Data;

@Data
public class ValueRequirements {
    Set<SearchRequirement> values;
}
