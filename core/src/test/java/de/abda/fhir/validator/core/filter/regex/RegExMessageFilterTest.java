package de.abda.fhir.validator.core.filter.regex;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;

/**
 * Testet die Implementierung der Klasse {@link RegExMessageFilter}
 * @author Dzmitry Liashenka
 *
 */
public class RegExMessageFilterTest {
	
	private final Logger logger = LoggerFactory.getLogger(RegExMessageFilter.class);
	
	private static final String PATH_TO_FILTER_XML = "/de/abda/fhir/validator/core/filter/regex/validation-filter.xml";
	
	private static final String PATH_TO_FILTER_SEVERITY_XML = "/de/abda/fhir/validator/core/filter/regex/validation-filter-severity.xml";
	
	private static final String PATH_TO_FILTER_MESSAGE_XML = "/de/abda/fhir/validator/core/filter/regex/validation-filter-message.xml";
	
	private static final String PATH_TO_FILTER_LOCATION_XML = "/de/abda/fhir/validator/core/filter/regex/validation-filter-location.xml";
	
	private static final String PATH_TO_FILTER_EMPTY_XML = "/de/abda/fhir/validator/core/filter/regex/validation-filter-empty.xml";

	private static final String MESSAGE_PATTERN_INFO = "testInformationMessage";

	private static final String MESSAGE_PATTERN_WARN = "testWarningMessage";

	private static final String MESSAGE_PATTERN_ERROR = "testErrorMessage";

	private static final String MESSAGE_PATTERN_FATAL = "testFatalMessage";

	private static final String LOCATION_PATTERN_INFO = "testLocationInformation";

	private static final String LOCATION_PATTERN_WARN = "testLocationWarning";
	
	private static final String LOCATION_PATTERN_ERROR = "testLocationError";
	
	private static final String LOCATION_PATTERN_FATAL = "testLocationFatal";

	private static final String PATH_NOT_EXIST = "file:///unknown.path.0815";
	

	List<SingleValidationMessage> messages;
	
	@BeforeEach
	public void setup() {
		
		SingleValidationMessage info = new SingleValidationMessage();
		info.setSeverity(ResultSeverityEnum.INFORMATION);
		info.setLocationString(LOCATION_PATTERN_INFO);
		info.setMessage(MESSAGE_PATTERN_INFO);
		
		SingleValidationMessage warning = new SingleValidationMessage();
		warning.setSeverity(ResultSeverityEnum.WARNING);
		warning.setLocationString(LOCATION_PATTERN_WARN);
		warning.setMessage(MESSAGE_PATTERN_WARN);
		
		SingleValidationMessage error = new SingleValidationMessage();
		error.setSeverity(ResultSeverityEnum.ERROR);
		error.setLocationString(LOCATION_PATTERN_ERROR);
		error.setMessage(MESSAGE_PATTERN_ERROR);
		
		SingleValidationMessage fatal = new SingleValidationMessage();
		fatal.setSeverity(ResultSeverityEnum.FATAL);
		fatal.setLocationString(LOCATION_PATTERN_FATAL);
		fatal.setMessage(MESSAGE_PATTERN_FATAL);
		
		messages = new ArrayList<>();
		messages.add(info);
		messages.add(warning);
		messages.add(error);
		messages.add(fatal);
	}
	
	
	/**
	 * Testet, ob die Daten korrekt gemarshalled und ungemarshalled werden können.
	 * Dazu werden die Daten einmal durch den Prozess durchgeführt und anschließend
	 * alle Attribute verglichen.
	 */
	@Test
	public void roundtrip() {
		List<FilterBeschreibung> filterBeschreibungsListe = new ArrayList<>();
		
		FilterBeschreibung info = new FilterBeschreibung();
		info.setSeverityPattern(Optional.of(Pattern.compile(ResultSeverityEnum.INFORMATION.getCode())));
		info.setMessagePattern(Optional.of(Pattern.compile(MESSAGE_PATTERN_INFO)));
		info.setLocationPattern(Optional.of(Pattern.compile(LOCATION_PATTERN_INFO)));
		
		FilterBeschreibung warn = new FilterBeschreibung();
		warn.setSeverityPattern(Optional.of(Pattern.compile(ResultSeverityEnum.WARNING.getCode())));
		warn.setMessagePattern(Optional.of(Pattern.compile(MESSAGE_PATTERN_WARN)));
		warn.setLocationPattern(Optional.of(Pattern.compile(LOCATION_PATTERN_WARN)));
		
		FilterBeschreibung error = new FilterBeschreibung();
		error.setSeverityPattern(Optional.of(Pattern.compile(ResultSeverityEnum.ERROR.getCode())));
		error.setMessagePattern(Optional.of(Pattern.compile(MESSAGE_PATTERN_ERROR)));
		error.setLocationPattern(Optional.of(Pattern.compile(LOCATION_PATTERN_ERROR)));
		
		FilterBeschreibung fatal = new FilterBeschreibung();
		fatal.setSeverityPattern(Optional.of(Pattern.compile(ResultSeverityEnum.FATAL.getCode())));
		fatal.setMessagePattern(Optional.of(Pattern.compile(MESSAGE_PATTERN_FATAL)));
		fatal.setLocationPattern(Optional.of(Pattern.compile(LOCATION_PATTERN_FATAL)));
		
		filterBeschreibungsListe.add(info);
		filterBeschreibungsListe.add(warn);
		filterBeschreibungsListe.add(error);
		filterBeschreibungsListe.add(fatal);
		
		// Attribute setzen!
		// Objekt Unmarshallen String > Bean
		RegExMessageFilter filter = this.loadFilter(PATH_TO_FILTER_XML);
		List<FilterBeschreibung> unmarshallList = filter.getFilterBeschreibungsListe();
		
		// Objekt Marshallen Bean > String
		StringWriter sw = new StringWriter();
		Writer writer = new PrintWriter(sw);
		
		filter.marshall(writer);
		logger.info("Marshalled filterlist \n{}", sw.toString());

		// Alle Attribute Prüfen: filterListe vs. Bean (unmarshalled roundtrip)
		assertEquals(filterBeschreibungsListe.get(0).getSeverityPattern().get().pattern(), unmarshallList.get(0).getSeverityPattern().get().pattern());
		assertEquals(filterBeschreibungsListe.get(0).getMessagePattern().get().pattern(), unmarshallList.get(0).getMessagePattern().get().pattern());
		assertEquals(filterBeschreibungsListe.get(0).getLocationPattern().get().pattern(), unmarshallList.get(0).getLocationPattern().get().pattern());
	
		assertEquals(filterBeschreibungsListe.get(1).getSeverityPattern().get().pattern(), unmarshallList.get(1).getSeverityPattern().get().pattern());
		assertEquals(filterBeschreibungsListe.get(1).getMessagePattern().get().pattern(), unmarshallList.get(1).getMessagePattern().get().pattern());
		assertEquals(filterBeschreibungsListe.get(1).getLocationPattern().get().pattern(), unmarshallList.get(1).getLocationPattern().get().pattern());
		
		assertEquals(filterBeschreibungsListe.get(2).getSeverityPattern().get().pattern(), unmarshallList.get(2).getSeverityPattern().get().pattern());
		assertEquals(filterBeschreibungsListe.get(2).getMessagePattern().get().pattern(), unmarshallList.get(2).getMessagePattern().get().pattern());
		assertEquals(filterBeschreibungsListe.get(2).getLocationPattern().get().pattern(), unmarshallList.get(2).getLocationPattern().get().pattern());

		assertEquals(filterBeschreibungsListe.get(3).getSeverityPattern().get().pattern(), unmarshallList.get(3).getSeverityPattern().get().pattern());
		assertEquals(filterBeschreibungsListe.get(3).getMessagePattern().get().pattern(), unmarshallList.get(3).getMessagePattern().get().pattern());
		assertEquals(filterBeschreibungsListe.get(3).getLocationPattern().get().pattern(), unmarshallList.get(3).getLocationPattern().get().pattern());
	}

	
	/**
	 * Testet die Implementation der Methode {@linkplain DefaultMessageFilter#filter(List)}
	 * In Test sollen die Messages mit Severity 'warning' und 'error' gefiltert werden. 
	 * @throws  
	 */
	@Test
	public void testSeverityPattern() throws IllegalArgumentException {
	    RegExMessageFilter filter = this.loadFilter(PATH_TO_FILTER_SEVERITY_XML);
		filter.filter(messages);
		
		messages.forEach(m -> {
			if (m.getSeverity().equals(ResultSeverityEnum.WARNING)) {
				fail("SingleValidationMessage mit Severity [" + ResultSeverityEnum.WARNING  + "] muss gefiltert werden");
			}
			if (m.getSeverity().equals(ResultSeverityEnum.ERROR)) {
				fail("SingleValidationMessage mit Severity [" + ResultSeverityEnum.ERROR  + "] muss gefiltert werden");
			}
		});
	}
	
	private RegExMessageFilter loadFilter( String cpResource ) {
	     URL url = this.getClass().getResource(cpResource);
	     assertNotNull(url, "Unbekannte Ressource" + url.toString());
         return new RegExMessageFilter(url);
    }


    /**
	 * Testet, ob {@linkplain SingleValidationMessage} mit MessagePattern gefiltert wird.
	 */
	@Test
	public void testMessagePattern() {
		RegExMessageFilter filter = this.loadFilter(PATH_TO_FILTER_MESSAGE_XML);
		filter.filter(messages);
		
		messages.forEach(m -> {
			if (m.getMessage().equals(MESSAGE_PATTERN_ERROR)) {
				fail("SingleValidationMessage mit Message [" + MESSAGE_PATTERN_ERROR  + "] muss gefiltert werden");
			}
			if (m.getMessage().equals(MESSAGE_PATTERN_FATAL)) {
				fail("SingleValidationMessage mit Message [" + MESSAGE_PATTERN_FATAL  + "] muss gefiltert werden");
			}
			if (m.getMessage().equals(MESSAGE_PATTERN_WARN)) {
				fail("SingleValidationMessage mit Message [" + MESSAGE_PATTERN_WARN  + "] muss gefiltert werden");
			}
		});
	}
	
	/**
	 * Testet, ob {@linkplain SingleValidationMessage} mit LocationPattern gefiltert wird.
	 */
	@Test
	public void testLocationPattern() {
		RegExMessageFilter filter = this.loadFilter(PATH_TO_FILTER_LOCATION_XML);
		filter.filter(messages);
		
		messages.forEach(m -> {
			if (m.getLocationString().equals(LOCATION_PATTERN_WARN)) {
				fail("SingleValidationMessage mit LocationPattern [" + LOCATION_PATTERN_WARN  + "] muss gefiltert werden");
			}
		});
	}
	
	/**
	 * Testet {@linkplain DefaultMessageFilter#filter(List)} mit eine leeres Filter. 
	 * Wenn Methode liefert zurück eine leere Liste, der Test ist erfolgreich.
	 */
	@Test
	public void testEmptyFilterList() {
		RegExMessageFilter filter = this.loadFilter(PATH_TO_FILTER_EMPTY_XML);
	}
	
	/**
	 * Testet Methode {@linkplain DefaultMessageFilter#unmarshall(URL)} mit nullable parameter.
	 */
	@Test()
	public void testUnmarshallException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RegExMessageFilter(null);
        });
	}
	

	
	/**
	 * Testet Methode {@linkplain DefaultMessageFilter#filter(List)} mit nullable parameter.
	 */
	@Test()
	public void testFilterException() {
        assertThrows(IllegalArgumentException.class, () -> {
    		RegExMessageFilter filter = this.loadFilter(PATH_TO_FILTER_XML);
            filter.filter(null);
        });
	}
	
	/**
	 * Testet Methode {@linkplain DefaultMessageFilter#unmarshall(URL)} mit falsche URL. 
	 * Als Ergebniss wird RuntimeException erwartet.
	 * @throws MalformedURLException
	 */
	@Test()
	public void testUnmarshallRuntimeException() throws MalformedURLException {
        assertThrows(RuntimeException.class, () -> {
    		this.loadFilter(PATH_NOT_EXIST);
        });
	}

}
