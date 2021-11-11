package de.abda.fhir.validator.core.configuration;

import java.util.Map;

public class FhirPackageInfo {

    private String packageName;
    private Map<String,FhirPackageVersion> versions;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Map<String, FhirPackageVersion> getVersions() {
        return versions;
    }

    public void setVersions(Map<String, FhirPackageVersion> versions) {
        this.versions = versions;
    }

}
