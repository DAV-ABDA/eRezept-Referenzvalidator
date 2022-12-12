package de.abda.fhir.cli;

import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import de.abda.fhir.validator.core.ReferenceValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidatorCLI {

    static Logger logger = LoggerFactory.getLogger(ValidatorCLI.class);

    public static void main(String[] args) {
        boolean noInstanceValidityCheck = false;

        // argv[0] => Instance-path
        // argv[1] => parameter -noInstanceValidityCheck
        // TODO: argv[2] => parameter -ProfileName ?!?

        if ((args.length == 1) || (args.length == 2)) {
            try {
                if (!Files.exists( Paths.get( args[0] ), LinkOption.NOFOLLOW_LINKS ) ) {
                    System.out.println( "First argument must be a exists filename '" + args[0] + "' can not be opened." );
                    System.exit(0);
                }
                if ((args.length == 2) && args[1].equals("-noInstanceValidityCheck")) {
                    noInstanceValidityCheck = true;
                } else if (args.length == 2) {
                    System.out.println( "the second parameter '" + args[1] + "' is not recognized and will be ignored" );
                }
                ReferenceValidator validator = new ReferenceValidator();
                Map<ResultSeverityEnum, List<SingleValidationMessage>> errors = validator.validateFile(Paths.get(args[0]), noInstanceValidityCheck);
                String mapAsString = errors.keySet().stream()
                        .map(key -> key + ": " + errors.get(key).size())
                        .collect(Collectors.joining(","));
                boolean validatorInputIsValid =
                        errors.getOrDefault(ResultSeverityEnum.ERROR, Collections.emptyList()).size() == 0
                                && errors.getOrDefault(ResultSeverityEnum.FATAL, Collections.emptyList()).size() == 0;
                logger.info("Validation result: " + validatorInputIsValid + " -- Result summary: " + mapAsString);
                System.exit(validatorInputIsValid ? 0 : 1);
            } catch (Exception e){
                logger.error("Exception occured", e);
                System.exit(0);
            }
        } else {
            if (args.length < 1) {
                logger.warn("Usage: First argument must be a filename");
                logger.warn("Usage: Second argument is an optional option to deactivate the instance validity check (default ist True)");
                logger.warn("No input file supplied! Exiting...");
            } else {
                logger.warn("Usage: First argument must be a filename");
                logger.warn("Usage: Second argument is an optional option to deactivate the instance validity check (default ist True)");
                logger.warn("terminated due to errors in invocation parameter...");
            }
            System.exit(0);
        }
    }
}
