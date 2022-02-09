package de.abda.fhir.validator.core.filter;

import java.util.List;

import ca.uhn.fhir.validation.SingleValidationMessage;
import de.abda.fhir.validator.core.ValidationResult;

/**
 * Removes undesired validation messages from a List
 *
 * @author Dzmitry Liashenka
 * @author Georg Tsakumagos
 */
public interface MessageFilter {

    /**
     * filters the passed in List and removes SingleValidationMessages regarding the defined filterCriteria
     *
     * @param messages The List of messages to be filtered. Must not be <code>null</code>.
     * @throws IllegalArgumentException if messages is invalid
     * @return ValidationResult the validation result
     */
    ValidationResult filter(List<SingleValidationMessage> messages) throws IllegalArgumentException;

}
