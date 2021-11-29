package de.abda.fhir.validator.core.support;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.ConceptValidationOptions;
import ca.uhn.fhir.context.support.ValidationSupportContext;
import java.util.HashMap;
import org.hl7.fhir.common.hapi.validation.support.BaseValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;

import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.Map;

/** TODO
 * This validation support module must be placed at the end of a {@link ValidationSupportChain}
 * in order to configure the validator to generate a warning if a resource being validated
 * contains an unknown code system.
 */
public class IgnoreMissingValueSetValidationSupport extends BaseValidationSupport {

    private final Map<String, String> supportedCodeSystemMap = new HashMap<>();

    /**
     * Constructor
     * @param theFhirContext {@link FhirContext}
     */
    public IgnoreMissingValueSetValidationSupport(FhirContext theFhirContext) {
        super(theFhirContext);
        supportedCodeSystemMap.put("http://fhir.de/CodeSystem/ifa/pzn", "PZN found. This validator has no PZN database and will not check if the provided PZN is valid.");
        supportedCodeSystemMap.put("http://fhir.de/CodeSystem/ask", "ASK entry found. This validator has no ASK database and will not check if the provided entry is valid");
    }

    @Override
    public boolean isCodeSystemSupported(ValidationSupportContext theValidationSupportContext, String theCodeSystem) {
        if (theCodeSystem != null && supportedCodeSystemMap.containsKey(theCodeSystem)){
            return true;
        } else return false;
    }

    @Nullable
    @Override
    public CodeValidationResult validateCode(ValidationSupportContext theValidationSupportContext, ConceptValidationOptions theOptions, String theCodeSystem, String theCode, String theDisplay, String theValueSetUrl) {
        if (theCodeSystem != null && supportedCodeSystemMap.containsKey(theCodeSystem)) {
            CodeValidationResult result = new CodeValidationResult();
            result.setSeverity(IssueSeverity.INFORMATION);
            result.setCodeSystemName(theCodeSystem);
            result.setCode(theCode);
            result.setMessage(supportedCodeSystemMap.get(theCodeSystem));
            return result;
        } else return null;
    }

}
