package de.woody64k.services.document.model.value.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode()
public class DocumentContainerRequirement {
    DocumentValueRequirement container = new DocumentValueRequirement();
    DocumentValueRequirement document = new DocumentValueRequirement();
}
