package de.abda.fhir.validator.core.filter;

import de.abda.fhir.validator.core.ValidationResult;
import de.abda.fhir.validator.core.filter.regex.FilterDefinitionList;
import de.abda.fhir.validator.core.filter.regex.NonFilteringMessageFilter;
import de.abda.fhir.validator.core.filter.regex.RegExMessageFilter;
import de.abda.fhir.validator.core.util.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Custom Filter engine which loads all filter rules from xml files, for each profile specified by {@link FilterEngine#VALIDATION_FILTER_PROPERTIES}.
 * Provides a version specific {@link MessageFilter} for each profile.
 * @author Frank Jesgarz
 */
public class FilterEngine {
    private static final String MESSAGE_NO_FILTER_DEFINED = "Es wurde keine Filter für das Profil {} definiert. Die Messages für dieses Profil werden nicht gefiltert.";
    public static final String VALIDATION_FILTER_PROPERTIES = "/de/abda/fhir/validator/core/filter/regex/validationFilter.properties";
    private static final String ERROR_CANT_READ_PROPERTIES = "Fehler beim Einlesen der Properties aus der Quelle:{}).";
    private static final Logger logger = LoggerFactory.getLogger(FilterEngine.class);
    private Properties profileFilters;
    private final Map<Profile, MessageFilter> messageFilters = new HashMap<>();

    /**
     * Constructor
     */
    public FilterEngine() {
        loadValidationFilterProperties();
    }


    /**
     * Loads a properties file which defines which {@link  FilterDefinitionList} is to be used for each FHIR profile.
     * The key of each property is the profile including the profile version (if present). The value is the path
     * to the XMl file containing the filter rules.
     */
    private void loadValidationFilterProperties() {
      //  ResourceBundle rb = ResourceBundle.getBundle("myResource", Locale.getDefault(), loader);
        profileFilters = new Properties();
        URL propertiesUrl = Objects.requireNonNull(this.getClass().getResource(VALIDATION_FILTER_PROPERTIES));
        try (InputStream is = propertiesUrl.openStream()) {
            profileFilters.load(is);
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException(String.format(ERROR_CANT_READ_PROPERTIES, propertiesUrl), e);
        }
    }

    /**
     * Filters out the validation messages of the validationResult according to the defined filter rules
     * and returns a {@link ValidationResult}, that contains the remaining validation messages as well as a List
     * of {@link FilteredMessage}, containing an entry for each message that has been filtered. If no {@link MessageFilter} was defined for the
     * given profile, the {@link NonFilteringMessageFilter} is used, which does not apply any filtering.
     * @param profile the profile according to which the input resource was filtered
     * @param validationResult the validation result
     * @return the result of the filtering operations
     */
    public ValidationResult filterMessages(Profile profile, ca.uhn.fhir.validation.ValidationResult validationResult) {
        MessageFilter messageFilter = loadFilterForProfile(profile);
        return messageFilter.filter(new ArrayList<>(validationResult.getMessages()));
    }

    /**
     * Returns an {@link MessageFilter} for the passed in {@link Profile} which can be used to filter a List of
     * Validation Messages.
     *
     * @param profile the profile
     * @return a {@link MessageFilter} for the profile
     */
    private MessageFilter loadFilterForProfile(Profile profile) {
        MessageFilter filter;
        if (messageFilters.containsKey(profile)) {
            filter = messageFilters.get(profile);
        } else {
            String profileAndVersion = profile.getCanonical().substring(profile.getCanonical().lastIndexOf('/') + 1);

            String filterXML = profileFilters.getProperty(profileAndVersion);
            if (filterXML != null) {
                URL url = this.getClass().getResource(filterXML);
                filter = new RegExMessageFilter(url, profile);
            } else {
                logger.warn(MESSAGE_NO_FILTER_DEFINED, profile.getCanonical());
                filter = new NonFilteringMessageFilter(profile);
            }
            messageFilters.put(profile, filter);
        }
        return filter;

    }
}
