package de.abda.fhir.validator.core;

import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import de.abda.fhir.validator.core.configuration.FhirProfileVersion;
import de.abda.fhir.validator.core.util.AllowListHelper;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Validator {

    static Logger logger = LoggerFactory.getLogger(Validator.class);
    public final FhirValidator fhirValidator;

    private FhirProfileVersion fhirProfileVersion;

    public Validator(FhirValidator fhirValidator, FhirProfileVersion fhirProfileVersion) {
        this.fhirValidator = fhirValidator;
        this.fhirProfileVersion = fhirProfileVersion;
    }

    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validate(String input) {
        return validate(input, true);
    }

    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validate(String input, boolean logErrors) {
        ValidationResult result = fhirValidator.validateWithResult(input);
        return handleValidationResults(result, logErrors);
    }

    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validate(IBaseResource input) {
        ValidationResult result = fhirValidator.validateWithResult(input);
        return handleValidationResults(result, true);
    }

    private Map<ResultSeverityEnum, List<SingleValidationMessage>> handleValidationResults(
        ValidationResult result, boolean logErrors) {

        List<SingleValidationMessage> messages = result.getMessages().stream().collect(Collectors.toList());
        AllowListHelper.applyAllowLists(messages, fhirProfileVersion);

        if (logErrors) {
            // The result object now contains the validation results
            for (SingleValidationMessage next : messages) {
                logger.info("Validator message: " + next.getSeverity() + " " + next.getLocationString()
                        + " " + next.getMessage());
            }
        }
        return messages.stream().collect(
                Collectors.groupingBy(SingleValidationMessage::getSeverity, Collectors.toList()));
    }
}
