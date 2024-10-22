package de.woody64k.services.word.util;

import java.util.Collection;

import de.woody64k.services.word.model.value.response.GenericObject;

public class Checker {
    public static boolean isEmpty(String text) {
        return text == null || text.isBlank();
    }

    public static boolean isNotEmpty(String text) {
        return !isEmpty(text);
    }

    public static boolean isEmpty(Collection<?> list) {
        return list == null || list.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> list) {
        return !isEmpty(list);
    }

    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else {
            if (obj instanceof String) {
                return isEmpty((String) obj);
            } else if (obj instanceof Collection) {
                return isEmpty(obj);
            } else if (obj instanceof GenericObject) {
                return false;
            }
        }
        return false;
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }
}
