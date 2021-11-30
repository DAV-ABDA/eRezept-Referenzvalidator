package de.abda.fhir.validator.core.util;

public class Profile {

    private String canonical;
    private String baseCanonical;
    private String version;

    public Profile(String canonical, String baseCanonical, String version) {
        this.canonical = canonical;
        this.baseCanonical = baseCanonical;
        this.version = version;
    }

    public String getCanonical() {
        return canonical;
    }

    public void setCanonical(String canonical) {
        this.canonical = canonical;
    }

    public String getBaseCanonical() {
        return baseCanonical;
    }

    public void setBaseCanonical(String baseCanonical) {
        this.baseCanonical = baseCanonical;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Profile profile = (Profile) o;

        if (!canonical.equals(profile.canonical)) return false;
        if (!baseCanonical.equals(profile.baseCanonical)) return false;
        return version.equals(profile.version);
    }

    @Override
    public int hashCode() {
        int result = canonical.hashCode();
        result = 31 * result + baseCanonical.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Profile{");
        sb.append("canonical='").append(canonical).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
