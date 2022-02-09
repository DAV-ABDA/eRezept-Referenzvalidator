package de.abda.fhir.validator.core.filter.regex;

import ca.uhn.fhir.validation.SingleValidationMessage;
import de.abda.fhir.validator.core.ValidationResult;
import de.abda.fhir.validator.core.filter.MessageFilter;
import de.abda.fhir.validator.core.util.Profile;

import java.util.Collections;
import java.util.List;

/**
 * This implementation simply returns a {@link ValidationResult} without applying any filtering
 */
public class NonFilteringMessageFilter implements MessageFilter {


    private final Profile profile;

    /**
     * Constructor
     * @param profile thr profile this filter is bound to
     */
    public NonFilteringMessageFilter(Profile profile) {
        this.profile = profile;
    }

    /**
     *
     * @param messages The List of messages to be filtered. Must not be <code>null</code>.
     * @return unfiltered Result
     */
    @Override
    public ValidationResult filter(List<SingleValidationMessage> messages)  {
        return new ValidationResult(messages, this, Collections.emptyList(), profile);
    }

    @Override
    public String toString() {
        return "NonFilteringMessageFilter{}";
    }
}
