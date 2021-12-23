package de.abda.fhir.validator.core.filter.regex;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Testet die JaxB- Implementierung von {@link FilterBeschreibung}
 * @author Dzmitry Liashenka
 *
 */
public class FilterBeschreibungTest {
	
	FilterBeschreibung filterBeschreibung;
	
	@BeforeEach
	public void setup() {
		filterBeschreibung = new FilterBeschreibung();
	}

	@Test()
	public void testSetSeverityPatternException() {
	   assertThrows(IllegalArgumentException.class, () -> {
	        filterBeschreibung.setSeverityPattern(null);
	    });
	}
	
	@Test()
	public void testSetLocationPatternException() {
        assertThrows(IllegalArgumentException.class, () -> {
            filterBeschreibung.setLocationPattern(null);
        });
	}
	
	@Test()
	public void testSetMessagePatternException() {
        assertThrows(IllegalArgumentException.class, () -> {
            filterBeschreibung.setMessagePattern(null);
        });
	}
	
	@Test
	public void testToString() {
		filterBeschreibung.setLocationPattern(Optional.of(Pattern.compile("location")));
		filterBeschreibung.setMessagePattern(Optional.of(Pattern.compile("message")));
		filterBeschreibung.setSeverityPattern(Optional.of(Pattern.compile("information")));
		
		assertEquals("FilterBeschreibung [ severityPattern='information', locationPattern='location', messagePattern='message' ]", filterBeschreibung.toString());
	}

}
