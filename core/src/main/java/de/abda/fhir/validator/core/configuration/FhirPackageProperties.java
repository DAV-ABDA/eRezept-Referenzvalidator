package de.abda.fhir.validator.core.configuration;

import java.util.Map;

public class FhirPackageProperties {

    private Map<String, FhirPackageInfo> fhirPackages;
    private Map<String, FhirProfile> supportedProfiles;

    public Map<String, FhirPackageInfo> getFhirPackages() {
        return fhirPackages;
    }

    public void setFhirPackages(Map<String, FhirPackageInfo> fhirPackages) {
        this.fhirPackages = fhirPackages;
    }

    public Map<String, FhirProfile> getSupportedProfiles() {
        return supportedProfiles;
    }

    public void setSupportedProfiles(Map<String, FhirProfile> supportedProfiles) {
        this.supportedProfiles = supportedProfiles;
    }

}
