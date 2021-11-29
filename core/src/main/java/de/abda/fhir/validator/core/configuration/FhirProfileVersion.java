package de.abda.fhir.validator.core.configuration;

public class FhirProfileVersion {

    private String version;
    private FhirPackage requiredPackage;

    public FhirProfileVersion() {
    }

    public FhirProfileVersion(String version,
        FhirPackage requiredPackage) {
        this.version = version;
        this.requiredPackage = requiredPackage;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FhirProfileVersion that = (FhirProfileVersion) o;

        if (version != null ? !version.equals(that.version) : that.version != null) {
            return false;
        }
        return requiredPackage.equals(that.requiredPackage);
    }

    @Override
    public int hashCode() {
        int result = version != null ? version.hashCode() : 0;
        result = 31 * result + requiredPackage.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FhirProfileVersion{");
        sb.append("requiredPackage=").append(requiredPackage.getPackageName());
        sb.append(", version='").append(version).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
