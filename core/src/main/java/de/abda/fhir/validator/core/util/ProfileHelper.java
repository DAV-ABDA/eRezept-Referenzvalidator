package de.abda.fhir.validator.core.util;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileHelper {

    static Logger logger = LoggerFactory.getLogger(ProfileHelper.class);
    private static final WstxInputFactory inputFactory = new WstxInputFactory();
    private static final WstxOutputFactory outputFactory = new WstxOutputFactory();
    private static final String patternString = "(<profile\\s*value=['\"])([Hh][Tt][Tt][Pp][Ss]?://[^|'\"<>\\s#]+/StructureDefinition/[^|'\"<>\\s#]+)(\\|)([^'\"<>\\s#]+)(['\"]\\s*/?>)"; // Identify canonical profile URLs

    public static Profile getProfile(IBaseResource resource) {
        String canonical = resource.getMeta().getProfile().get(0).getValueAsString();
        return getProfileFromCanonical(canonical);
    }

    public static Profile getProfileFromCanonical(String canonical) {
        String[] splittedString = canonical.split("\\|");
        if (splittedString.length < 2)
        {
            //String defaultVersion = "0.0.0";
            return new Profile(canonical, splittedString[0], "0.0.0");
        }
        else
        {
            return new Profile(canonical, splittedString[0], splittedString[1]);
        }
    }

    public static Profile getProfileFromStringInput(String input) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return new Profile(
                    matcher.group(3) + matcher.group(4) + matcher.group(5),
                    matcher.group(3),
                    matcher.group(5)
            );
        }
        return null;
    }

    public static Profile getProfileFromFile(File file) { //TODO XMLBufferReader based implementation?
        Pattern pattern = Pattern.compile(patternString);
        try {
            BufferedReader brTest = new BufferedReader(new FileReader(file));
            boolean foundProfile = false;
            String input;
            Matcher matcher;
            while (foundProfile == false) {
                input = brTest.readLine();
                if (input == null) return null;
                matcher = pattern.matcher(input);
                if (matcher.find()) {
                    return new Profile(
                            matcher.group(2) + matcher.group(3) + matcher.group(4),
                            matcher.group(2),
                            matcher.group(4)
                    );
                }
            }
        } catch (Exception e) {
            logger.error("Could not determine Profile. " + e.getClass().getSimpleName() + " during profile search in file: " + file.getName());
        }
        return null;
    }

    public static Profile getProfileFromXmlStream(InputStream inputStream) {
        final XMLEventReader xmlEventReader;
        int level = 0; // InitialProfile = 1 for packageContext
        try {
            xmlEventReader = inputFactory.createXMLEventReader(inputStream);
            while (xmlEventReader.hasNext()) {
                XMLEvent nextTag = xmlEventReader.nextTag();
                if (level == 1 && nextTag.isStartElement() && "meta".equals(nextTag.asStartElement().getName().getLocalPart().toLowerCase())) {
                    while (xmlEventReader.hasNext()) {
                        nextTag = xmlEventReader.nextTag();
                        if (nextTag.isStartElement() && "profile".equals(nextTag.asStartElement().getName().getLocalPart().toLowerCase())) {
                            Attribute valueAttribute = nextTag.asStartElement().getAttributeByName(new QName("", "value", ""));
                            if (valueAttribute == null) {
                                logger.error("Could not read value attribute from meta/profile tag.");
                                return null;
                            }
                            String canonical = valueAttribute.getValue();
                            return getProfileFromCanonical(canonical);
                        }
                    }
                } else if (nextTag.isStartElement() && !nextTag.isEndElement()) {
                    level++;
                } else if (!nextTag.isStartElement() && nextTag.isEndElement()) {
                    if (level > 2) {
                        // Abbruch, da Angabe "meta" nur an ihrer Posi/Reihenfolge erfolgt -> eigentlich 2. Tag auf Profilebene
                        // ERROR Bundle.meta -> das Element "meta" außerhalb der definierten Reihenfolge
                        return null;
                    } else {
                        level--;
                    }
                }
            }
        } catch (XMLStreamException e) {
            logger.error("Could not determine profile.", e);
        }

        return null;
    }

    public static LocalDate getInstanceDateFromXmlStream(InputStream inputStream, String xmlPath) {
        final XMLEventReader xmlEventReader;
        int level = 0;
        int pos = 0;
        String[] xmlPathElements = xmlPath.split("\\s*;\\s*");
        try {
            xmlEventReader = inputFactory.createXMLEventReader(inputStream);
            while (xmlEventReader.hasNext()) {
                XMLEvent nextTag = xmlEventReader.nextTag();
                if (level == pos && nextTag.isStartElement() && xmlPathElements[pos].toLowerCase().contains(nextTag.asStartElement().getName().getLocalPart().toLowerCase())) {
                    if (pos < (xmlPathElements.length - 1)) {
                        pos++;
                        level++;
                    } else {
                        Attribute valueAttribute = nextTag.asStartElement().getAttributeByName(new QName("", "value", ""));
                        if (valueAttribute == null) {
                            logger.error("Could not read value attribute for date.");
                            return null;
                        }
                        // TODO: Zeitverschiebung beachten? DateTime Umwandlung ?!? --> TemporalAccessor
                        return LocalDate.parse(valueAttribute.getValue().substring(0,10)); // TODO: was wenn kein Date oder anders formatiert?
                    }
                } else if (level == pos && nextTag.isEndElement() && xmlPathElements[pos].toLowerCase().contains(nextTag.asEndElement().getName().getLocalPart().toLowerCase())) {
                    if (pos < (xmlPathElements.length - 1)) { // TODO: Abbruch wenn pos = xmlPathElements.length -2 und pos-- !!!!!
                        pos--;
                        level--;
                    }
                } else if (nextTag.isStartElement() && !nextTag.isEndElement()) {
                    level++;
                } else if (!nextTag.isStartElement() && nextTag.isEndElement()) {
                    level--;
                }
/*
                if (nextTag.isStartElement() || nextTag.isStartElement()) {
                    logger.error("level:" + level + " pos:" + pos + " Element: " + nextTag.asStartElement().getName().getLocalPart());
                }
                if (level < pos) { // level < pos = abbruch
                    logger.error("level < pos");
                    //return null;
                }
*/
            }
        } catch (XMLStreamException e) {
            logger.error("Could not instance date.", e);
        }
        return null;
    }
}
