package de.abda.fhir.validator.core.configuration;

import java.util.List;

public class FhirPackageVersion {

    private String version;
    private String filename;
    private List<FhirPackage> dependencies;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<FhirPackage> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<FhirPackage> dependencies) {
        this.dependencies = dependencies;
    }

}
