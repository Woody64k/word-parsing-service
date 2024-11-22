package de.woody64k.services.document.service;

import java.util.Collection;

import org.springframework.stereotype.Service;

import de.woody64k.services.document.model.content.DocumentContent;
import de.woody64k.services.document.model.value.request.DocumentValueRequirement;
import de.woody64k.services.document.model.value.request.ListRequirement;
import de.woody64k.services.document.model.value.request.SearchRequirement;
import de.woody64k.services.document.model.value.response.WordValues;
import de.woody64k.services.document.service.analyser.ChapterAnalyser;
import de.woody64k.services.document.service.analyser.DefaultValueSetter;
import de.woody64k.services.document.service.analyser.DouplepointValueAnalyser;
import de.woody64k.services.document.service.analyser.FullPlaintextAnalyser;
import de.woody64k.services.document.service.analyser.HeadingColumnAnalyser;
import de.woody64k.services.document.service.analyser.HeadingRowAnalyser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DocumentAnalyser {

    public WordValues getValues(DocumentContent parsedData, DocumentValueRequirement request) {
        WordValues result = new WordValues();
        result.integrate(parseFlatValues(parsedData, request.getValues()));
        for (ListRequirement listRequ : request.getLists()) {
            result.addValues(listRequ.getResultName(), HeadingRowAnalyser.analyse(parsedData, listRequ));
        }
        return result;
    }

    private WordValues parseFlatValues(DocumentContent parsedData, Collection<SearchRequirement> searchRequ) {
        WordValues result = new WordValues();
        for (SearchRequirement condition : searchRequ) {
            result.integrate(DouplepointValueAnalyser.analyse(parsedData, condition));
            result.integrate(HeadingColumnAnalyser.analyse(parsedData, condition));
            result.integrate(ChapterAnalyser.analyse(parsedData, condition));
            result.integrate(DefaultValueSetter.setDefaultValue(result.getData(), condition));
            result.integrate(FullPlaintextAnalyser.analyse(parsedData, condition));
        }
        return result;
    }
}
