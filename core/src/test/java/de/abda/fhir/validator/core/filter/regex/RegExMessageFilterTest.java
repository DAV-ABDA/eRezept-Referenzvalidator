package de.abda.fhir.validator.core.filter.regex;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.abda.fhir.validator.core.util.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testet die Implementierung der Klasse {@link RegExMessageFilter}
 *
 * @author Dzmitry Liashenka
 */
public class RegExMessageFilterTest {

    private static final String ISSUE_1 = "Issue-001";
    private static final String ISSUE_2 = "Issue-002";
    private static final String ISSUE_3 = "Issue-003";
    private static final String ISSUE_4 = "Issue-004";

    private final Logger logger = LoggerFactory.getLogger(RegExMessageFilter.class);

    private static final String PATH_TO_FILTER_XML = "/de/abda/fhir/validator/core/filter/regex/validation-filter.xml";

    private static final String PATH_TO_FILTER_SEVERITY_XML = "/de/abda/fhir/validator/core/filter/regex/validation-filter-severity.xml";

    private static final String PATH_TO_FILTER_MESSAGE_XML = "/de/abda/fhir/validator/core/filter/regex/validation-filter-message.xml";

    private static final String PATH_TO_FILTER_LOCATION_XML = "/de/abda/fhir/validator/core/filter/regex/validation-filter-location.xml";

    private static final String PATH_TO_FILTER_EMPTY_LIST_XML = "/de/abda/fhir/validator/core/filter/regex/validation-filter-empty-list.xml";
    private static final String PATH_TO_FILTER_EMPTY_FILTER_XML = "/de/abda/fhir/validator/core/filter/regex/validation-filter-empty-filter.xml";


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
        List<FilterDefinition> filterBeschreibungsListe = new ArrayList<>();

        FilterDefinition info = new FilterDefinition();
        info.setIssueId(ISSUE_1);
        info.setSeverityPattern(Pattern.compile(ResultSeverityEnum.INFORMATION.getCode()));
        info.setMessagePattern(Pattern.compile(MESSAGE_PATTERN_INFO));
        info.setLocationPattern(Pattern.compile(LOCATION_PATTERN_INFO));

        FilterDefinition warn = new FilterDefinition();
        warn.setIssueId(ISSUE_2);
        warn.setSeverityPattern(Pattern.compile(ResultSeverityEnum.WARNING.getCode()));
        warn.setMessagePattern(Pattern.compile(MESSAGE_PATTERN_WARN));
        warn.setLocationPattern(Pattern.compile(LOCATION_PATTERN_WARN));

        FilterDefinition error = new FilterDefinition();
        error.setIssueId(ISSUE_3);
        error.setSeverityPattern(Pattern.compile(ResultSeverityEnum.ERROR.getCode()));
        error.setMessagePattern(Pattern.compile(MESSAGE_PATTERN_ERROR));
        error.setLocationPattern(Pattern.compile(LOCATION_PATTERN_ERROR));

        FilterDefinition fatal = new FilterDefinition();
        fatal.setIssueId(ISSUE_4);
        fatal.setSeverityPattern(Pattern.compile(ResultSeverityEnum.FATAL.getCode()));
        fatal.setMessagePattern(Pattern.compile(MESSAGE_PATTERN_FATAL));
        fatal.setLocationPattern(Pattern.compile(LOCATION_PATTERN_FATAL));

        filterBeschreibungsListe.add(info);
        filterBeschreibungsListe.add(warn);
        filterBeschreibungsListe.add(error);
        filterBeschreibungsListe.add(fatal);

        // Attribute setzen!
        // Objekt Unmarshallen String > Bean
        RegExMessageFilter filter = this.loadFilter(PATH_TO_FILTER_XML);
        List<FilterDefinition> unmarshallList = filter.getFilterDefinitions();

        // Objekt Marshallen Bean > String
        StringWriter sw = new StringWriter();
        Writer writer = new PrintWriter(sw);

        filter.marshall(writer);
        logger.info("Marshalled filterlist \n{}", sw);

        // Alle Attribute Prüfen: filterListe vs. Bean (unmarshalled roundtrip)
        assertEquals(filterBeschreibungsListe.get(0).getSeverityPattern().pattern(), unmarshallList.get(0).getSeverityPattern().pattern());
        assertEquals(filterBeschreibungsListe.get(0).getMessagePattern().pattern(), unmarshallList.get(0).getMessagePattern().pattern());
        assertEquals(filterBeschreibungsListe.get(0).getLocationPattern().pattern(), unmarshallList.get(0).getLocationPattern().pattern());

        assertEquals(filterBeschreibungsListe.get(1).getSeverityPattern().pattern(), unmarshallList.get(1).getSeverityPattern().pattern());
        assertEquals(filterBeschreibungsListe.get(1).getMessagePattern().pattern(), unmarshallList.get(1).getMessagePattern().pattern());
        assertEquals(filterBeschreibungsListe.get(1).getLocationPattern().pattern(), unmarshallList.get(1).getLocationPattern().pattern());

        assertEquals(filterBeschreibungsListe.get(2).getSeverityPattern().pattern(), unmarshallList.get(2).getSeverityPattern().pattern());
        assertEquals(filterBeschreibungsListe.get(2).getLocationPattern().pattern(), unmarshallList.get(2).getLocationPattern().pattern());

        assertEquals(filterBeschreibungsListe.get(3).getSeverityPattern().pattern(), unmarshallList.get(3).getSeverityPattern().pattern());
        assertEquals(filterBeschreibungsListe.get(3).getMessagePattern().pattern(), unmarshallList.get(3).getMessagePattern().pattern());
        assertEquals(filterBeschreibungsListe.get(3).getLocationPattern().pattern(), unmarshallList.get(3).getLocationPattern().pattern());
    }

    /**
     * Testet die Implementation der Methode {@linkplain RegExMessageFilter#filter(List)}
     * In Test sollen die Messages mit Severity 'warning' und 'error' gefiltert werden.
     *
     * @throws IllegalArgumentException if param is null
     */
    @Test
    public void testSeverityPattern() throws IllegalArgumentException {
        RegExMessageFilter filter = this.loadFilter(PATH_TO_FILTER_SEVERITY_XML);
        filter.filter(messages);

        messages.forEach(m -> {
            if (m.getSeverity().equals(ResultSeverityEnum.WARNING)) {
                fail("SingleValidationMessage mit Severity [" + ResultSeverityEnum.WARNING + "] muss gefiltert werden");
            }
            if (m.getSeverity().equals(ResultSeverityEnum.ERROR)) {
                fail("SingleValidationMessage mit Severity [" + ResultSeverityEnum.ERROR + "] muss gefiltert werden");
            }
        });
    }

    private RegExMessageFilter loadFilter(String cpResource) {
        URL url = this.getClass().getResource(cpResource);
        return new RegExMessageFilter(url, new Profile("test", "test", "1.2.3"));
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
                fail("SingleValidationMessage mit Message [" + MESSAGE_PATTERN_ERROR + "] muss gefiltert werden");
            }
            if (m.getMessage().equals(MESSAGE_PATTERN_FATAL)) {
                fail("SingleValidationMessage mit Message [" + MESSAGE_PATTERN_FATAL + "] muss gefiltert werden");
            }
            if (m.getMessage().equals(MESSAGE_PATTERN_WARN)) {
                fail("SingleValidationMessage mit Message [" + MESSAGE_PATTERN_WARN + "] muss gefiltert werden");
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
                fail("SingleValidationMessage mit LocationPattern [" + LOCATION_PATTERN_WARN + "] muss gefiltert werden");
            }
        });
    }

    /**
     * Testet das Verhalten vom {@link RegExMessageFilter}, wenn eine Datei geladen wird, die einen Filter enthält, in
     * dem nichts gefiltert wird. In dem Fall dürfen keinen Messages  gefiltert werden.
     */
    @Test
    public void testNoPatternDefined() {
        RegExMessageFilter filter = this.loadFilter(PATH_TO_FILTER_EMPTY_FILTER_XML);
        int originalSize = messages.size();
        filter.filter(messages);
        assertEquals(originalSize, messages.size(), "Es sollte keine Message entfernt werden, da keine Patterns definiert sind.");
    }

    /**
     * Testet {@linkplain RegExMessageFilter#filter(List)} mit einer leeren Filterliste.
     */
    @Test
    public void testEmptyFilterList() {
        RegExMessageFilter filter = this.loadFilter(PATH_TO_FILTER_EMPTY_LIST_XML);
        int originalSize = messages.size();
        filter.filter(messages);
        assertEquals(originalSize, messages.size(), "Es sollte keine Message entfernt werden, da die Listeleer ist.");

    }

    /**
     * Testet Konstruktor {@linkplain RegExMessageFilter} mit nullable parameter.
     */
    @Test()
    public void testUnmarshallException() {
        assertThrows(NullPointerException.class, () -> new RegExMessageFilter(null, null));
    }


    /**
     * Testet Methode {@linkplain RegExMessageFilter#filter(List)} mit nullable parameter.
     */
    @Test()
    public void testFilterException() {
        assertThrows(IllegalArgumentException.class, () -> {
            RegExMessageFilter filter = this.loadFilter(PATH_TO_FILTER_XML);
            filter.filter(null);
        });
    }

    /**
     * Testet den Konstruktor {@linkplain RegExMessageFilter} mit falsche URL.
     * Als Ergebniss wird RuntimeException erwartet.
     *
     */
    @Test()
    public void testUnmarshallRuntimeException(){
        assertThrows(RuntimeException.class, () -> this.loadFilter(PATH_NOT_EXIST));
    }

}
