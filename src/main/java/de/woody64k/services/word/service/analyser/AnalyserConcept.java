package de.woody64k.services.word.service.analyser;

import de.woody64k.services.word.model.content.WordContent;
import de.woody64k.services.word.model.value.response.GenericObject;

public interface AnalyserConcept {
    public GenericObject analyse(WordContent parsedData, String condition);
}
