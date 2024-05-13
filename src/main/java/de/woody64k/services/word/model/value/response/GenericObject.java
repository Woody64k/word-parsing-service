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
        if (key != null) {
            GenericObject val = new GenericObject();
            val.put(key, value);
            return val;
        } else if (value instanceof GenericObject) {
            return (GenericObject) value;
        } else if (value instanceof List && ((List) value).size() == 1) {
            return (GenericObject) ((List) value).get(0);
        } else {
            throw new RuntimeException("Empty keys are only allowed if the value is a generic object.");
        }
    }

    @Override
    public Object put(String key, Object object) {
        if (key != null) {
            if (containsKey(key)) {
                return super.put(key, handleCollision(key, flattenSingleLists(object)));
            } else {
                return super.put(key, flattenSingleLists(object));
            }
        } else if (object instanceof GenericObject) {
            putAll((GenericObject) object);
            return object;
        } else {
            throw new RuntimeException("Empty keys are only allowed if the value is a list.");
        }
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

    /**
     * Returns an object only containing the listed keys.
     * 
     * @param keys
     * @return
     */
    public GenericObject sliceOut(Collection<String> keys) {
        GenericObject slicedObject = new GenericObject();
        for (String key : keys) {
            if (containsKey(key)) {
                slicedObject.put(key, get(key));
            }
        }
        return slicedObject;
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
            } else if (object instanceof Collection) {
                results.add(existingObject);
                results.addAll((Collection) object);
            } else if (object instanceof GenericObject) {
                results.add(object);
            } else {
                results.add(object);
                log.warn(String.format("Unsolved Collision for %s", key));
            }
        } else if (existingObject instanceof Collection) {
            if (object instanceof String) {
                results.addAll((Collection) existingObject);
                results.add(object);
            } else if (object instanceof Collection) {
                results.addAll((Collection) existingObject);
                results.addAll((Collection) object);
            } else if (object instanceof GenericObject) {
                results.add(object);
            } else {
                results.add(object);
                log.warn(String.format("Unsolved Collision for %s", key));
            }
        } else if (existingObject instanceof GenericObject) {
            if (object instanceof GenericObject) {
                results.add(existingObject);
                results.add(object);
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

    public boolean equalsByKey(GenericObject mergedResult, List<String> mergeKey) {
        for (String key : mergeKey) {
            if ((containsKey(key) && !get(key).equals(mergedResult.get(key))) || (!containsKey(key) && mergedResult.containsKey(key))) {
                return false;
            }
        }
        return true;
    }
}
