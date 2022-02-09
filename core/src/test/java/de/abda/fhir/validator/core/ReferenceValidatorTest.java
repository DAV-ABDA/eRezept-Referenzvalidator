package de.abda.fhir.validator.core;

import de.abda.fhir.validator.core.exception.ValidatorInitializationException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.apache.commons.lang3.tuple.Pair.of;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unittest for {@link ReferenceValidator}
 *
 * @author RupprechJo
 */
class ReferenceValidatorTest {

  private static final Path VALID_BASE_DIR = Paths.get("src/test/resources/valid");
  private static final Path INVALID_BASE_DIR = Paths.get("src/test/resources/invalid");
  private static final Path INVALID_BULK_DIR = Paths.get("src/test/resources/invalid/bulk");
  private static final Path EXCEPTION_BASE_DIR = Paths.get("src/test/resources/exception");

  static ReferenceValidator validator = new ReferenceValidator();

  @ParameterizedTest
  @MethodSource
  void validateValidFile(Path path) throws Exception {
    ValidationResult result = validator
            .validateFile(path);
//    String mapAsString = errors.keySet().stream()
//        .map(key -> key + ": " + errors.get(key).size())
//        .collect(Collectors.joining(","));
    System.out.println(marshall(result));
    assertTrue(result.isValid());
  }

  private static Stream<Path> validateValidFile() throws IOException {
    return Files.walk(VALID_BASE_DIR).filter(path -> path.toString().endsWith(".xml"));
  }

  @ParameterizedTest
  @MethodSource
  void validateInvalidFile(Pair<Path, String> arguments) throws Exception {
    ValidationResult result = validator
            .validateFile(arguments.getKey());
//    String mapAsString = errors.keySet().stream()
//        .map(key -> key + ": " + errors.get(key).size())
//        .collect(Collectors.joining(","));
    System.out.println(marshall(result));
    assertFalse(result.isValid());
  }

  private static Stream<Pair<Path, String>> validateInvalidFile() throws IOException {
    return Stream.of(
        of(INVALID_BASE_DIR.resolve("InvalidEprescriptionBundle1.xml"),
            "Der Wert ist \"https://fhir.kbv.de/CodeSystem/Wrong\", muss aber \"https://fhir.kbv.de/CodeSystem/KBV_CS_ERP_Section_Type\" sein")
    );
  }

  @ParameterizedTest
  @MethodSource
  void validateInvalidFileBulk(Path path) throws Exception {
    ValidationResult result = validator
            .validateFile(path);
//    String mapAsString = errors.keySet().stream()
//            .map(key -> key + ": " + errors.get(key).size())
//            .collect(Collectors.joining(","));
    System.out.println(marshall(result));
    assertFalse(result.isValid());
  }

  private static Stream<Path> validateInvalidFileBulk() throws IOException {
    return Files.walk(INVALID_BULK_DIR).filter(path -> path.toString().endsWith(".xml"));
  }

  @ParameterizedTest
  @MethodSource
  void validateFileWithException(Pair<Path, Class<? extends Exception>> arguments) {
    Assertions.assertThrows(arguments.getRight(), () -> validator.validateFile(arguments.getKey()));
  }

  private static Stream<Pair<Path, Class<? extends Exception>>> validateFileWithException(){
    return Stream.of(
        of(EXCEPTION_BASE_DIR.resolve("NotExistingBundle.xml"),
            ValidatorInitializationException.class),
        of(EXCEPTION_BASE_DIR.resolve("NotExistingBundleVersion.xml"),
            ValidatorInitializationException.class)

    );
  }

  private String marshall(ValidationResult validationResult) throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(ValidationResult.class);
    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    StringWriter sw = new StringWriter();
    jaxbMarshaller.marshal(validationResult, sw);
    return sw.toString();
  }
}
