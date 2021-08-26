package de.abda.fhir.validator.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unittest for {@link Validator}
 * @author RupprechJo
 */
class ValidatorTest {

  @Test
  void testInitialization(){
    new Validator();
  }
}
