package de.abda.fhir.validator.core;

import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Validator {

    static final Logger logger = LoggerFactory.getLogger(Validator.class);
    private final FhirValidator fhirValidator;

    public Validator(FhirValidator fhirValidator) {
        this.fhirValidator = fhirValidator;
    }

    /**
     * Validates the input string and returns the fhir hapi validation result without applying any kind of filtering
     * @param input the input string to be validated
     * @return the validation result
     */
    public ValidationResult validateWithResult(String input){
        return fhirValidator.validateWithResult(input);
    }

}
