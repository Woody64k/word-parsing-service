package de.woody64k.services.word.model.value.request.transform;

import java.util.List;

import de.woody64k.services.word.model.value.request.SplitValueRequirement;
import lombok.Data;

@Data
public class SplitTransform {
    String by;
    SplitAs as;
    List<SplitValueRequirement> values;

    public static enum SplitAs {
        list, values;
    }
}
