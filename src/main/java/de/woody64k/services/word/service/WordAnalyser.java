package de.woody64k.services.word.service;

import java.util.Collection;

import org.springframework.stereotype.Service;

import de.woody64k.services.word.model.content.WordContent;
import de.woody64k.services.word.model.value.request.DocumentValueRequirement;
import de.woody64k.services.word.model.value.request.ListRequirement;
import de.woody64k.services.word.model.value.request.SearchRequirement;
import de.woody64k.services.word.model.value.response.GenericObject;
import de.woody64k.services.word.model.value.response.WordValues;
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
            result.addValues(listRequ.getName(), HeadingRowAnalyser.analyse(parsedData, listRequ));
        }
        return result;
    }

    private WordValues parseFlatValues(WordContent parsedData, Collection<SearchRequirement> searchRequ) {
        WordValues result = new WordValues();
        for (SearchRequirement condition : searchRequ) {
            result.integrate(HeadingColumnAnalyser.analyse(parsedData, condition));
            result.integrate(DouplepointValueAnalyser.analyse(parsedData, condition));
            result.integrate(scannForSubvalues(result.getData(), condition));
        }
        return result;
    }

    /**
     * Scanns for subvalues only one level.
     * 
     * @implements FR-07
     * @param result
     * @param condition
     * @return
     */
    public GenericObject scannForSubvalues(GenericObject result, SearchRequirement condition) {
        if (condition.getValues() != null && condition.getValues().size() > 0) {
            Object foundData = result.get(condition.getResultName());
            if (foundData instanceof Collection) {
                GenericObject newData = new GenericObject();
                for (String oneFound : (Collection<String>) foundData) {
                    GenericObject newObject = scannSubdata(oneFound, condition.getValues());
                    newData.put(condition.getResultName(), newObject);
                }
                return newData;
            } else {
                return GenericObject.create(condition.getResultName(), scannSubdata((String) foundData, condition.getValues()));
            }
        } else {
            return new GenericObject();
        }
    }

    private GenericObject scannSubdata(String foundData, Collection<SearchRequirement> searchRequ) {
        GenericObject response = new GenericObject();
        for (SearchRequirement condition : searchRequ) {
            response.putAll(DouplepointValueAnalyser.scanCell(foundData, condition));
        }
        return response;
    }
}
