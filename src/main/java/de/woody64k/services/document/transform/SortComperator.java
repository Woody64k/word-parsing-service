package de.woody64k.services.document.transform;

import java.util.Comparator;

import de.woody64k.services.document.model.value.response.GenericObject;
import de.woody64k.services.document.util.Checker;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SortComperator implements Comparator<GenericObject> {
    String[] criteria;

    @Override
    public int compare(GenericObject o1, GenericObject o2) {
        for (String criterium : criteria) {
            int res = compateObject(o1, o2, criterium);
            if (res != 0) {
                return res;
            }
        }
        return 0;
    }

    public int compateObject(GenericObject o1, GenericObject o2, String criterium) {
        if (Checker.isEmpty(o1.get(criterium))) {
            if (Checker.isEmpty(o2.get(criterium))) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if (Checker.isEmpty(o2.get(criterium))) {
                return -1;
            } else {
                return compareValue(o1.get(criterium), o2.get(criterium));
            }
        }
    }

    private int compareValue(Object object, Object object2) {
        if (object instanceof String && object2 instanceof String) {
            return ((String) object).compareTo((String) object2);
        } else {
            return 0;
        }
    }

}
