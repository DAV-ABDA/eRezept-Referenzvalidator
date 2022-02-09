package de.abda.fhir.validator.core.filter;

import ca.uhn.fhir.validation.SingleValidationMessage;
import de.abda.fhir.validator.core.filter.regex.FilterDefinition;

import javax.xml.bind.annotation.*;

/**
 * Holds a message that was filtered out and the filter definition that was used for filtering
 * @author Frank Jesgarz
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"filterDefinition", "singleValidationMessage"})
public class FilteredMessage {
    private FilterDefinition filterDefinition;
    private SingleValidationMessage singleValidationMessage;

    @SuppressWarnings("unused")
    public FilteredMessage(){
        //for jaxb
    }

    /**
     * Constructor
     * @param filterDefinition the filterDefinition that was responsible for filtering out the singleValidationMessage
     * @param singleValidationMessage the singleValidationMessage that was filtered
     */
    public FilteredMessage(FilterDefinition filterDefinition, SingleValidationMessage singleValidationMessage) {
        this.filterDefinition = filterDefinition;
        this.singleValidationMessage = singleValidationMessage;
    }

    /**
     * @return the filterDefinition that was responsible for filtering out the message
     */
    @XmlElement()
    public FilterDefinition getFilterDefinition() {
        return filterDefinition;
    }

    /**
     *
     * @return the {@link SingleValidationMessage} that was filtered
     */
    @XmlElement()
    public SingleValidationMessage getSingleValidationMessage() {
        return singleValidationMessage;
    }
}
