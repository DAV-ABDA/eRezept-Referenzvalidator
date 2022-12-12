package de.abda.fhir.validator.core;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import de.abda.fhir.validator.core.util.FileHelper;
import de.abda.fhir.validator.core.util.Profile;
import de.abda.fhir.validator.core.util.ProfileHelper;
import de.abda.fhir.validator.core.util.ProfileValidityDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

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
    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validateFile(String inputFile, boolean noInstanceValidityCheck) {
        logger.debug("Start validating File {}", inputFile);
        String validatorInputAsString = FileHelper.loadValidatorInputAsString(inputFile);
        return this.validateImpl(validatorInputAsString, noInstanceValidityCheck);
    }

    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validateFile(String inputFile) {
        return this.validateFile(inputFile, true);
    }
    /**
     * Validates the given File
     * @param inputFile String path, not null or empty
     * @return Map of {@link ResultSeverityEnum} as key and a List of {@link SingleValidationMessage} as key
     */
    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validateFile(Path inputFile, boolean noInstanceValidityCheck) {
        return validateFile(inputFile.toString(), noInstanceValidityCheck);
    }

    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validateFile(Path inputFile) {
        return validateFile(inputFile.toString(), true);
    }
    /**
     * Validates the given String containing a FHIR resource
     * @param validatorInputAsString String, not null or empty
     * @return Map of {@link ResultSeverityEnum} as key and a List of {@link SingleValidationMessage} as key
     */
    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validateString(String validatorInputAsString, boolean noInstanceValidityCheck) {
        logger.debug("Start validating String input");
        return validateImpl(validatorInputAsString, noInstanceValidityCheck);
    }

    /**
     * The first validation in a new validator is very slow. So this method creates validators
     * for all supported profiles and loads all necessary data, so the calls to the validator
     * will be fast afterwards.
     * @param profileToPreload a var ags array of profiles, that will be preloaded. If this is null or
     *                         empty, then all profiles will be preloaded
     */
    public void preloadAllSupportedValidators(ProfileForPreloading... profileToPreload){
        validatorHolder.preloadAllSupportedValidators(profileToPreload);
    }

    private Map<ResultSeverityEnum, List<SingleValidationMessage>> validateImpl(String validatorInputAsString) {
        return validateImpl(validatorInputAsString, false);
    }

    private Map<ResultSeverityEnum, List<SingleValidationMessage>> validateImpl(String validatorInputAsString, boolean noInstanceValidityCheck) {
        Profile instanceProfile = null;
        ProfileValidityDate instanceProfileValidityDate = null;
        Map<ResultSeverityEnum, List<SingleValidationMessage>> instanceValidityCheckResults = new HashMap<>();
        // TODO: ReleaseVersionsausgabe !?! oder Option Auswertung Instanz?
        //ValidationMessageAdd(instanceValidityCheckResults, ResultSeverityEnum.INFORMATION, "Validator Version 1.0.0");
        //ValidationMessageAdd(instanceValidityCheckResults, ResultSeverityEnum.INFORMATION, "noInstanceValidityCheck: " + noInstanceValidityCheck);
        InputStream validatorInputStream = new ByteArrayInputStream(validatorInputAsString.getBytes(StandardCharsets.UTF_8));
        if (noInstanceValidityCheck) {
            instanceProfile = ProfileHelper.getProfileFromXmlStream(validatorInputStream);
        } else { // if (!noInstanceValidityCheck) {
            instanceProfileValidityDate = ProfileHelper.getProfileValidityDateFromXmlStream(validatorInputStream, validatorHolder);
            if (instanceProfileValidityDate == null || instanceProfileValidityDate.getValidityPeriod() == null) {
                ValidationMessageAdd(instanceValidityCheckResults, ResultSeverityEnum.FATAL, "validityPeriod for profile not found");
            } else {
                if (instanceProfileValidityDate.getInstanceDate() == null) {
                    logger.debug("instanceDate null");
                    ValidationMessageAdd(instanceValidityCheckResults, ResultSeverityEnum.FATAL, "no Instance date");
                } else {
                    logger.debug(instanceProfileValidityDate.getInstanceDate().toString());
                    instanceProfile = instanceProfileValidityDate.getProfile();
                    if ((instanceProfileValidityDate.getInstanceDate().isAfter(instanceProfileValidityDate.getValidityPeriod().getValid_from()) && instanceProfileValidityDate.getInstanceDate().isBefore(instanceProfileValidityDate.getValidityPeriod().getValid_to())) || instanceProfileValidityDate.getInstanceDate().isEqual(instanceProfileValidityDate.getValidityPeriod().getValid_from()) || instanceProfileValidityDate.getInstanceDate().isEqual(instanceProfileValidityDate.getValidityPeriod().getValid_to())) {
                        logger.info("Instance valid");
                        ValidationMessageAdd(instanceValidityCheckResults, ResultSeverityEnum.INFORMATION, "Instance valid");
                    } else {
                        logger.error("Instance invalid");
                        ValidationMessageAdd(instanceValidityCheckResults, ResultSeverityEnum.FATAL, "Instance invalid");
                    }
                }
            }
        }
        // TODO: Stream schließen... ?!?
        //validatorInputStream.close();
        //https://stackoverflow.com/questions/309424/how-do-i-read-convert-an-inputstream-into-a-string-in-java

        if (instanceProfile != null) {
            Validator validator = validatorHolder.getValidatorForProfile(instanceProfile);
            if (validator != null) {
                Map<ResultSeverityEnum, List<SingleValidationMessage>> output = validator.validate(validatorInputAsString);
                output.putAll(instanceValidityCheckResults);
                return output;
            } else {
                ValidationMessageAdd(instanceValidityCheckResults, ResultSeverityEnum.FATAL, "Profile unbekannt!");
                return instanceValidityCheckResults;
            }
        } else {
            ValidationMessageAdd(instanceValidityCheckResults, ResultSeverityEnum.FATAL, "Profile nicht erkannt!");
            return instanceValidityCheckResults;
        }
    }

    private void ValidationMessageAdd(Map<ResultSeverityEnum, List<SingleValidationMessage>> instanceValidityCheckResults, ResultSeverityEnum inResultSeverityEnum, String inMessage) {
        // create new Message
        SingleValidationMessage mySingleValidationMessage = new SingleValidationMessage();
        mySingleValidationMessage.setSeverity(inResultSeverityEnum);
        mySingleValidationMessage.setMessage(inMessage);

        if(instanceValidityCheckResults.containsKey(inResultSeverityEnum)){
            instanceValidityCheckResults.get(inResultSeverityEnum).add(mySingleValidationMessage);
        } else {
            ArrayList<SingleValidationMessage> mySingleValidationMessageList  = new ArrayList<>();
            mySingleValidationMessageList.add(mySingleValidationMessage);
            instanceValidityCheckResults.put(inResultSeverityEnum, mySingleValidationMessageList);
        }
    }
}
