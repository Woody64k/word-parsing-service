package de.woody64k.services.word.model.value;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListRequirement extends DocumentValueRequirement {
    String name;
}
