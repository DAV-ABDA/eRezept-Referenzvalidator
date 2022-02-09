package de.abda.fhir.validator.core.filter.regex;

import ca.uhn.fhir.validation.ResultSeverityEnum;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Manueller Test zum Testen des Marshallings
 */
@Ignore
class FilterDefinitionListTest {
    private static final String ISSUE_1 = "Issue-001";
    private static final String MESSAGE_PATTERN_INFO = "testInformationMessage";
    private static final String LOCATION_PATTERN_INFO = "testLocationInformation";
    private final Logger logger = LoggerFactory.getLogger(FilterDefinitionListTest.class);




    @Test
    public void marshallUnmarshall() throws Exception {
        List<FilterDefinition> filterDefinitions = new ArrayList<>();
        FilterDefinition info = new FilterDefinition();
        info.setIssueId(ISSUE_1);
        info.setSeverityPattern(Pattern.compile(ResultSeverityEnum.INFORMATION.getCode()));
        info.setMessagePattern(Pattern.compile(MESSAGE_PATTERN_INFO));
        info.setLocationPattern(Pattern.compile(LOCATION_PATTERN_INFO));
        filterDefinitions.add(info);
        FilterDefinitionList filterDefinitionList = new FilterDefinitionList();
        filterDefinitionList.setFilterDefinitionList(filterDefinitions);
        StringWriter sw = new StringWriter();
        Writer writer = new PrintWriter(sw);
        marshall(writer, filterDefinitionList);
        logger.warn("Marshalled filterlist \n{}", sw);
        FilterDefinitionList result = unmarshall(sw.toString());
        logger.warn("unmarshalled result \n{}", result);

    }

    private void marshall(Writer writer , FilterDefinitionList filterDefinitions) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(FilterDefinitionList.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(filterDefinitions, writer);
    }

    private FilterDefinitionList unmarshall(String input) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(FilterDefinitionList.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        StringReader reader = new StringReader(input);
        return (FilterDefinitionList) jaxbUnmarshaller.unmarshal(reader);
        }
}