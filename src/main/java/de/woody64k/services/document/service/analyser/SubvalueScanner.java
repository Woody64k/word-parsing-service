package de.woody64k.services.document.service.analyser;

import java.util.Collection;

import de.woody64k.services.document.model.value.request.SearchRequirement;
import de.woody64k.services.document.model.value.response.GenericObject;

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
                    newData.putAllAndFlatten(scannSubdata(oneFound, valueMappings), true);
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
            response.putAllAndFlatten(DouplepointValueAnalyser.scanCell(foundData, condition), true);
        }
        return response;
    }
}
