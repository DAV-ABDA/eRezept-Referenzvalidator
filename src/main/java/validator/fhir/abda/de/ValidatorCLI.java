package validator.fhir.abda.de;

import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidatorCLI {

    static Logger logger = LoggerFactory.getLogger(ValidatorCLI.class);

    public static void main(String[] args) {

        if (args.length != 1) {
            logger.warn("No input file supplied! Exiting...");
            return;
        }

        String validatorInput = InputHelper.loadValidatorInput(args[0]);

        logger.debug(validatorInput);

        Validator validator = new Validator();

        Map<ResultSeverityEnum, List<SingleValidationMessage>> errors = validator.validate(validatorInput);
        String mapAsString = errors.keySet().stream()
                .map(key -> key + ": " + errors.get(key).size())
                .collect(Collectors.joining(","));
        Boolean validatorInputIsValid = (errors.get(ResultSeverityEnum.ERROR).size() == 0 && errors.get(ResultSeverityEnum.FATAL).size() == 0) ? true : false;
        logger.info("Validation result: " + validatorInputIsValid + " -- Error summary: " + mapAsString);

    }

}
