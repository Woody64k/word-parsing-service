package de.woody64k.services.document.transform;

import org.apache.commons.lang3.StringUtils;

import de.woody64k.services.document.model.value.request.SplitValueRequirement;
import de.woody64k.services.document.model.value.request.transform.SubStringTransform;
import de.woody64k.services.document.model.value.request.transform.ValueTransformRequirement;
import de.woody64k.services.document.model.value.request.transform.SplitTransform.SplitAs;
import de.woody64k.services.document.model.value.response.GenericObject;
import de.woody64k.services.document.util.Trimmer;

public class ValueTransformer {
    public static Object transform(String value, ValueTransformRequirement requ) {
        if (requ != null) {
            String cuttedValue = doSubstring(value, requ.getSubString());
            return doSplit(cuttedValue, requ);
        } else {
            return value;
        }
    }

    public static String doSubstring(String value, SubStringTransform requ) {
        if (value != null && requ != null) {
            if (requ.getStartWords() != null) {
                for (String startWord : requ.getStartWords()) {
                    if (value.contains(startWord)) {
                        String textAfterStart = value.substring(value.indexOf(startWord) + startWord.length())
                                .trim();
                        return substringFromEnd(textAfterStart, requ);
                    }
                }
                // if startWord is not contained in text
                return null;
            } else {
                return substringFromEnd(value, requ);
            }
        }
        return value;
    }

    public static String substringFromEnd(String value, SubStringTransform requ) {
        if (requ.getEndWords() != null) {
            for (String endWord : requ.getEndWords()) {
                if (value.contains(endWord)) {
                    return value.substring(0, value.indexOf(endWord))
                            .trim();
                }
            }
        }
        return value;
    }

    /**
     * @implements FR-08, FR-09
     * @param value
     * @param requ
     * @return
     */
    public static Object doSplit(String value, ValueTransformRequirement requ) {
        if (value != null && requ.getSplit() != null) {
            String[] splittedValue = value.split(requ.getSplit()
                    .getBy());
            if (requ.getSplit()
                    .getAs() == SplitAs.list) {
                // @implements: FR-08
                return Trimmer.trimmAll(splittedValue);
            } else {
                // @implements: FR-09
                GenericObject splitResult = new GenericObject();
                for (SplitValueRequirement splitValRequ : requ.getSplit()
                        .getValues()) {
                    if (splitValRequ.getPosition() != null) {
                        if (splitValRequ.getPosition() <= splittedValue.length && splitValRequ.getPosition() >= 1) {
                            String usedPart = splittedValue[splitValRequ.getPosition() - 1].trim();
                            splitResult.put(splitValRequ.getResultName(), ValueTransformer.transform(usedPart, splitValRequ.getTransform()));
                        }
                    } else if (splitValRequ.getFromPosition() != null) {
                        int indexOfPosition = StringUtils.ordinalIndexOf(value, requ.getSplit()
                                .getBy(), splitValRequ.getFromPosition() - 1);
                        if (indexOfPosition >= 0) {
                            String usedPart = value.substring(indexOfPosition + 1)
                                    .trim();
                            splitResult.put(splitValRequ.getResultName(), ValueTransformer.transform(usedPart, splitValRequ.getTransform()));
                        } else {
                            if (splitValRequ.getFromPosition() == 1) {
                                // The case that the one before the first occurence is looked for
                                splitResult.put(splitValRequ.getResultName(), ValueTransformer.transform(value, splitValRequ.getTransform()));
                            }
                        }

                    }
                }
                return splitResult;
            }
        }
        return value;
    }
}
