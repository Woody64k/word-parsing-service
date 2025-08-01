package de.woody64k.services.document.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import de.woody64k.services.document.model.content.DocumentContent;
import de.woody64k.services.document.model.content.IContent;
import de.woody64k.services.document.model.content.IContent.ContentCategory;
import de.woody64k.services.document.model.value.request.DocumentValueRequirement;
import de.woody64k.services.document.model.value.request.ListRequirement;
import de.woody64k.services.document.model.value.request.MailWithDocmentsRequirement;
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
            result.integrate(DefaultValueSetter.setDefaultValue(result, condition));
            result.integrate(FullPlaintextAnalyser.analyse(parsedData, condition));
        }
        return result;
    }

    public DocumentValues getValuesFromContainer(DocumentContent parsedData, MailWithDocmentsRequirement request) {
        DocumentValues container = getValues(parsedData.newDocWith(ContentCategory.TABLE, ContentCategory.TEXT), request.getMail());
        List<DocumentValues> containedDocuments = new ArrayList<>();
        for (IContent documentContent : parsedData.filterFor(ContentCategory.DOCUMENT)) {
            DocumentContent document = (DocumentContent) documentContent;
            if (request.getFileNameFilter() == null || document.getFileName()
                    .matches(request.getFileNameFilter())) {
                containedDocuments.add(getValues(document, request.getDocument()));
            }
        }
        container.put("documents", containedDocuments);
        return container;
    }
}
