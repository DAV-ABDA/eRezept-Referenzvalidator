package de.abda.fhir.cli;

import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import de.abda.fhir.validator.core.ReferenceValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidatorCLI {

    static Logger logger = LoggerFactory.getLogger(ValidatorCLI.class);

    public static void main(String[] args) {
        boolean noInstanceValidityCheck = false;
        int arg4instance = -1;
        int arg4profile = -1;
        List<String> profileValidateAgainst = new ArrayList<String>();

        // argv[0] => InstancePath
        // argv[1] => parameter --noInstanceValidityCheck
        // argv[2] => parameter --profile [parameter]
        if (args.length < 1) {
            logger.warn("Usage: one argument must be a filename to validate");
            logger.warn("Usage: another one argument is an optional option to deactivate the instance validity check");
            logger.warn("No input file supplied! Exiting...");
            //System.out.println("Usage: one argument must be a filename to validate");
            System.exit(0);
        }
        try {
            for(int i = 0; i < args.length; i++) {
                if (i == arg4profile ) {
                    continue;
                }
                if (args[i].contains("--")) {
                    if (args[i].equals("--noInstanceValidityCheck")) {
                        noInstanceValidityCheck = true;
                    }
                    else if (args[i].equals("--profile")) {
                        // packages.yaml surch4 -> profileName: "http...../StructureDefinition/.........."
                        if (((i + 1) < args.length) && (args[i + 1].startsWith("http"))) {
                            arg4profile = i + 1;
                            profileValidateAgainst.add(args[arg4profile]);
                        } else {
                            System.out.println( "wrong profile parameter '" + args[i + 1]);
                            System.exit(0);
                        }
                    }
                } else {
                    if (!Files.exists(Paths.get(args[i]), LinkOption.NOFOLLOW_LINKS)) {
                        System.out.println( "one argument must be a exists filename '" + args[i] + "' can not be opened." );
                        System.exit(0);
                    } else {
                        // TODO: der Letzte gewinnt... if arg4instance > 0
                        arg4instance = i;
                    }
                }
            } // for args
            ReferenceValidator validator = new ReferenceValidator();
            Map<ResultSeverityEnum, List<SingleValidationMessage>> errors = validator.validateFile(Paths.get(args[arg4instance]), noInstanceValidityCheck, profileValidateAgainst);

            String mapAsString = errors.keySet().stream()
                    .map(key -> key + ": " + errors.get(key).size())
                    .collect(Collectors.joining(","));

            boolean validatorInputIsValid =
                    errors.getOrDefault(ResultSeverityEnum.ERROR, Collections.emptyList()).size() == 0
                            && errors.getOrDefault(ResultSeverityEnum.FATAL, Collections.emptyList()).size() == 0;

            logger.info("Validation result: " + validatorInputIsValid + " -- Result summary: " + mapAsString);
            //System.out.println("Validation result: " + validatorInputIsValid + " -- Result summary: " + mapAsString);

            System.exit(validatorInputIsValid ? 0 : 1);
        } catch (Exception e){
            logger.error("Exception occured", e);
            System.exit(0);
        }
    }
}
