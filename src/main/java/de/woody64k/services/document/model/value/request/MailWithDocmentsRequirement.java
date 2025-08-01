package de.woody64k.services.document.model.value.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MailWithDocmentsRequirement extends DocumentValueRequirement {
    String fileNameFilter;
    DocumentValueRequirement mail = new DocumentValueRequirement();
    DocumentValueRequirement document = new DocumentValueRequirement();
}
