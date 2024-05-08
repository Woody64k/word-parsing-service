package de.woody64k.services.word.model.value.request;

import lombok.Data;

@Data
public class SearchRequirement extends ValueRequirements {
    String searchTerm;
    String resultName;
}
