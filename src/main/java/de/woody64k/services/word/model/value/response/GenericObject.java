package de.woody64k.services.word.model.value.response;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * Generic Map which holds Values or another Generic Map.
 */
@Slf4j
public class GenericObject extends LinkedHashMap<String, Object> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static GenericObject create(String key, Object value) {
        GenericObject val = new GenericObject();
        val.put(key, value);
        return val;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        for (String key : m.keySet()) {
            if (containsKey(key)) {
                put(key, handleCollision(key, flattenSingleLists(m.get(key))));
            } else {
                put(key, flattenSingleLists(m.get(key)));
            }
        }
    }

    private Object handleCollision(String key, Object object) {
        Object existingObject = flattenSingleLists(get(key));
        Set<Object> results = new HashSet<>();
        if (existingObject instanceof String) {
            if (object instanceof String) {
                if (!existingObject.equals(object)) {
                    results.add(existingObject);
                    results.add(object);
                } else {
                    results.add(existingObject);
                }
            } else if (object instanceof List) {
                results.add(existingObject);
                results.addAll((List) object);
            } else {
                results.add(object);
                log.warn(String.format("Unsolved Collision for %s", key));
            }
        } else if (existingObject instanceof List) {
            if (object instanceof String) {
                results.addAll((List) existingObject);
                results.add(object);
            } else if (object instanceof List) {
                results.addAll((List) existingObject);
                results.addAll((List) object);
            } else {
                results.add(object);
                log.warn(String.format("Unsolved Collision for %s", key));
            }
        } else {
            results.add(object);
            log.warn(String.format("Unsolved Collision for %s", key));
        }
        return flattenSingleLists(results);
    }

    /**
     * Avoids empty arrays and single value lists.
     * 
     * @implements FS-04
     * @param results
     * @return
     */
    public Object flattenSingleLists(Object results) {
        if (results instanceof Collection) {
            Collection coll = (Collection) results;
            if (coll.size() == 0) {
                return null;
            } else if (coll.size() == 1) {
                return coll.iterator().next();
            } else {
                return coll;
            }
        } else {
            return results;
        }
    }

    /**
     * Creates or deliveres a Value List.
     * 
     * @param key
     * @return
     */
    public GenericObject getList(String key) {
        if (containsKey(key)) {
            if (get(key) instanceof GenericObject) {
                return (GenericObject) get(key);
            } else {
                throw new RuntimeException(String.format("Try to overwrite %s (value: %s) with an Empty List-Type.", key, get(key)));
            }
        } else {
            GenericObject values = new GenericObject();
            put(key, values);
            return values;
        }
    }
}
