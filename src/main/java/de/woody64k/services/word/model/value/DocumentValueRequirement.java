package de.woody64k.services.word.model.value;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class DocumentValueRequirement {
    List<String> values = new ArrayList<>();
    List<ListRequirement> lists = new ArrayList<>();
}
