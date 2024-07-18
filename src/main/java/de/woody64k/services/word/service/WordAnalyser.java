package de.woody64k.services.word.service;

import java.util.Collection;

import org.springframework.stereotype.Service;

import de.woody64k.services.word.model.content.WordContent;
import de.woody64k.services.word.model.value.request.DocumentValueRequirement;
import de.woody64k.services.word.model.value.request.ListRequirement;
import de.woody64k.services.word.model.value.request.SearchRequirement;
import de.woody64k.services.word.model.value.response.WordValues;
import de.woody64k.services.word.service.analyser.DefaultValueSetter;
import de.woody64k.services.word.service.analyser.DouplepointValueAnalyser;
import de.woody64k.services.word.service.analyser.HeadingColumnAnalyser;
import de.woody64k.services.word.service.analyser.HeadingRowAnalyser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WordAnalyser {

    public WordValues getValues(WordContent parsedData, DocumentValueRequirement request) {
        WordValues result = new WordValues();
        result.integrate(parseFlatValues(parsedData, request.getValues()));
        for (ListRequirement listRequ : request.getLists()) {
            result.addValues(listRequ.getResultName(), HeadingRowAnalyser.analyse(parsedData, listRequ));
        }
        return result;
    }

    private WordValues parseFlatValues(WordContent parsedData, Collection<SearchRequirement> searchRequ) {
        WordValues result = new WordValues();
        for (SearchRequirement condition : searchRequ) {
            result.integrate(HeadingColumnAnalyser.analyse(parsedData, condition));
            result.integrate(DouplepointValueAnalyser.analyse(parsedData, condition));
            result.integrate(DefaultValueSetter.setDefaultValue(result.getData(), condition));
        }
        return result;
    }
}
