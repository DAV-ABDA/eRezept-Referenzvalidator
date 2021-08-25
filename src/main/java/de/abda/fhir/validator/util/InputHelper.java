package de.abda.fhir.validator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputHelper {

    static Logger logger = LoggerFactory.getLogger(InputHelper.class);

    public static String removeVersionInCanonicals(String validatorInput) {
        String patternString = "(https?://.{1,100}(\\..{1,50}){1,10}(/.{1,50}){0,20}/StructureDefinition/.{1,50})(\\|\\d(.\\d+)+)"; // Identify canonical profile URLs
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(validatorInput);
        validatorInput = matcher.replaceAll(matchResult -> { // remove version number
            return matchResult.group(1);
        });
        return validatorInput;
    }

}
