package de.woody64k.services.word.model.value.response;

import lombok.Data;

@Data
public class WordValues {
    GenericValue data = new GenericValue();

    public void addValue(String key, String value) {
        data.put(key, value);
    }

    public void integrate(String key, WordValues values) {
        data.put(key, values.getData());
    }

    /**
     * Creates or deliveres a Value List.
     * 
     * @param key
     * @return
     */
    public GenericValue getList(String key) {
        return data.getList(key);
    }

}
