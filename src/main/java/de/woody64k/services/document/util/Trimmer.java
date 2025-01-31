package de.woody64k.services.document.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class Trimmer {

    public static Collection<String> trimmAll(String[] values) {
        return trimmAll(Arrays.asList(values));
    }

    public static Collection<String> trimmAll(Collection<String> values) {
        return values.stream().map(String::trim).collect(Collectors.toList());
    }
}
