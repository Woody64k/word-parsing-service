package de.woody64k.services.document.model.value.response;

import java.util.List;

import lombok.Data;

@Data
public class WordValues {
    GenericObject data = new GenericObject();

    public void addValue(String key, String value) {
        data.put(key, value);
    }

    public void integrate(WordValues values) {
        data.putAll(values.getData());
    }

    public void integrate(GenericObject values) {
        data.putAll(values);
    }

    public void addValues(String key, List<GenericObject> values) {
        data.put(key, values);
    }

    /**
     * Creates or deliveres a Value List.
     * 
     * @param key
     * @return
     */
    public GenericObject getList(String key) {
        return data.getList(key);
    }

}
