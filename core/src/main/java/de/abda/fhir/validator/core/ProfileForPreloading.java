package de.abda.fhir.validator.core;

/**
 * Enum with all supported Profiles, that can be used for proloading validators
 * @author RupprechJo
 */
public enum ProfileForPreloading {
  KBV_PR_ERP_BUNDLE("https://fhir.kbv.de/StructureDefinition/KBV_PR_ERP_Bundle"),
  ERX_MEDICATION_DISPENSE("https://gematik.de/fhir/StructureDefinition/ErxMedicationDispense"),
  GEM_ERP_PR_MedicationDispense("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_MedicationDispense"),
  GEM_ERP_PR_CloseOperationInputBundle("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_CloseOperationInputBundle"),
  ERX_RECEIPT("https://gematik.de/fhir/StructureDefinition/ErxReceipt"),
  GEM_ERP_PR_Bundle("https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_Bundle"),
  GEM_ERPCHRG("https://gematik.de/fhir/erpchrg/StructureDefinition/GEM_ERPCHRG_PR_ChargeItem"),
  GEM_ERPCONSENT("https://gematik.de/fhir/erpchrg/StructureDefinition/GEM_ERPCHRG_PR_Consent"),
  DAV_PR_ERP_ABGABEDATEN_BUNDLE("http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-PR-ERP-AbgabedatenBundle"),
  DAV_PKV_PR_ERP_ABGABEDATEN_BUNDLE("http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-PKV-PR-ERP-AbgabedatenBundle"),
  GKVSV_PR_TA7_SAMMELRECHNUNG("https://fhir.gkvsv.de/StructureDefinition/GKVSV_PR_TA7_Sammelrechnung_Bundle"),
  GKVSV_PR_TA7_RECHNUNG("https://fhir.gkvsv.de/StructureDefinition/GKVSV_PR_TA7_Rechnung_Bundle");

  private String canonical;

  ProfileForPreloading(String canonical) {
    this.canonical = canonical;
  }

  public String getCanonical() {
    return canonical;
  }
}
