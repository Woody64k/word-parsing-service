package de.woody64k.services.word.service.analyser;

import de.woody64k.services.word.model.value.request.SearchRequirement;
import de.woody64k.services.word.model.value.response.GenericObject;

public class DefaultValueSetter {
    public static GenericObject setDefaultValue(GenericObject object, SearchRequirement requirement) {
        GenericObject newDefaultObject = new GenericObject();
        if (object.get(requirement.getResultName()) == null && requirement.getDefaultVaule() != null) {
            newDefaultObject.put(requirement.getResultName(), requirement.getDefaultVaule());
        }
        return newDefaultObject;
    }
}
