package de.woody64k.services.word.model.value.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DocumentValueRequirement extends ValueRequirements {
    List<ListRequirement> lists = new ArrayList<>();
}
