package de.abda.fhir.validator.core.configuration;

import de.abda.fhir.validator.core.util.Profile;
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

    public FhirPackageValidityPeriod getProfileValidityPeriod(Profile profile){
        FhirProfile myProfile = this.supportedProfiles.get(profile.getBaseCanonical());
        if (myProfile != null) {
            FhirProfileVersion myProfileVersion = myProfile.getVersions().get(profile.getVersion());
            if (myProfileVersion != null) {
                return myProfileVersion.getValidityPeriod();
            }
        }
        return null;
    }
}
