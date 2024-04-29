package de.woody64k.services.word.model.value.response;

import java.util.HashMap;

/**
 * Generic Map which holds Values or another Generic Map.
 */
public class GenericValue extends HashMap<String, Object> {

    /**
     * Creates or deliveres a Value List.
     * 
     * @param key
     * @return
     */
    public GenericValue getList(String key) {
        if (containsKey(key)) {
            if (get(key) instanceof GenericValue) {
                return (GenericValue) get(key);
            } else {
                throw new RuntimeException(String.format("Try to overwrite %s (value: %s) with an Empty List-Type.", key, get(key)));
            }
        } else {
            GenericValue values = new GenericValue();
            put(key, values);
            return values;
        }
    }
}
