package de.abda.fhir.validator.core.support;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.ConceptValidationOptions;
import ca.uhn.fhir.context.support.ValidationSupportContext;
import org.hl7.fhir.common.hapi.validation.support.BaseValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;

import javax.annotation.Nullable;

/** TODO
 * This validation support module must be placed at the end of a {@link ValidationSupportChain}
 * in order to configure the validator to generate a warning if a resource being validated
 * contains an unknown code system.
 */
public class IgnoreMissingPznValueSetValidationSupport extends BaseValidationSupport {

    /**
     * Constructor
     */
    public IgnoreMissingPznValueSetValidationSupport(FhirContext theFhirContext) {
        super(theFhirContext);
    }

    @Override
    public boolean isCodeSystemSupported(ValidationSupportContext theValidationSupportContext, String theCodeSystem) {
        if (theCodeSystem != null && theCodeSystem.equals("http://fhir.de/CodeSystem/ifa/pzn")){
            return true;
        } else return false;
    }

    @Nullable
    @Override
    public CodeValidationResult validateCode(ValidationSupportContext theValidationSupportContext, ConceptValidationOptions theOptions, String theCodeSystem, String theCode, String theDisplay, String theValueSetUrl) {
        if (theCodeSystem != null && theCodeSystem.equals("http://fhir.de/CodeSystem/ifa/pzn")) {
            CodeValidationResult result = new CodeValidationResult();
            result.setSeverity(IssueSeverity.INFORMATION);
            result.setCodeSystemName(theCodeSystem);
            result.setCode(theCode);
            result.setMessage("PZN found. PZN validation is not supported.");
            return result;
        } else return null;
    }

}
