package de.abda.fhir.validator.core.filter.regex;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import ca.uhn.fhir.validation.SingleValidationMessage;
import de.abda.fhir.validator.core.ValidationResult;
import de.abda.fhir.validator.core.filter.FilteredMessage;
import de.abda.fhir.validator.core.filter.MessageFilter;
import de.abda.fhir.validator.core.util.Profile;
import org.apache.commons.io.FilenameUtils;
import org.xml.sax.SAXException;

/**
 * Default implementation of {@link MessageFilter}.
 * This implementation uses regex checks to test if the list of {@link SingleValidationMessage} needs to be filtered
 * according to a list of rules defined in {@link FilterDefinitionList}
 *
 * @author Dzmitry Liashenka
 * @author Georg Tsakumagos
 * @author Frank Jesgarz
 */
public class RegExMessageFilter implements MessageFilter {
    private final static Logger LOGGER = Logger.getLogger(RegExMessageFilter.class.getName());

    private static final String ERROR_URL_NULL = "Die übergebene URL darf nicht NULL sein.";
    private static final String ERROR_PROFILE_NULL = "Das übergebene Profil darf nicht NULL sein.";

    private static final String ERROR_MESSAGES_NULL = "Die übergebene Liste mit SingleValidationMessages darf nicht NULL sein.";
    private static final String ERROR_UNMARSHALL_FILTER = "Fehler beim Einlesen der Filterdefinition %s.";
    private static final String DEBUG_MESSAGE_FILTERED = "'%01$s' message wird gefiltert.";
    private static final String ERROR_WRITER_NULL = "Die übergebene Writer darf nicht NULL sein.";
    private static final String ERROR_CANT_WRITE_FILE = "Fehler beim Schreiben der FilterBeschreibungsListe.";

    private final List<FilterDefinition> filterDefinitions;
    private Profile profile;
    private URL url;


    /**
     * Default Konstruktor
     */
    public RegExMessageFilter() throws RuntimeException {
        this.filterDefinitions = Collections.emptyList();
    }

    /**
     * Konstruktor
     *
     * @param url path to an XML file with a {@link FilterDefinitionList}. Must not be <code>NULL</code>
     * @param profile the profile that is filtered by this Filter. Must not be <code>NULL</code>
     * @throws IllegalArgumentException is the parameter is <code>null</code>.
     * @throws RuntimeException         in case of an expception while reading the file
     */
    public RegExMessageFilter(URL url, Profile profile) throws IllegalArgumentException, RuntimeException {

        this.url = Objects.requireNonNull(url, ERROR_URL_NULL);
        this.profile = Objects.requireNonNull(profile, ERROR_PROFILE_NULL);

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(FilterDefinitionList.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            jaxbUnmarshaller.setSchema(generateSchema(jaxbContext));

            try (InputStream is = url.openStream()) {
                FilterDefinitionList result = jaxbUnmarshaller
                        .unmarshal(new StreamSource(is), FilterDefinitionList.class).getValue();
				if ( null == result.getFilterDefinitionList()) {
					this.filterDefinitions = Collections.emptyList();
				} else {
					this.filterDefinitions = Collections.unmodifiableList(result.getFilterDefinitionList());
				}
            }
        } catch (final Throwable e) {
            throw new RuntimeException(String.format(ERROR_UNMARSHALL_FILTER, url), e);
        }
    }

    /**
     * generates Schema for the validation of input files from class file annotations
     * @param jaxbContext the jaxb context the schema is supposed to be gebneraded for
     * @return the schema
     */
   private Schema generateSchema(JAXBContext jaxbContext) throws IOException, SAXException {
        final List<ByteArrayOutputStream> outs = new ArrayList<>();
        jaxbContext.generateSchema(new SchemaOutputResolver(){
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                outs.add(out);
                StreamResult streamResult = new StreamResult(out);
                streamResult.setSystemId("");
                return streamResult;
            }});
        StreamSource[] sources = new StreamSource[outs.size()];
        for (int i=0; i<outs.size(); i++) {
            ByteArrayOutputStream out = outs.get(i);
            sources[i] = new StreamSource(new ByteArrayInputStream(out.toByteArray()),"");
        }
        SchemaFactory sf = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
        return sf.newSchema(sources);

    }



    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationResult filter(List<SingleValidationMessage> messages) throws IllegalArgumentException {
        if (null == messages) {
            throw new IllegalArgumentException(ERROR_MESSAGES_NULL);
        }
        List<FilteredMessage> filterEvents = new ArrayList<>();
        for (FilterDefinition filterDefinition : this.filterDefinitions) {
            final Iterator<SingleValidationMessage> messageIterator = messages.iterator();
            while (messageIterator.hasNext()) {
                SingleValidationMessage next = messageIterator.next();
                if (doFilter(filterDefinition, next)) {
                    filterEvents.add(new FilteredMessage(filterDefinition, next));
                    messageIterator.remove();
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine(String.format(DEBUG_MESSAGE_FILTERED, next));
                    }
                }
            }
        }
        return new ValidationResult(messages,this, filterEvents, this.profile);
    }

    /**
     * Checks if the message needs to be filtered regarding the {@link FilterDefinition}
     *
     * @param filterBeschreibung      The filter definitions
     * @param singleValidationMessage the validationMessage to be checked
     * @return false, if there is no rule defined in {@link FilterDefinition or if one of the defined rules is not matched, true otherwise;
     */
    private boolean doFilter(FilterDefinition filterBeschreibung, SingleValidationMessage singleValidationMessage) {
        if (!filterBeschreibung.hasPatterns()) {
            return false;
        } else {
            return patternNullOrMatching(filterBeschreibung.getSeverityPattern(), singleValidationMessage.getSeverity().name())
                    && patternNullOrMatching(filterBeschreibung.getMessagePattern(), singleValidationMessage.getMessage())
                    && patternNullOrMatching(filterBeschreibung.getLocationPattern(), singleValidationMessage.getLocationString());
        }
    }

    private boolean patternNullOrMatching(Pattern pattern, String value) {
        return pattern == null || pattern.matcher(value).matches();
    }

    /**
     * Returns an unmodifiable List of {@link FilterDefinition} objects
     *
     * @return the list. Is never <code>null</code>.
     */
    public List<FilterDefinition> getFilterDefinitions() {
        return this.filterDefinitions;
    }

    /**
     * Marshalls the {@linkplain FilterDefinitionList} to the writer
     *
     * @param writer the writer
     * @throws IllegalArgumentException if the parameter is <code>null</code>
     */
    public void marshall(Writer writer) throws IllegalArgumentException {
        if (null == writer) {
            throw new IllegalArgumentException(ERROR_WRITER_NULL);
        }

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(FilterDefinitionList.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            FilterDefinitionList filterList = new FilterDefinitionList();
            filterList.setFilterDefinitionList(filterDefinitions);

            jaxbMarshaller.marshal(filterList, writer);

        } catch (final Throwable exception) {
            throw new RuntimeException(ERROR_CANT_WRITE_FILE, exception);
        }
    }

    public URL getUrl() {
        return url;
    }

    @Override
    public String toString() {
       String fileName =  FilenameUtils.getName(url.getPath());
        return "RegExMessageFilter{" +
                "source=" + fileName +
                '}';
    }
}
