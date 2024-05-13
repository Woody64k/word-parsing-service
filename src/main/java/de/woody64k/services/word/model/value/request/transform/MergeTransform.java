package de.woody64k.services.word.model.value.request.transform;

import java.util.List;

import lombok.Data;

@Data
public class MergeTransform {
    List<String> by;
    List<String> collect;
}
