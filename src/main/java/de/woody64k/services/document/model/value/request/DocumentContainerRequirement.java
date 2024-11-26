package de.woody64k.services.document.model.value.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode()
public class DocumentContainerRequirement extends DocumentValueRequirement {
    DocumentValueRequirement documents = new DocumentValueRequirement();
}
