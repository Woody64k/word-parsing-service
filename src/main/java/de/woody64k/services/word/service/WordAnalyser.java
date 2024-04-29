package de.woody64k.services.word.service;

import org.springframework.stereotype.Service;

import de.woody64k.services.word.model.content.WordContent;
import de.woody64k.services.word.model.value.DocumentValueRequirement;
import de.woody64k.services.word.model.value.ListRequirement;
import de.woody64k.services.word.model.value.response.WordValues;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WordAnalyser {

    public WordValues getValues(WordContent parsedData, DocumentValueRequirement request) {
        WordValues result = new WordValues();
        for (String value : request.getValues()) {
            // TODO: Collect Values.
            result.addValue(value, null);
        }
        for (ListRequirement listRequ : request.getLists()) {
            result.integrate(listRequ.getName(), getValues(parsedData, listRequ));
        }
        return result;
    }
}
