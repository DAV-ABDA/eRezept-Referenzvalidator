package de.abda.fhir.validator.core.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.IValidationSupport;
import de.abda.fhir.validator.core.support.IgnoreMissingValueSetValidationSupport;
import de.abda.fhir.validator.core.support.VersionIgnoringSnapshotGeneratingValidationSupport;
import de.abda.fhir.validator.core.support.VersionPipeAwareSupportChain;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;

public class ValidationSupportChainHelper {

    // This is basically the validator configuration!
    public static IValidationSupport createValidationSupportChain(PrePopulatedValidationSupport npmPackageSupport, FhirContext ctx) {
    	/*
    	 * Reuse already prepared supports from context
    	 * save memory from the hogs
    	 */
    	IValidationSupport validationSupport = ctx.getValidationSupport();

    	// Create a support chain including the NPM Package Support
        return
	        		new VersionPipeAwareSupportChain(
	                npmPackageSupport,
	                validationSupport,
	                new VersionIgnoringSnapshotGeneratingValidationSupport(ctx),
	                new IgnoreMissingValueSetValidationSupport(ctx)

        );
    }
}
