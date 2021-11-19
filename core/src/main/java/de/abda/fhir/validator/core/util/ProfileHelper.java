package de.abda.fhir.validator.core.util;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileHelper {

    static Logger logger = LoggerFactory.getLogger(ProfileHelper.class);

    public static Profile getProfile(IBaseResource resource) {
        String metaProfile = resource.getMeta().getProfile().get(0).getValueAsString();
        String[] splittedString = metaProfile.split("\\|");
        // TODO: Was wenn keine Version angegeben (gematik)?
        if (splittedString.length < 2)
        {
            //String defaultVersion = "0.0.0";
            return new Profile(metaProfile, splittedString[0], "0.0.0");
        }
        else
        {
            return new Profile(metaProfile, splittedString[0], splittedString[1]);
        }
    }

    public static Profile getProfileFromStringInput(String input) {
        String patternString = "(<profile value=)(['\"])([Hh][Tt][Tt][Pp][Ss]?://[^|'\"<>\\s#]+/StructureDefinition/[^|'\"<>\\s#]+)(\\|)([^'\"<>\\s#]+)(['\"])(>)"; // Identify canonical profile URLs
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return new Profile(
                    matcher.group(3) + matcher.group(4) + matcher.group(5),
                    matcher.group(3),
                    matcher.group(5)
            );
        }
        return null;
    }

    public static Profile getProfileFromFile(File file) {
        String patternString = "(<profile\\s*value=['\"])([Hh][Tt][Tt][Pp][Ss]?://[^|'\"<>\\s#]+/StructureDefinition/[^|'\"<>\\s#]+)(\\|)([^'\"<>\\s#]+)(['\"]\\s*/?>)"; // Identify canonical profile URLs
        Pattern pattern = Pattern.compile(patternString);
        try {
            BufferedReader brTest = new BufferedReader(new FileReader(file));
            boolean foundProfile = false;
            String input;
            Matcher matcher;
            while (foundProfile == false) {
                input = brTest.readLine();
                if (input == null) return null;
                matcher = pattern.matcher(input);
                if (matcher.find()) {
                    return new Profile(
                            matcher.group(2) + matcher.group(3) + matcher.group(4),
                            matcher.group(2),
                            matcher.group(4)
                    );
                }
            }
        } catch (Exception e) {
            logger.error("Could not determine Profile. " + e.getClass().getSimpleName() + " during profile search in file: " + file.getName());
        }
        return null;
    }

}
