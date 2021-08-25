package de.abda.fhir.validator.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.StrictErrorHandler;
import ca.uhn.fhir.parser.XmlParser;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHelper {

    static Logger logger = LoggerFactory.getLogger(FileHelper.class);

    public static String loadValidatorInputAsString(String inputPath) {
        try {
            String inputString = Files.readString(Path.of(inputPath));
            inputString = InputHelper.removeVersionInCanonicals(inputString);
            return inputString;
        } catch (IOException e) {
            logger.error("Angegebene Datei \"" + inputPath + "\" konnte nicht gefunden werden.");
            System.exit(404);
            return null;
        }
    }

    public static IBaseResource loadValidatorInputAsResource(String inputPath, FhirContext ctx) {
        try {
            XmlParser parser = new XmlParser(ctx, new StrictErrorHandler());
            return parser.parseResource(Files.newInputStream(Path.of(inputPath)));
        } catch (IOException e) {
            logger.error("Angegebene Datei \"" + inputPath + "\" konnte nicht gefunden werden.");
            System.exit(404);
            return null;
        }
    }
}
