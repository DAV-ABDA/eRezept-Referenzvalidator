package de.abda.fhir.validator.core;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import de.abda.fhir.validator.core.exception.ValidatorInitializationException;
import java.io.InputStream;
import java.util.Properties;
import org.hl7.fhir.common.hapi.validation.support.*;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Validator {

    static final String PACKAGES_PROPERTIES = "/package/packages.properties";
    static Logger logger = LoggerFactory.getLogger(Validator.class);
    private final FhirValidator validator;

    public Validator() {
        this(FhirContext.forR4());
    }

    public Validator(FhirContext ctx) {
        this.validator = getValidatorInstance(ctx);
    }

//    private void init(ctx);

    private static FhirValidator getValidatorInstance(FhirContext ctx)  {
        NpmPackageValidationSupport npmPackageSupport = new NpmPackageValidationSupport(ctx);
        try {
            //loading packages
            Properties packagesProps = new Properties();
            InputStream stream = Validator.class.getResourceAsStream(PACKAGES_PROPERTIES);
            if (stream == null) {
                throw new ValidatorInitializationException("No file packages.properties found. "
                    + "Please verify, that you have a packages version in the classpath");
            }
            packagesProps.load(stream);
            String packages = (String) packagesProps.get("packages");
            String[] packageFiles = packages.split(",");
            for (String packageFile : packageFiles) {
                npmPackageSupport.loadPackageFromClasspath("classpath:" + packageFile);
            }

            // Create a support chain including the NPM Package Support
            UnknownCodeSystemWarningValidationSupport unknownCodeSystemWarningValidationSupport = new UnknownCodeSystemWarningValidationSupport(
                ctx);
            unknownCodeSystemWarningValidationSupport.setAllowNonExistentCodeSystem(true);
            ValidationSupportChain validationSupportChain = new ValidationSupportChain(
                npmPackageSupport,
                new DefaultProfileValidationSupport(ctx),
                new CommonCodeSystemsTerminologyService(ctx),
                new InMemoryTerminologyServerValidationSupport(ctx),
                new SnapshotGeneratingValidationSupport(ctx),
                unknownCodeSystemWarningValidationSupport
            );
            //        CachingValidationSupport validationSupport = new CachingValidationSupport(validationSupportChain);

            // Create a validator. Note that for good performance you can create as many validator objects
            // as you like, but you should reuse the same validation support object in all of the,.
            FhirValidator validator = ctx.newValidator();
            FhirInstanceValidator instanceValidator = new FhirInstanceValidator(
                validationSupportChain);
            // instanceValidator.setNoTerminologyChecks(true);
            validator.registerValidatorModule(instanceValidator);
            logger.info("Validator initialization succeeded");
            return validator;
        } catch (ValidatorInitializationException e){
            logger.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw new ValidatorInitializationException("Validator could not be initialized correctly", e);
        }
    }

    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validate(String input) {
        ValidationResult result = validator.validateWithResult(input);
        return handleValidationResults(result);
    }

    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validate(IBaseResource input) {
        ValidationResult result = validator.validateWithResult(input);
        return handleValidationResults(result);
    }

    private Map<ResultSeverityEnum, List<SingleValidationMessage>> handleValidationResults(
        ValidationResult result) {

        // The result object now contains the validation results
        for (SingleValidationMessage next : result.getMessages()) {
            logger.info(
                "Validator message: " + next.getSeverity() + " " + next.getLocationString() + " "
                    + next.getMessage());
        }
        return result.getMessages().stream().collect(
                Collectors.groupingBy(SingleValidationMessage::getSeverity, Collectors.toList()));
    }
}
