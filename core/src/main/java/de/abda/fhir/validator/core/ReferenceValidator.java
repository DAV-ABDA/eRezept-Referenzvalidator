package de.abda.fhir.validator.core;

import ca.uhn.fhir.context.FhirContext;
import de.abda.fhir.validator.core.filter.FilterEngine;
import de.abda.fhir.validator.core.util.FileHelper;
import de.abda.fhir.validator.core.util.Profile;
import de.abda.fhir.validator.core.util.ProfileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * This is the default class to use the ABDA FHIR validator in your Java Application.
 * To validate a File, you can use the {@link #validateFile(Path)}, if the file content is
 * already read as String, you can use {@link #validateString(String)}. The profile data is loaded
 * on demand the first time a profile version is used. Further invocations use the already
 * loaded data.
 *
 * <p>ReferenceValidator is currently NOT threadsafe, but it can be reused for validating
 * further FHIR resources in the same or another thread.</p>
 */
public class ReferenceValidator {
    static final Logger logger = LoggerFactory.getLogger(Validator.class);
    private final FhirContext ctx;
    private final ValidatorHolder validatorHolder;
    private final FilterEngine filterEngine;


    /**
     * Creates a new instance without parameters.
     */
    public ReferenceValidator() {
        this(FhirContext.forR4());
    }

    /**
     * Creates a new instance using an existing FhirContext.
     *
     * @param ctx {@link FhirContext}, not null
     */
    public ReferenceValidator(FhirContext ctx) {
        this.ctx = ctx;
        validatorHolder = new ValidatorHolder(this.ctx);
        filterEngine = new FilterEngine();
    }

    /**
     * Validates the passed in file and filters out encountered ValidationMessages according to the {@link FilterEngine} defined for this project
     * @param path file to be validated
     * @return the filtered result
     */
    public ValidationResult validateFile(Path path) {
        logger.debug("Start validating File {}", path);
        String validatorInputAsString = FileHelper.loadValidatorInputAsString(path.toString(), false);
        Profile profile = getProfile(validatorInputAsString);
        return validateString(validatorInputAsString, profile);
    }

    /**
     * Validates the passed in string and filters out encountered ValidationMessages according to the {@link FilterEngine} defined for this project
     * @param validatorInputAsString string to be validated
     * @return the filtered result
     */
    public ValidationResult validateString(String validatorInputAsString) {
        logger.debug("Start validating String input");
        Profile profile = getProfile(validatorInputAsString);
        return validateString(validatorInputAsString, profile);
    }

    /**
     * Validates the passed in string and filters out encountered ValidationMessages according to the {@link FilterEngine} defined for this project
     * @param validatorInputAsString string to be validated
     * @param profile Profile to be used for validation
     * @return the validation result
     */
    public ValidationResult validateString(String validatorInputAsString, Profile profile) {
        Validator validator = validatorHolder.getValidatorForProfile(profile);
        ca.uhn.fhir.validation.ValidationResult validationResult = validator.validateWithResult(validatorInputAsString);
        return filterEngine.filterMessages(profile, validationResult);
    }

    private Profile getProfile(String validatorInputAsString) {
        InputStream validatorInputStream = new ByteArrayInputStream(validatorInputAsString.getBytes(StandardCharsets.UTF_8));
        Profile profile = ProfileHelper.getProfileFromXmlStream(validatorInputStream);
        return profile;
    }

    /**
     * The first validation in a new validator is very slow. So this method creates validators
     * for all supported profiles and loads all necessary data, so the calls to the validator
     * will be fast afterwards.
     *
     * @param profileToPreload a varags array of profiles, that will be preloaded. If this is null or
     *                         empty, then all profiles will be preloaded
     */
    public void preloadAllSupportedValidators(ProfileForPreloading... profileToPreload) {
        validatorHolder.preloadAllSupportedValidators(profileToPreload);
    }
}
