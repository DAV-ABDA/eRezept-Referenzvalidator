package de.abda.fhir.validator.core;

import static de.abda.fhir.validator.core.ProfileForPreloading.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author RupprechJo
 */
public class ReferenceValidatorPreloadTest {

  @Test
  void testPreload() {
    ReferenceValidator validator = new ReferenceValidator();
    validator.preloadAllSupportedValidators(KBV_PR_ERP_BUNDLE);
    validator.preloadAllSupportedValidators(ERX_RECEIPT, DAV_PR_ERP_ABGABEDATEN_BUNDLE);
  }
  @Disabled // TODO: Fehlverhalten Endlosschleife bei Generierung TA7 SnapShot
  @Test void testSammelrechnung(){
    ReferenceValidator validator = new ReferenceValidator();
    validator.preloadAllSupportedValidators(GKVSV_PR_TA7_SAMMELRECHNUNG);
  }

}
