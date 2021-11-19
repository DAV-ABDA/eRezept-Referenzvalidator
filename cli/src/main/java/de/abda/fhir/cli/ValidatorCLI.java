package de.abda.fhir.cli;

import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ch.qos.logback.classic.Level;
import de.abda.fhir.validator.core.ReferenceValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidatorCLI {

    static Logger logger = LoggerFactory.getLogger(ValidatorCLI.class);
    static ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    public static void main(String[] args) {

        rootLogger.setLevel(Level.INFO);

        if (args.length != 1) {
            logger.warn("Usage: First argument must be a filename");
            logger.warn("No input file supplied! Exiting...");
            return;
        }

        try {
            ReferenceValidator validator = new ReferenceValidator();
            Map<ResultSeverityEnum, List<SingleValidationMessage>> errors = validator.validateFile(
                    Paths.get(args[0]));
            String mapAsString = errors.keySet().stream()
                    .map(key -> key + ": " + errors.get(key).size())
                    .collect(Collectors.joining(","));
            boolean validatorInputIsValid =
                    errors.getOrDefault(ResultSeverityEnum.ERROR, Collections.emptyList()).size() == 0
                            && errors.getOrDefault(ResultSeverityEnum.FATAL, Collections.emptyList()).size() == 0;
            logger.info("Validation result: " + validatorInputIsValid + " -- Error summary: " + mapAsString);
            System.exit(validatorInputIsValid ? 0 : 1);
        } catch (Exception e){
            logger.error("Exception occured", e);
        }

        /*File inputFile = new File(args[0]);

        FhirContext ctx = FhirContext.forR4Cached();
        ValidatorHolder validatorHolder = new ValidatorHolder(ctx);

        try {
            Profile profile = ProfileHelper.getProfileFromFile(inputFile);
            logger.debug("Profile identified: " + profile.getCanonical());
            Validator validator = validatorHolder.getValidatorForProfile(profile);
            String validatorInput = FileHelper.loadValidatorInputAsString(args[0], true);
            logger.debug(validatorInput);
            Map<ResultSeverityEnum, List<SingleValidationMessage>> errors = validator.validate(validatorInput);
            String mapAsString = errors.keySet().stream()
                    .map(key -> key + ": " + errors.get(key).size())
                    .collect(Collectors.joining(","));
            Boolean validatorInputIsValid = (errors.getOrDefault(ResultSeverityEnum.ERROR, Collections.emptyList()).size() == 0 && errors.getOrDefault(ResultSeverityEnum.FATAL, Collections.emptyList()).size() == 0) ? true : false;
            logger.info("Validation result: " + validatorInputIsValid + " -- Error summary: " + mapAsString);
        } catch (Exception e){
            logger.error("Exception occured", e);
        }*/

    }

}
