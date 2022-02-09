package de.abda.fhir.validator.core.filter.regex;

import java.util.Optional;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This class is responsible for marshalling and unmarshalling Patterns to Strings and vice versa.
 * All Patterns are compiled <em>case insensitive</em>
 *
 * @author Dzmitry Liashenka
 */
public class PatternTypeAdapter extends XmlAdapter<String, Pattern> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Pattern unmarshal(String value) {
        if (null == value) {
            return null;
        }
        return Pattern.compile(value, Pattern.CASE_INSENSITIVE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String marshal(Pattern value) {
        if (null == value) {
            return null;
        }
        return value.pattern();
    }
}
