package de.abda.fhir.cli;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import de.abda.fhir.validator.core.Validator;
import de.abda.fhir.validator.core.ValidatorHolder;
import de.abda.fhir.validator.core.util.FileHelper;
import de.abda.fhir.validator.core.util.ParserHelper;
import de.abda.fhir.validator.core.util.Profile;
import de.abda.fhir.validator.core.util.ProfileHelper;
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
            logger.warn("Usage: First argument must be a filename");
            logger.warn("No input file supplied! Exiting...");
            return;
        }

        FhirContext ctx = FhirContext.forR4Cached();
        ValidatorHolder validatorHolder = new ValidatorHolder(ctx);

        try {
            String validatorInputWithVersion = FileHelper.loadValidatorInputAsString(args[0], false);
            IBaseResource resource = ParserHelper.parseString(validatorInputWithVersion, ctx);
            Profile profile = ProfileHelper.getProfile(resource);
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
        }

    }

}
