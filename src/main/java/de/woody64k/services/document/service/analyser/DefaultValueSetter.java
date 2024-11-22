package de.woody64k.services.document.service.analyser;

import de.woody64k.services.document.model.value.request.SearchRequirement;
import de.woody64k.services.document.model.value.response.GenericObject;

public class DefaultValueSetter {
    public static GenericObject setDefaultValue(GenericObject object, SearchRequirement requirement) {
        GenericObject newDefaultObject = new GenericObject();
        if (object.get(requirement.getResultName()) == null && requirement.getDefaultValue() != null) {
            newDefaultObject.put(requirement.getResultName(), requirement.getDefaultValue());
        }
        return newDefaultObject;
    }
}
