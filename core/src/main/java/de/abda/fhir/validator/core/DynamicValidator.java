package de.abda.fhir.validator.core;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import de.abda.fhir.validator.core.util.FileHelper;
import de.abda.fhir.validator.core.util.InputHelper;
import de.abda.fhir.validator.core.util.ParserHelper;
import de.abda.fhir.validator.core.util.Profile;
import de.abda.fhir.validator.core.util.ProfileHelper;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the default class to use the ABDA FHIR validator in your Java Application.
 * To validate a File, you can use the {@link #validate(Path)}, if the file content is
 * already read as String, you can use {@link #validate(String)}. The profile data is loaded
 * on demand the first time a profile version is used. Further invocations use the already
 * loaded data.
 *
 * <p>Dynamic Validator is currently NOT threadsafe, but it can be reused for validating
 * further FHIR resources in the same or another thread.</p>
 */
public class DynamicValidator {
    static Logger logger = LoggerFactory.getLogger(Validator.class);
    private FhirContext ctx ;
    private ValidatorHolder validatorHolder;

    /**
     * Creates a new instance without parameters.
     */
    public DynamicValidator() {
        this(FhirContext.forR4());
    }

    /**
     * Creates a new instance using an existing FhirContext.
     * @param ctx {@link FhirContext}, not null
     */
    public DynamicValidator(FhirContext ctx) {
        this.ctx = ctx;
        validatorHolder = new ValidatorHolder(ctx);
    }

    /**
     * Validates the given File
     * @param inputFile Path, not null
     * @return Map of {@link ResultSeverityEnum} as key and a List of {@link SingleValidationMessage} as key
     */
    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validate(Path inputFile) {
        logger.debug("Start validating File {}", inputFile.toString());
        String validatorInputAsString = FileHelper
            .loadValidatorInputAsString(inputFile.toString(), false);
        return this.validateImpl(validatorInputAsString);
    }
    /**
     * Validates the given String containing a FHIR resouce
     * @param validatorInputAsString String, not null or empty
     * @return Map of {@link ResultSeverityEnum} as key and a List of {@link SingleValidationMessage} as key
     */
    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validate(String validatorInputAsString) {
        logger.debug("Start validating String input");
        return validateImpl(validatorInputAsString);
    }

    private Map<ResultSeverityEnum, List<SingleValidationMessage>> validateImpl(
        String validatorInputAsString) {
        IBaseResource resource = ParserHelper.parseString(validatorInputAsString, ctx);
        Profile profile = ProfileHelper.getProfile(resource);
        Validator validator = validatorHolder.getValidatorForProfile(profile);
        String validatorInputWithoutVersion = InputHelper.removeVersionInCanonicals(
            validatorInputAsString);
        return validator.validate(validatorInputWithoutVersion);
    }
}
