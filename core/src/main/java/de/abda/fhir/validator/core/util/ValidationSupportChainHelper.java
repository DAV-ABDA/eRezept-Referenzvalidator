package de.abda.fhir.validator.core.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import de.abda.fhir.validator.core.support.IgnoreMissingValueSetValidationSupport;
import de.abda.fhir.validator.core.support.VersionIgnoringSnapshotGeneratingValidationSupport;
import de.abda.fhir.validator.core.support.VersionRemovingNpmPackageValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;

public class ValidationSupportChainHelper {

    // This is basically the validator configuration!
    public static ValidationSupportChain createValidationSupportChain(VersionRemovingNpmPackageValidationSupport npmPackageSupport, FhirContext ctx) {
        // Create a support chain including the NPM Package Support
        return new ValidationSupportChain(
                npmPackageSupport,
                new DefaultProfileValidationSupport(ctx),
                new VersionIgnoringSnapshotGeneratingValidationSupport(ctx),
                new CommonCodeSystemsTerminologyService(ctx),
                new InMemoryTerminologyServerValidationSupport(ctx),
                new IgnoreMissingValueSetValidationSupport(ctx)
        );
    }

}
