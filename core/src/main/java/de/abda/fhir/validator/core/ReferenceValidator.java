package de.abda.fhir.validator.core;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import de.abda.fhir.validator.core.configuration.FhirPackageValidityPeriod;
import de.abda.fhir.validator.core.util.FileHelper;
import de.abda.fhir.validator.core.util.Profile;
import de.abda.fhir.validator.core.util.ProfileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

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
    static Logger logger = LoggerFactory.getLogger(Validator.class);
    private FhirContext ctx ;
    private ValidatorHolder validatorHolder;

    /**
     * Creates a new instance without parameters.
     */
    public ReferenceValidator() {
        this(FhirContext.forR4());
    }

    /**
     * Creates a new instance using an existing FhirContext.
     * @param ctx {@link FhirContext}, not null
     */
    public ReferenceValidator(FhirContext ctx) {
        this.ctx = ctx;
        validatorHolder = new ValidatorHolder(ctx);
    }
    /**
     * Validates the given File
     * @param inputFile Path, not null
     * @return Map of {@link ResultSeverityEnum} as key and a List of {@link SingleValidationMessage} as key
     */
    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validateFile(String inputFile) {
        logger.debug("Start validating File {}", inputFile);
        // TODO: NICO Umbau File basierter Stream
        String validatorInputAsString = FileHelper.loadValidatorInputAsString(inputFile);
        return this.validateImpl(validatorInputAsString);
    }
    /**
     * Validates the given File
     * @param inputFile String path, not null or empty
     * @return Map of {@link ResultSeverityEnum} as key and a List of {@link SingleValidationMessage} as key
     */
    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validateFile(Path inputFile) {
        return validateFile(inputFile.toString());
    }
    /**
     * Validates the given String containing a FHIR resouce
     * @param validatorInputAsString String, not null or empty
     * @return Map of {@link ResultSeverityEnum} as key and a List of {@link SingleValidationMessage} as key
     */
    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validateString(String validatorInputAsString) {
        logger.debug("Start validating String input");
        return validateImpl(validatorInputAsString);
    }

    /**
     * The first validation in a new validator is very slow. So this method creates validators
     * for all supported profiles and loads all necessary data, so the calls to the validator
     * will be fast afterwards.
     * @param profileToPreload a varags array of profiles, that will be preloaded. If this is null or
     *                         empty, then all profiles will be preloaded
     */
    public void preloadAllSupportedValidators(ProfileForPreloading... profileToPreload){
        validatorHolder.preloadAllSupportedValidators(profileToPreload);
    }

    private Map<ResultSeverityEnum, List<SingleValidationMessage>> validateImpl(String validatorInputAsString) {
        // fileinputstream
        InputStream validatorInputStream = new ByteArrayInputStream(validatorInputAsString.getBytes(StandardCharsets.UTF_8));
        // TODO: file management -> Fehler abfangen und melden -> output
        // TODO: ReleaseVersionsausgabe !?!
        Profile instanceProfile = ProfileHelper.getProfileFromXmlStream(validatorInputStream);
        //validatorInputStream.close();

        FhirPackageValidityPeriod validityPeriod = validatorHolder.getProfileValidityPeriod(instanceProfile);
        if (validityPeriod == null) {
            logger.error("validityPeriod for profile not found");
            // TODO: Fehlermanagment -> output
        } else {
            logger.info("validityPeriod for profile: " + validityPeriod.getValid_from() + " - " + validityPeriod.getValid_to());

            // TODO: ein zweites mal notwendig? Position zurücksetzen!!!
            validatorInputStream = new ByteArrayInputStream(validatorInputAsString.getBytes(StandardCharsets.UTF_8));
            LocalDate instanceDate = ProfileHelper.getInstanceDateFromXmlStream(validatorInputStream, validityPeriod.getFhir_path());
            //validatorInputStream.close();
            if (instanceDate == null) {
                logger.debug("instanceDate null");
            } else {
                logger.debug(instanceDate.toString());
                if ((instanceDate.isAfter(validityPeriod.getValid_from()) && instanceDate.isBefore(validityPeriod.getValid_to())) || instanceDate.isEqual(validityPeriod.getValid_from()) || instanceDate.isEqual(validityPeriod.getValid_to())) {
                    logger.info("Instance valid");
                } else {
                    logger.error("Instance invalid");
                }
                // TODO: Fehlermanagment -> output
            }
        }
        Validator validator = validatorHolder.getValidatorForProfile(instanceProfile);

        Map<ResultSeverityEnum, List<SingleValidationMessage>> output = validator.validate(validatorInputAsString);
        //output.entrySet(ResultSeverityEnum.ERROR);
        return output; // TODO: Fehlermanagment -> auch Meldungen des Referenzvalidators zurückgeben!
        //return validator.validate(validatorInputAsString);
    }

}
