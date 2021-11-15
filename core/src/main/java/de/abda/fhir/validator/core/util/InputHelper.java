package de.abda.fhir.validator.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputHelper {

    static Logger logger = LoggerFactory.getLogger(InputHelper.class);

    public static String removeVersionInCanonicals(String validatorInput) {
        String patternString = "(['\"])(https?://[^'\"|<>]+/StructureDefinition/[^'\"|<>]+)(\\|[^'\"|<>]+)(['\"])"; // Identify canonical profile URLs
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(validatorInput);
        validatorInput = matcher.replaceAll(matchResult -> { // remove version number
            return matchResult.group(1)+matchResult.group(2)+matchResult.group(4);
        });
        return validatorInput;
    }

}
