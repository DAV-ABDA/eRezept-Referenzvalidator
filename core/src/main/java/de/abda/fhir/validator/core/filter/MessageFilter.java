package de.abda.fhir.validator.core.filter;

import java.util.List;

import ca.uhn.fhir.validation.SingleValidationMessage;

/**
 * Schnittstellenbeschreibung für einen Filter zur Entfernung von unerwünschten Meldungen.
 * 
 * @author Dzmitry Liashenka
 * @author Georg Tsakumagos
 */
public interface MessageFilter {

	/**
	 * Filtert die übergebene Liste und entfernt alle
	 * Messages, die der Filterbedingung entsprechen.
	 * 
	 * @param messages Die zu filternde Liste. Darf nicht <code>null</code> sein.
	 * 
	 * @throws IllegalArgumentException Wenn die übergeben Liste ungültig ist.
	 */
	public void filter(List<SingleValidationMessage> messages) throws IllegalArgumentException;
	
}
