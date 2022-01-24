package de.abda.fhir.validator.core.filter.regex;

import java.util.Optional;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Die Klasse wird mit der Methode {@linkplain PatternTypeAdapter#marshal(Optional)} ein Pattern zum String konvertieren.
 * Mit der Methode {@linkplain PatternTypeAdapter#unmarshal(String)} wird ein String ('Case_Insensitive') zum Pattern konvertieren. 
 * @author Dzmitry Liashenka
 */
public class PatternTypeAdapter extends XmlAdapter<String,Optional<Pattern>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Pattern> unmarshal(String value) throws Exception {
	    if (null == value) {
	        return Optional.empty();
	    }
		return Optional.of( Pattern.compile(value, Pattern.CASE_INSENSITIVE));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String marshal(Optional<Pattern> value) throws Exception {
        if (null == value || false == value.isPresent()) {
            return null;
        }
		return value.get().pattern();
	}
}
