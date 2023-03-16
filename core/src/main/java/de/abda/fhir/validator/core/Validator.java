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

import java.util.Collections;
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
        return validate(input, true);
    }

    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validate(IBaseResource input, boolean logErrors) {
        ValidationResult result = fhirValidator.validateWithResult(input);
        return handleValidationResults(result, logErrors);
    }

    public List<SingleValidationMessage> validate2ValidationMessageList(String input) {
        return validate2ValidationMessageList(input, true);
    }

    public List<SingleValidationMessage> validate2ValidationMessageList(String input, boolean logErrors) {
        ValidationResult result = fhirValidator.validateWithResult(input);

        List<SingleValidationMessage> messages = result.getMessages().stream().collect(Collectors.toList());
        AllowListHelper.applyAllowLists(messages, fhirProfileVersion);
        if (logErrors) {
            // The result object now contains the validation results
            for (SingleValidationMessage next : messages) {
                logger.info("Validator message: " + next.getSeverity() + " " + next.getLocationString() + " " + next.getMessage());
            }
        }
        return messages;
    }

    public List<SingleValidationMessage> validate2ValidationMessageList(IBaseResource input) {
        return validate2ValidationMessageList(input, true);
    }

    public List<SingleValidationMessage> validate2ValidationMessageList(IBaseResource input, boolean logErrors) {
        ValidationResult result = fhirValidator.validateWithResult(input);

        List<SingleValidationMessage> messages = result.getMessages().stream().collect(Collectors.toList());
        AllowListHelper.applyAllowLists(messages, fhirProfileVersion);
        if (true) {
            // The result object now contains the validation results
            for (SingleValidationMessage next : messages) {
                logger.info("Validator message: " + next.getSeverity() + " " + next.getLocationString() + " " + next.getMessage());
            }
        }
        return messages;
    }

    public boolean validate2Boolean(String input) {
        return validate2Boolean(input, true);
    }

    public boolean validate2Boolean(String input, boolean logErrors) {
        return validate2Boolean(validate2ValidationMessageList(input, logErrors));
    }

    public boolean validate2Boolean(IBaseResource input) {
        return validate2Boolean(input, true);
    }

    public boolean validate2Boolean(IBaseResource input, boolean logErrors) {
        return validate2Boolean(validate2ValidationMessageList(input, logErrors));
    }

    public static boolean validate2Boolean(Map<ResultSeverityEnum, List<SingleValidationMessage>> messagesGroup) {
        boolean validatorInputIsValid = messagesGroup.getOrDefault(ResultSeverityEnum.ERROR, Collections.emptyList()).size() == 0
                && messagesGroup.getOrDefault(ResultSeverityEnum.FATAL, Collections.emptyList()).size() == 0;
        return validatorInputIsValid;
    }

    public static boolean validate2Boolean(List<SingleValidationMessage> messages) {
        Map<ResultSeverityEnum, List<SingleValidationMessage>> messagesGroup = messages.stream().collect(Collectors.groupingBy(SingleValidationMessage::getSeverity, Collectors.toList()));
        return validate2Boolean(messagesGroup);
    }

    public static Map<ResultSeverityEnum, List<SingleValidationMessage>> validateList2Map(List<SingleValidationMessage> messages) {
        return messages.stream().collect(Collectors.groupingBy(SingleValidationMessage::getSeverity, Collectors.toList()));
    }

    private Map<ResultSeverityEnum, List<SingleValidationMessage>> handleValidationResults(ValidationResult result, boolean logErrors) {
        List<SingleValidationMessage> messages = result.getMessages().stream().collect(Collectors.toList());
        AllowListHelper.applyAllowLists(messages, fhirProfileVersion);

        if (logErrors) {
            // The result object now contains the validation results
            for (SingleValidationMessage next : messages) {
                logger.info("Validator message: " + next.getSeverity() + " " + next.getLocationString() + " " + next.getMessage());
            }
        }
        return messages.stream().collect(Collectors.groupingBy(SingleValidationMessage::getSeverity, Collectors.toList()));
    }
}
