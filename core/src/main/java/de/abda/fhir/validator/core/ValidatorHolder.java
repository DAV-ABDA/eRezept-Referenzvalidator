package de.abda.fhir.validator.core;

import ca.uhn.fhir.context.FhirContext;
import de.abda.fhir.validator.core.util.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ValidatorHolder {

    static Logger logger = LoggerFactory.getLogger(ValidatorHolder.class);

    private Map<Profile, Validator> validatorMap;
    private ValidatorFactory validatorFactory;

    public ValidatorHolder() {
        new ValidatorHolder(FhirContext.forR4Cached());
    }

    public ValidatorHolder(FhirContext ctx) {
        this.validatorMap = new HashMap<Profile, Validator>();
        this.validatorFactory = new ValidatorFactory(ctx);
    }

    public Validator getValidatorForProfile(Profile profile) {
        if (!validatorMap.containsKey(profile)) {
            validatorMap.put(profile, validatorFactory.createValidatorForProfile(profile));
        }
        return validatorMap.get(profile);
    }

}
