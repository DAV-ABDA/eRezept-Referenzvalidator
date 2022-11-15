package de.abda.fhir.validator.core.configuration;

public class FhirProfileVersion {

    private String version;
    private FhirPackageValidityPeriod validityPeriod;
    private FhirPackage requiredPackage;

    public FhirProfileVersion() {
    }

    public FhirProfileVersion(String version, FhirPackageValidityPeriod validityPeriod, FhirPackage requiredPackage) {
        this.version = version;
        this.validityPeriod = validityPeriod;
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

    public FhirPackageValidityPeriod getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(FhirPackageValidityPeriod validityPeriod) {
        this.validityPeriod = validityPeriod;
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
        if (!requiredPackage.equals(that.requiredPackage)) {
            return false;
        }
        return validityPeriod.equals(that.validityPeriod);
    }

    @Override
    public int hashCode() {
        int result = version != null ? version.hashCode() : 0;
        result = 31 * result + requiredPackage.hashCode();
        result = 31 * result + validityPeriod.hashCode();
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
