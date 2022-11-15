package de.abda.fhir.validator.core.configuration;

import java.time.LocalDate;
import java.util.Objects;

public class FhirPackageValidityPeriod {
    private LocalDate valid_from;
    private LocalDate valid_to;
    private String fhir_path;

    public LocalDate getValid_from() {
        return valid_from;
    }

    public void setValid_from(LocalDate valid_from) {
        this.valid_from = valid_from;
    }

    public LocalDate getValid_to() {
        return valid_to;
    }

    public void setValid_to(LocalDate valid_to) {
        this.valid_to = valid_to;
    }

    public String getFhir_path() {
        return fhir_path;
    }

    public void setFhir_path(String fhir_path) {
        this.fhir_path = fhir_path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FhirPackageValidityPeriod that = (FhirPackageValidityPeriod) o;
        return Objects.equals(valid_from, that.valid_from) && Objects.equals(valid_to, that.valid_to) && Objects.equals(fhir_path, that.fhir_path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valid_from, valid_to, fhir_path);
    }
}
