package de.abda.fhir.validator.core;

import ca.uhn.fhir.context.FhirContext;
import de.abda.fhir.validator.core.configuration.FhirProfileVersion;
import de.abda.fhir.validator.core.util.Profile;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ValidatorHolder {

    static final Logger logger = LoggerFactory.getLogger(ValidatorHolder.class);

    private Map<Profile, Validator> validatorMap;
    private ValidatorFactory validatorFactory;

    public ValidatorHolder() {
        new ValidatorHolder(FhirContext.forR4Cached());
    }

    public ValidatorHolder(FhirContext ctx) {
        this.validatorMap = new HashMap<>();
        this.validatorFactory = new ValidatorFactory(ctx);
    }

    public Validator getValidatorForProfile(Profile profile) {
        if (!validatorMap.containsKey(profile)) {
            validatorMap.put(profile, validatorFactory.createValidatorForProfile(profile));
        }
        return validatorMap.get(profile);
    }

    /**
     * Takes all supported profiles and their versions, creates a Validator instance and validates
     * an empty bundle to load the snapshots.
     * @param profileToPreload a varags array of profiles, that will be preloaded. If this is null or
     *                         empty, then all profiles will be preloaded
     */
    public void preloadAllSupportedValidators(ProfileForPreloading... profileToPreload){
        if (profileToPreload == null || profileToPreload.length ==0){
            profileToPreload = ProfileForPreloading.values();
        }
        ProfileForPreloading[] finalProfileToPreload = profileToPreload;
        List<Pair<String,FhirProfileVersion>> profiles = validatorFactory.getAllSupportedProfiles()
            .stream().filter(pair-> Stream.of(finalProfileToPreload).anyMatch(profileForPreloading -> profileForPreloading.getCanonical().equals(pair.getKey())))
            .collect(Collectors.toList());

        logger.info("Preloading these profiles: {}", "\n" + profiles.stream()
            .map(pair-> pair.getKey() + " , " + pair.getValue().getRequiredPackage().getPackageName() + ": " + pair.getValue().getVersion())
            .collect(Collectors.joining("\n")));

        for (Pair<String,FhirProfileVersion> profileData : profiles) {
            Profile profile = new Profile(profileData.getKey()
                + "|" + profileData.getValue().getRequiredPackage().getPackageVersion(),
                profileData.getKey(),
                profileData.getValue().getRequiredPackage().getPackageVersion());
            if (validatorMap.containsKey(profile)){
                logger.debug("Validator for profile " + profile + " is already loaded");
            }else {
                Validator validator = validatorFactory
                    .loadValidator(profileData.getKey(), profileData.getValue());
                validatorMap.put(profile, validator);

                //We validate an empty bundle, so that the validator generates the snapshots and loads all internal data
                validator.validateWithResult("<Bundle xmlns=\"http://hl7.org/fhir\">\n"
                    + "    <id value=\"fb16b9fb-eca9-4a64-b257-083ac87c9c9c\"/>\n"
                    + "    <meta>\n"
                    + "        <profile value=\"" + profile.getCanonical() + "\"/>\n"
                    + "        \n"
                    + "    </meta>\n"
                    + "</Bundle>");
            }
        }
    }

}
