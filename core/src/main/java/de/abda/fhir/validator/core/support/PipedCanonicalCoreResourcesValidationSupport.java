package de.abda.fhir.validator.core.support;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.IValidationSupport;
import de.abda.fhir.validator.core.util.Profile;
import de.abda.fhir.validator.core.util.ProfileHelper;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.StructureDefinition;

/**
 * Modified from <a href="https://github.com/gematik/app-referencevalidator/blob/e93ff3a6d4da77c868174e26a3598c4891e59837/commons/src/main/java/de/gematik/refv/commons/validation/support/PipedCanonicalCoreResourcesValidationSupport.java"</a>
 * Copyright (c) 2023 gematik GmbH, Apache License, Version 2.0
 */

/**
 * This validation support module must be placed at the end of a {@link ValidationSupportChain}
 * to patch the validator to generate no warnings and errors in resolving piped canonicals for core resources.
 */
public class PipedCanonicalCoreResourcesValidationSupport implements IValidationSupport {
    private final FhirContext ctx;
    public PipedCanonicalCoreResourcesValidationSupport(FhirContext ctx) {
        this.ctx = ctx;
    }
    @Override
    public IBaseResource fetchStructureDefinition(String theUrl) {
        Profile p = ProfileHelper.getProfileFromCanonical(theUrl);
        if(p.getVersion() == null)
            return null;

        IValidationSupport support = ctx.getValidationSupport();
        IBaseResource resource = support.fetchStructureDefinition(p.getBaseCanonical());
        if(resource == null)
            return null;

        if(StringUtils.equals(((StructureDefinition) resource).getVersion(), p.getVersion()))
            return resource;

        return null;
    }

    @Override
    public FhirContext getFhirContext() {
        return ctx;
    }
}
