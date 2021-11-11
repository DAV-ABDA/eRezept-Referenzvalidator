package de.abda.fhir.validator.core;

import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Validator {

    static Logger logger = LoggerFactory.getLogger(Validator.class);
    private final FhirValidator fhirValidator;

    public Validator(FhirValidator fhirValidator) {
        this.fhirValidator = fhirValidator;
    }

    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validate(String input) {
        ValidationResult result = fhirValidator.validateWithResult(input);
        return handleValidationResults(result);
    }

    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validate(IBaseResource input) {
        ValidationResult result = fhirValidator.validateWithResult(input);
        return handleValidationResults(result);
    }

    private Map<ResultSeverityEnum, List<SingleValidationMessage>> handleValidationResults(
        ValidationResult result) {

        // The result object now contains the validation results
        for (SingleValidationMessage next : result.getMessages()) {
            logger.info(
                "Validator message: " + next.getSeverity() + " " + next.getLocationString() + " " + next.getMessage());
        }
        return result.getMessages().stream().collect(
                Collectors.groupingBy(SingleValidationMessage::getSeverity, Collectors.toList()));
    }
}
