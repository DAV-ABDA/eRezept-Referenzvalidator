package de.abda.fhir.validator.core.exception;

/**
 * A RuntimeException thrown, if an error occurs on initialization of the validator
 * @author RupprechJo
 */
public class ValidatorInitializationException extends RuntimeException{

  public ValidatorInitializationException(String message) {
    super(message);
  }

  public ValidatorInitializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
