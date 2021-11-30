package de.abda.fhir.validator.core;

/**
 * Enum with all supported Profiles, that can be used for proloading validators
 * @author RupprechJo
 */
public enum ProfileForPreloading {
  KBV_PR_ERP_BUNDLE("https://fhir.kbv.de/StructureDefinition/KBV_PR_ERP_Bundle"),
  ERX_MEDICATION_DISPENSE(
      "https://gematik.de/fhir/StructureDefinition/ErxMedicationDispense"),
  ERX_RECEIPT("https://gematik.de/fhir/StructureDefinition/ErxReceipt"),
  DAV_PR_ERP_ABGABEDATEN_BUNDLE(
      "http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-PR-ERP-AbgabedatenBundle"),
  GKVSV_PR_TA7_SAMMELRECHNUNG(
      "https://fhir.gkvsv.de/StructureDefinition/GKVSV_PR_TA7_Sammelrechnung_Bundle");

  private String canonical;

  ProfileForPreloading(String canonical) {
    this.canonical = canonical;
  }

  public String getCanonical() {
    return canonical;
  }
}
