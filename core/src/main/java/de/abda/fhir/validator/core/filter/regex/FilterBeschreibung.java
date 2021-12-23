package de.abda.fhir.validator.core.filter.regex;

import java.util.Optional;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ca.uhn.fhir.validation.SingleValidationMessage;

/**
 * Datenstruktur für die Beschreibung eines Matchers für die Attribute {@link SingleValidationMessage}.
 * 
 * @author Dzmitry Liashenka
 */
@XmlRootElement(name="filter")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder= {"severityPattern","locationPattern", "messagePattern"})
public class FilterBeschreibung {
	
	private static final String ERROR_SEVERITY_NULL = "Severity darf nicht NULL sein.";
	private static final String ERROR_LOCATIONSTRING_NULL = "LocationString darf nicht NULL sein.";
	private static final String ERROR_MESSAGE_NULL = "Message darf nicht NULL sein.";
	
	private Optional<Pattern> severityPattern;
	private Optional<Pattern> locationPattern;
	private Optional<Pattern> messagePattern;
	
	/**
	 * Ermittelt das zum gegen die Eigenschaft {@linkplain SingleValidationMessage#getSeverity()}
	 * @return Das <em>case insensitive</em> Pattern. Ist niemals <code>null</code>.
	 */
	@XmlElement
	@XmlJavaTypeAdapter(PatternTypeAdapter.class)
	public Optional<Pattern> getSeverityPattern() {
		return this.severityPattern;
	}
	
    /**
     * Ermittelt das zum gegen die Eigenschaft {@linkplain SingleValidationMessage#getLocationString()}
     * @return Das <em>case insensitive</em> Pattern. Ist niemals <code>null</code>.
     */
	@XmlElement
	@XmlJavaTypeAdapter(PatternTypeAdapter.class)
	public Optional<Pattern> getLocationPattern() {
		return this.locationPattern;
	}
	
    /**
     * Ermittelt das zum gegen die Eigenschaft {@linkplain SingleValidationMessage#getMessage()}
     * @return Das <em>case insensitive</em> Pattern. Ist niemals <code>null</code>.
     */
	@XmlElement
	@XmlJavaTypeAdapter(PatternTypeAdapter.class)
	public Optional<Pattern> getMessagePattern() {
		return this.messagePattern;
	}

	/**
	 * Setzt neues Wert für serverity
	 * @param pattern Type des Messages: "information", "warning", "error", "fatal".
	 * @throws IllegalArgumentException Wenn übergebene Parameter <code>null</code> ist.
	 */
	public void setSeverityPattern(Optional<Pattern> pattern) throws IllegalArgumentException {
		if ( null == pattern) {
			throw new IllegalArgumentException(ERROR_SEVERITY_NULL);
		}
		this.severityPattern = pattern;
	}

	/**
	 * Setzt neues Wert für locationString
	 * @param pattern Location validierte Elemente.
	 * @throws IllegalArgumentException Wenn übergebene Parameter <code>null</code> ist.
	 */
	public void setLocationPattern(Optional<Pattern> pattern) throws IllegalArgumentException {
		if ( null == pattern) {
			throw new IllegalArgumentException(ERROR_LOCATIONSTRING_NULL);
		}
		this.locationPattern = pattern;
	}
	
	/**
	 * Setzt neues Wert für message.
	 * @param pattern {@linkplain SingleValidationMessage} von FhirValidator.
	 * @throws IllegalArgumentException Wenn übergebene Parameter <code>null</code> ist.
	 */
	public void setMessagePattern(Optional<Pattern> pattern) throws IllegalArgumentException {
		if ( null == pattern) {
			throw new IllegalArgumentException(ERROR_MESSAGE_NULL);
		}
		this.messagePattern = pattern;
	}

	@Override
	public String toString() {
		return String.format("FilterBeschreibung [ severityPattern='%s', locationPattern='%s', messagePattern='%s' ]", severityPattern.orElse( null ), locationPattern.orElse( null ), messagePattern.orElse( null ));
	}
	
}
