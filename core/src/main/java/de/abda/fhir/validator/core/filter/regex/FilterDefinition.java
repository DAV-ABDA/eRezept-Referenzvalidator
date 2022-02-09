package de.abda.fhir.validator.core.filter.regex;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;

/**
 * Data Structure which defines several matchers for the attributes defined in {@link SingleValidationMessage}.
 *
 * @author Dzmitry Liashenka
 */
@XmlRootElement(name = "filter")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"issueId", "severityPattern", "locationPattern", "messagePattern"})
public class FilterDefinition {
    private String issueId;
    private Pattern severityPattern = null;
    private Pattern locationPattern = null;
    private Pattern messagePattern = null;

    @XmlAttribute(required = true)
    @XmlID
    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }




    /**
     * returns a pattern that can be used for matching the property {@linkplain SingleValidationMessage#getSeverity()}
     *
     * @return <em>case insensitive</em> Pattern. May be null <code>null</code>.
     */
    @XmlElement
    @XmlJavaTypeAdapter(PatternTypeAdapter.class)
    public Pattern getSeverityPattern() {
        return this.severityPattern;
    }

    /**
     * returns a pattern that can be used for matching the property {@linkplain SingleValidationMessage#getLocationString()}
     *
     * @return <em>case insensitive</em> Pattern. May be null <code>null</code>.
     */
    @XmlElement
    @XmlJavaTypeAdapter(PatternTypeAdapter.class)
    public Pattern getLocationPattern() {
        return this.locationPattern;
    }

    /**
     * returns a pattern that can be used for matching the property {@linkplain SingleValidationMessage#getMessage()}
     *
     * @return <em>case insensitive</em> Pattern. May be null <code>null</code>.
     */
    @XmlElement
    @XmlJavaTypeAdapter(PatternTypeAdapter.class)
    public Pattern getMessagePattern() {
        return this.messagePattern;
    }

    /**
     * sets the pattern that is used to check if the severity of the message is matching
     *
     * @param pattern severity of the message. May be <code>null</code>.
     * @see ResultSeverityEnum#getCode()
     */
    public void setSeverityPattern(Pattern pattern) {
        this.severityPattern = pattern;
    }

    /**
     * sets the pattern that is used to check if the location of the message is matching
     *
     * @param pattern pattern that is used to check the location of message. May be <code>null</code>.
     */
    public void setLocationPattern(Pattern pattern) {
        this.locationPattern = pattern;
    }

    /**
     * sets the pattern that is used to match the content of the message
     *
     * @param pattern pattern that is used to check the content of the message. May be <code>null</code>.
     */
    public void setMessagePattern(Pattern pattern) {
        this.messagePattern = pattern;
    }

    @Override
    public String toString() {
        return String.format("FilterBeschreibung [ issueId='%s', severityPattern='%s', locationPattern='%s', messagePattern='%s' ]", issueId, severityPattern, locationPattern, messagePattern);
    }

    /**
     * Checks if there are any Patterns for the location, message or severity defined
     *
     * @return true if there is at least one pattern defined.
     */
    public boolean hasPatterns() {
        return !Stream.of(messagePattern, severityPattern, locationPattern)
                .allMatch(Objects::isNull);
    }
}
