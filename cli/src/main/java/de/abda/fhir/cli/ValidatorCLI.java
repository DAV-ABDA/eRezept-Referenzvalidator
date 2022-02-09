package de.abda.fhir.cli;

import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import de.abda.fhir.validator.core.ReferenceValidator;
import de.abda.fhir.validator.core.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidatorCLI {

    static Logger logger = LoggerFactory.getLogger(ValidatorCLI.class);

    public static void main(String[] args) {

        if (args.length != 1) {
            logger.warn("Usage: First argument must be a filename");
            logger.warn("No input file supplied! Exiting...");
            return;
        }

        try {
            ReferenceValidator validator = new ReferenceValidator();
            ValidationResult validationResult = validator.validateFile(
                    Paths.get(args[0]));
//            String mapAsString = validationResult.keySet().stream()
//                    .map(key -> key + ": " + validationResult.get(key).size())
//                    .collect(Collectors.joining(","));
            logger.info("Validation success: " + validationResult.isValid() + " -- Result summary: " + getSummary(validationResult));
            System.exit(validationResult.isValid() ? 0 : 1);
        } catch (Exception e){
            logger.error("Exception occured", e);
        }

        /*File inputFile = new File(args[0]);

        FhirContext ctx = FhirContext.forR4Cached();
        ValidatorHolder validatorHolder = new ValidatorHolder(ctx);

        try {
            Profile profile = ProfileHelper.getProfileFromXmlStream(new FileInputStream(inputFile));
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

    private static String getSummary(ValidationResult validationResult) {
        Map<ResultSeverityEnum, List<SingleValidationMessage>> messages = validationResult.getSingleValidationMessages().stream().collect(
                Collectors.groupingBy(SingleValidationMessage::getSeverity, Collectors.toList()));
        String numMessagesBySeverity = messages.keySet().stream()
                    .map(key -> key + ": " + messages.get(key).size())
                    .collect(Collectors.joining(","));
        int numFilteredMessages = validationResult.getFilteredMessages().size();
        return numMessagesBySeverity + ", numberOfFilteredMessages:" + numFilteredMessages;
    }
}
