package de.abda.fhir.validator.core.util;

import java.util.regex.MatchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputHelper {

    static Logger logger = LoggerFactory.getLogger(InputHelper.class);

    public static String removeVersionInCanonicals(String validatorInput) {
//        String patternString = "(['\"])([Hh][Tt][Tt][Pp][Ss]?://[^|'\"<>\\s#]+/StructureDefinition/[^|'\"<>\\s#]+)(\\|[^'\"<>\\s#]+)(['\"])"; // Identify canonical profile URLs
//        Pattern pattern = Pattern.compile(patternString);
//        Matcher matcher = pattern.matcher(validatorInput);
//        if (matcher.find()) {
//            MatchResult matchResult = matcher.toMatchResult();
//            String replacement = matchResult.group(1) + matchResult.group(2) + matchResult.group(4);
//            validatorInput = matcher.replaceAll(replacement);
//        }
        return validatorInput;
    }

}
