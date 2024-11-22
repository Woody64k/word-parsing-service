package de.woody64k.services.document.model.value.request.transform;

import java.util.List;

import lombok.Data;

@Data
public class SubStringTransform {
    List<String> startWords;
    List<String> endWords;
}
