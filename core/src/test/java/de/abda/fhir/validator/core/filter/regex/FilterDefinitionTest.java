package de.abda.fhir.validator.core.filter.regex;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Testet die JaxB-Implementierung von {@link FilterDefinition}
 * @author Dzmitry Liashenka
 *
 */
public class FilterDefinitionTest {
	
	FilterDefinition filterBeschreibung;
	
	@BeforeEach
	public void setup() {
		filterBeschreibung = new FilterDefinition();
	}
	
	@Test
	public void testToString() {
		filterBeschreibung.setIssueId("ERP-001");
		filterBeschreibung.setLocationPattern(Pattern.compile("location"));
		filterBeschreibung.setMessagePattern(Pattern.compile("message"));
		filterBeschreibung.setSeverityPattern(Pattern.compile("information"));

		assertEquals("FilterBeschreibung [ issueId='ERP-001', severityPattern='information', locationPattern='location', messagePattern='message' ]", filterBeschreibung.toString());
	}

}
