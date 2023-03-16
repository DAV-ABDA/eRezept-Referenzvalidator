package de.abda.fhir.validator.core;

import ca.uhn.fhir.context.FhirContext;
import de.abda.fhir.validator.core.support.PipedCanonicalCoreResourcesValidationSupport;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unittest for {@link ReferenceValidator}
 *
 * @author RupprechJo
 */
class PipedCanonicalCoreResourcesValidationSupportTests {

  public static final FhirContext CTX = FhirContext.forR4();
  public static final PipedCanonicalCoreResourcesValidationSupport VALIDATION_SUPPORT = new PipedCanonicalCoreResourcesValidationSupport(CTX);

  @Test
  void testPipedCanonicalIsFetchedCorrectly() {
      String canonical = "http://hl7.org/fhir/StructureDefinition/Binary|4.0.1";
      StructureDefinition resource = VALIDATION_SUPPORT.fetchResource(StructureDefinition.class, canonical);
      Assertions.assertNotNull(resource);
      Assertions.assertEquals(canonical, String.format("%s|%s", resource.getUrl(), resource.getVersion()));
  }

  @Test
  void testNonPipedCanonicalsAreIgnored() {
      String canonical = "http://hl7.org/fhir/StructureDefinition/Binary";
      StructureDefinition resource = VALIDATION_SUPPORT.fetchResource(StructureDefinition.class, canonical);
      Assertions.assertNull(resource);
  }
}
