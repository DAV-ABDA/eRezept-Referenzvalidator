package de.abda.fhir.validator.core;

import ca.uhn.fhir.context.FhirContext;
import de.abda.fhir.validator.core.configuration.FhirProfileVersion;
import de.abda.fhir.validator.core.util.Profile;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
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

    public void preloadAllSupportedValidators(){
        List<Pair<String,FhirProfileVersion>> profiles = validatorFactory.getAllSupportedProfiles();
        logger.info("Preloading all supported profiles: {}", profiles);
        for (Pair<String,FhirProfileVersion> profileData : profiles) {
            Validator validator = validatorFactory.loadValidator(profileData.getKey(), profileData.getValue());
            Profile profile = new Profile(profileData.getKey()
                + "|" + profileData.getValue().getRequiredPackage().getPackageVersion(),
                profileData.getKey(),
                profileData.getValue().getRequiredPackage().getPackageVersion());
            validatorMap.put(profile, validator);

            //We validate an empty bundle, so that the validator generates the snapshots and loads all internal data
            validator.validate("<Bundle xmlns=\"http://hl7.org/fhir\">\n"
                + "    <id value=\"fb16b9fb-eca9-4a64-b257-083ac87c9c9c\"/>\n"
                + "    <meta>\n"
                + "        <profile value=\"" + profile.getCanonical() +"\"/>\n"
                + "        \n"
                + "    </meta>\n"
                + "</Bundle>");
        }
        
    }

}
