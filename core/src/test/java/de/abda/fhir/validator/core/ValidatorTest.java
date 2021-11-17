package de.abda.fhir.validator.core;

import static org.junit.jupiter.api.Assertions.*;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import org.junit.jupiter.api.Test;

/**
 * Unittest for {@link Validator}
 * @author RupprechJo
 */
class ValidatorTest {

  @Test
  void testInitialization(){
    new Validator(new FhirValidator(FhirContext.forR4()));
  }
}
