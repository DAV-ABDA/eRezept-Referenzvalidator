package de.abda.fhir.validator.core.configuration;

public class FhirPackage {

    private String packageName;
    private String packageVersion;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FhirPackage that = (FhirPackage) o;

        if (!packageName.equals(that.packageName)) return false;
        return packageVersion.equals(that.packageVersion);
    }

    @Override
    public int hashCode() {
        int result = packageName.hashCode();
        result = 31 * result + packageVersion.hashCode();
        return result;
    }
}
