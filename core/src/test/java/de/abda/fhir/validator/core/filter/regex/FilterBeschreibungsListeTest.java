package de.abda.fhir.validator.core.filter.regex;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FilterBeschreibungsListeTest {
	
	FilterBeschreibungsListe filterBeschreibungsListe;
	
	@BeforeEach
	public void setup() {
		filterBeschreibungsListe = new FilterBeschreibungsListe();
	}

	@Test()
	public void testSetFilterBeschreibungsListeException() {
        assertThrows(IllegalArgumentException.class, () -> {
            filterBeschreibungsListe.setBeschreibungFilterList(null);
        });

	}

}
