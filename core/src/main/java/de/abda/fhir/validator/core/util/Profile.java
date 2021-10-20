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

    public String getBaseCanonical() {
        return baseCanonical;
    }

    public String getVersion() {
        return version;
    }

}
