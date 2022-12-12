package de.abda.fhir.validator.core.util;

import de.abda.fhir.validator.core.configuration.FhirPackageValidityPeriod;

import java.time.LocalDate;
import java.util.Objects;

public class ProfileValidityDate {
    Profile profile;
    FhirPackageValidityPeriod validityPeriod;

    LocalDate instanceDate;

    public ProfileValidityDate(Profile profile) {
        this.profile = profile;
        this.validityPeriod = null;
        this.instanceDate = null;
    }

    public ProfileValidityDate(Profile profile, FhirPackageValidityPeriod validityPeriod, LocalDate instanceDate) {
        this.profile = profile;
        this.validityPeriod = validityPeriod;
        this.instanceDate = instanceDate;
    }

    public Profile getProfile() {
        return profile;
    }
    public FhirPackageValidityPeriod getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(FhirPackageValidityPeriod validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public LocalDate getInstanceDate() {
        return instanceDate;
    }

    public void setInstanceDate(LocalDate instanceDate) {
        this.instanceDate = instanceDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileValidityDate that = (ProfileValidityDate) o;
        return Objects.equals(profile, that.profile) && Objects.equals(validityPeriod, that.validityPeriod) && Objects.equals(instanceDate, that.instanceDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profile, validityPeriod, instanceDate);
    }
}
