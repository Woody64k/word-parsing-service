package de.woody64k.services.word.model.value.request.transform;

import java.util.Map;

import lombok.Data;

@Data
public class SplitTransform {
    String by;
    SplitAs as;
    Map<Integer, String> values;

    public static enum SplitAs {
        list, values;
    }
}
