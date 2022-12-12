package de.abda.fhir.validator.core;

import org.junit.jupiter.api.Test;

import static de.abda.fhir.validator.core.ProfileForPreloading.*;

/**
 * @author RupprechJo
 */
public class ReferenceValidatorPreloadTest {

  @Test
  void testPreload() {
    ReferenceValidator validator = new ReferenceValidator();
    validator.preloadAllSupportedValidators(KBV_PR_ERP_BUNDLE);
    validator.preloadAllSupportedValidators(ERX_MEDICATION_DISPENSE);
    validator.preloadAllSupportedValidators(ERX_RECEIPT);
    validator.preloadAllSupportedValidators(DAV_PR_ERP_ABGABEDATEN_BUNDLE);
    validator.preloadAllSupportedValidators(GKVSV_PR_TA7_SAMMELRECHNUNG);
    validator.preloadAllSupportedValidators(GEM_ERP_PR_MedicationDispense);
    validator.preloadAllSupportedValidators(GEM_ERP_PR_Bundle);
    validator.preloadAllSupportedValidators(GKVSV_PR_TA7_RECHNUNG);
    validator.preloadAllSupportedValidators(GEM_ERPCHRG, DAV_PKV_PR_ERP_ABGABEDATEN_BUNDLE);
    validator.preloadAllSupportedValidators(KBV_PR_ERP_BUNDLE, ERX_RECEIPT, GEM_ERP_PR_Bundle, DAV_PR_ERP_ABGABEDATEN_BUNDLE, GKVSV_PR_TA7_RECHNUNG);
  }

}
