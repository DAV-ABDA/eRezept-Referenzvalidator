package de.abda.fhir.validator.core.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.StrictErrorHandler;
import ca.uhn.fhir.parser.XmlParser;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHelper {

    static Logger logger = LoggerFactory.getLogger(FileHelper.class);

    public static String loadValidatorInputAsString(String inputPath) {
        return loadValidatorInputAsString(inputPath, true);
    }

    public static String loadValidatorInputAsString(String inputPath, Boolean removeVersion) {
        try {
            String inputString = FileUtils.readFileToString(new File(inputPath), StandardCharsets.UTF_8);
            if (removeVersion) {
                inputString = InputHelper.removeVersionInCanonicals(inputString);
            }
            return inputString;
        } catch (IOException e) {
            logger.error("Angegebene Datei \"" + inputPath + "\" konnte nicht gefunden werden.", e);
            throw new RuntimeException(e);
        }
    }

    public static IBaseResource loadValidatorInputAsResource(String inputPath, FhirContext ctx) {
        try {
            XmlParser parser = new XmlParser(ctx, new StrictErrorHandler());
            return parser.parseResource(Files.newInputStream(Paths.get(inputPath)));
        } catch (IOException e) {
            logger.error("Angegebene Datei \"" + inputPath + "\" konnte nicht gefunden werden.", e);
            throw new RuntimeException(e);
        }
    }
}
