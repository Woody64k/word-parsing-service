package de.woody64k.services.document.model.value.response;

import java.util.List;

import lombok.Data;

@Data
public class DocumentValues extends GenericObject {

    public void addValue(String key, String value) {
        put(key, value);
    }

    public void integrate(DocumentValues values) {
        putAll(values);
    }

    public void integrate(GenericObject values) {
        putAll(values);
    }

    public void addValues(String key, List<GenericObject> values) {
        put(key, values);
    }

    /**
     * Creates or deliveres a Value List.
     * 
     * @param key
     * @return
     */
    @Override
    public GenericObject getList(String key) {
        return getList(key);
    }

}
