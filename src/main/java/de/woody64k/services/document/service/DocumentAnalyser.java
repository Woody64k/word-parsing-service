package de.woody64k.services.document.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import de.woody64k.services.document.model.content.DocumentContent;
import de.woody64k.services.document.model.content.IContent;
import de.woody64k.services.document.model.content.IContent.ContentCategory;
import de.woody64k.services.document.model.value.request.DocumentContainerRequirement;
import de.woody64k.services.document.model.value.request.DocumentValueRequirement;
import de.woody64k.services.document.model.value.request.ListRequirement;
import de.woody64k.services.document.model.value.request.SearchRequirement;
import de.woody64k.services.document.model.value.response.DocumentValues;
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

    public DocumentValues getValues(DocumentContent parsedData, DocumentValueRequirement request) {
        DocumentValues result = new DocumentValues();
        result.integrate(parseFlatValues(parsedData, request.getValues()));
        for (ListRequirement listRequ : request.getLists()) {
            result.addValues(listRequ.getResultName(), HeadingRowAnalyser.analyse(parsedData, listRequ));
        }
        return result;
    }

    private DocumentValues parseFlatValues(DocumentContent parsedData, Collection<SearchRequirement> searchRequ) {
        DocumentValues result = new DocumentValues();
        for (SearchRequirement condition : searchRequ) {
            result.integrate(DouplepointValueAnalyser.analyse(parsedData, condition));
            result.integrate(HeadingColumnAnalyser.analyse(parsedData, condition));
            result.integrate(ChapterAnalyser.analyse(parsedData, condition));
            result.integrate(DefaultValueSetter.setDefaultValue(result.getData(), condition));
            result.integrate(FullPlaintextAnalyser.analyse(parsedData, condition));
        }
        return result;
    }

    public DocumentValues getValuesFromContainer(DocumentContent parsedData, DocumentContainerRequirement request) {
        DocumentValues container = getValues(parsedData, request);
        List<DocumentValues> containedDocuments = new ArrayList<>();
        for (IContent document : parsedData.getAllByCategory(ContentCategory.DOCUMENT)) {
            containedDocuments.add(getValues((DocumentContent) document, request.getDocuments()));
        }
        container.getData()
                .put("documents", containedDocuments);
        return container;
    }
}
