package de.abda.fhir.validator.core.configuration;

import java.util.Map;

public class FhirProfile {

    private String profileName;
    private Map<String, FhirProfileVersion> versions;

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Map<String, FhirProfileVersion> getVersions() {
        return versions;
    }

    public void setVersions(Map<String, FhirProfileVersion> versions) {
        this.versions = versions;
    }

}
