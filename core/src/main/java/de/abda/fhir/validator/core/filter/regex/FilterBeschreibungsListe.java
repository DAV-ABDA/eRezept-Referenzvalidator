package de.abda.fhir.validator.core.filter.regex;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Eine Liste mit FilterBeschreibung für Validation.
 * @author Dzmitry Liashenka
 *
 */
@XmlRootElement(name="filterlist")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class FilterBeschreibungsListe {

	private static final String ERROR_FILTER_LIST_NULL = "Die Liste der Filters darf nicht NULL sein.";
	
	private List<FilterBeschreibung> beschreibungFilterList;

	/**
	 * Gibt eine Liste mit {@linkplain FilterBeschreibung} zurück.
	 * @return the beschreibungFilterList Eine Liste mit FilterBeschreibung für Validation.
	 */
	@XmlElement(name="filter")
	public List<FilterBeschreibung> getBeschreibungFilterList() {
		return beschreibungFilterList;
	}

	/**
	 * Setzt den Wert für die Eigenschaft.
	 * @param beschreibungFilterList Liste der FilterBeschreinungen. Darf nicht <code>null</code> sein!
	 * @throws IllegalArgumentException wenn beschreibungFilterList <code>null</code> ist.
	 */
	public void setBeschreibungFilterList(List<FilterBeschreibung> beschreibungFilterList) throws IllegalArgumentException {
		if ( null == beschreibungFilterList) {
			throw new IllegalArgumentException(ERROR_FILTER_LIST_NULL);
		}
		this.beschreibungFilterList = beschreibungFilterList;
	}
}
