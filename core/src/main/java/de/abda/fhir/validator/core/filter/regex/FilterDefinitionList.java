package de.abda.fhir.validator.core.filter.regex;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Data Structure which defines a list of {@link FilterDefinition}
 *
 * @author Dzmitry Liashenka
 */
@XmlRootElement(name = "filterlist")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class FilterDefinitionList {

    private List<FilterDefinition> filterDefinitionList;

    /**
     * List of {@linkplain FilterDefinition} for filtering a List of {@link ca.uhn.fhir.validation.SingleValidationMessage}.
     *
     * @return filter list
     */
    @XmlElement(name = "filter")
    public List<FilterDefinition> getFilterDefinitionList() {
        return filterDefinitionList;
    }

    /**
     * Sets the filter list
     *
     * @param filterDefinitionList List of {@link FilterDefinition}. Must not be <code>null</code>.
     */
    public void setFilterDefinitionList(List<FilterDefinition> filterDefinitionList) {
        this.filterDefinitionList = filterDefinitionList;
    }
}
