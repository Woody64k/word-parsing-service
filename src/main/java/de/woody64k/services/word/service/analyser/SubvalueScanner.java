package de.woody64k.services.word.service.analyser;

import java.util.Collection;

import de.woody64k.services.word.model.value.request.SearchRequirement;
import de.woody64k.services.word.model.value.response.GenericObject;

public class SubvalueScanner {
    /**
     * Scanns for subvalues only one level.
     * 
     * @implements FR-07
     * @param result
     * @param condition
     * @return
     */
    public static Object scannForSubvalues(Object foundData, SearchRequirement condition) {
        if (condition.getValues() != null && condition.getValues().size() > 0) {
            if (foundData instanceof Collection) {
                GenericObject newData = new GenericObject();
                for (String oneFound : (Collection<String>) foundData) {
                    newData.putAll(scannOneSubEntity(oneFound, condition));
                }
                return newData;
            } else {
                return scannOneSubEntity((String) foundData, condition);
            }
        } else {
            return foundData;
        }
    }

    public static GenericObject scannOneSubEntity(String oneFound, SearchRequirement condition) {
        GenericObject newObject = scannSubdata(oneFound, condition.getValues());
        if (condition.getResultName() != null) {
            return GenericObject.create(condition.getResultName(), newObject);
        } else {
            return newObject;
        }
    }

    private static GenericObject scannSubdata(String foundData, Collection<SearchRequirement> searchRequ) {
        GenericObject response = new GenericObject();
        for (SearchRequirement condition : searchRequ) {
            response.putAll(DouplepointValueAnalyser.scanCell(foundData, condition));
        }
        return response;
    }
}
