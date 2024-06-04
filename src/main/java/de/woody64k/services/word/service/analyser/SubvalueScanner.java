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
    public static Object scannForSubvalues(Object foundData, Collection<SearchRequirement> valueMappings) {
        if (valueMappings != null && valueMappings.size() > 0) {
            if (foundData instanceof Collection) {
                GenericObject newData = new GenericObject();
                for (String oneFound : (Collection<String>) foundData) {
                    newData.putAll(scannSubdata(oneFound, valueMappings));
                }
                return newData;
            } else {
                return scannSubdata((String) foundData, valueMappings);
            }
        } else {
            return foundData;
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
