package de.abda.fhir.cli;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import de.abda.fhir.validator.core.Validator;
import de.abda.fhir.validator.core.util.FileHelper;
import de.abda.fhir.validator.core.util.ParserHelper;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
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

        FhirContext ctx = FhirContext.forR4();

        try {
            String validatorInput = FileHelper.loadValidatorInputAsString(args[0]);
            logger.debug(validatorInput);
//          IBaseResource validatorInput = FileHelper.loadValidatorInputAsResource(args[0],ctx);
            Validator validator = new Validator(ctx);

            ParserHelper parserHelper = new ParserHelper(ctx);
            IBaseResource resource = parserHelper.parseString(validatorInput);

            Map<ResultSeverityEnum, List<SingleValidationMessage>> errors = validator.validate(validatorInput);
            String mapAsString = errors.keySet().stream()
                    .map(key -> key + ": " + errors.get(key).size())
                    .collect(Collectors.joining(","));
            Boolean validatorInputIsValid = (errors.getOrDefault(ResultSeverityEnum.ERROR, Collections.emptyList()).size() == 0 && errors.getOrDefault(ResultSeverityEnum.FATAL, Collections.emptyList()).size() == 0) ? true : false;
            logger.info("Validation result: " + validatorInputIsValid + " -- Error summary: " + mapAsString);
        } catch (Exception e){
            logger.error("Exception occured", e);
        }

    }

}
