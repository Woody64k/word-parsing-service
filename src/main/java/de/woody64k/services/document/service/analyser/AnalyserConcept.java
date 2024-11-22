package de.woody64k.services.document.service.analyser;

import de.woody64k.services.document.model.content.DocumentContent;
import de.woody64k.services.document.model.value.response.GenericObject;

public interface AnalyserConcept {
    public GenericObject analyse(DocumentContent parsedData, String condition);
}
