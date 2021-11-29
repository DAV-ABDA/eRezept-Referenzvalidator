package de.abda.fhir.validator.core;

import org.junit.jupiter.api.Test;

/**
 * @author RupprechJo
 */
public class ReferenceValidatorPreloadTest {

  @Test
  void testPreload(){
    ReferenceValidator validator = new ReferenceValidator();
    validator.preloadAllSupportedValidators();
  }

}
