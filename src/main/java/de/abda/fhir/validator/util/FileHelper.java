package de.abda.fhir.validator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHelper {

    static Logger logger = LoggerFactory.getLogger(FileHelper.class);

    public static String loadValidatorInput(String inputPath) {
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
}
