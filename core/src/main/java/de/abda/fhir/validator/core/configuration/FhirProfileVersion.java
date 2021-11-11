package de.abda.fhir.validator.core.configuration;

public class FhirProfileVersion {

    private String version;
    private FhirPackage requiredPackage;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public FhirPackage getRequiredPackage() {
        return requiredPackage;
    }

    public void setRequiredPackage(FhirPackage requiredPackage) {
        this.requiredPackage = requiredPackage;
    }

}
