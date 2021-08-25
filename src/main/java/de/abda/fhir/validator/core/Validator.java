package de.abda.fhir.validator.core;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import de.abda.fhir.validator.cli.ValidatorCLI;
import org.hl7.fhir.common.hapi.validation.support.*;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Validator {

    static final String DE_BASE_PACKAGE = "classpath:package/de.basisprofil.r4-0.9.13.tgz";
    static final String DAV_ERP_PR_ABDABEDATENBUNDLE = "classpath:package/de.abda.erezeptabgabedaten-1.0.3.tgz";

    static Logger logger = LoggerFactory.getLogger(ValidatorCLI.class);
    private FhirValidator validator;

    public Validator() {
        this(FhirContext.forR4());
    }

    public Validator(FhirContext ctx) {
        try {
            this.validator = getValidatorInstance(ctx);
        } catch (Exception e) {
            logger.error("Could not instantiate validator!");
        }
    }

//    private void init(ctx);

    private static FhirValidator getValidatorInstance(FhirContext ctx) throws Exception {
        NpmPackageValidationSupport npmPackageSupport = new NpmPackageValidationSupport(ctx);
        try {
            npmPackageSupport.loadPackageFromClasspath(DE_BASE_PACKAGE);
            npmPackageSupport.loadPackageFromClasspath(DAV_ERP_PR_ABDABEDATENBUNDLE);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new Exception();
        }

        // Create a support chain including the NPM Package Support
        UnknownCodeSystemWarningValidationSupport unknownCodeSystemWarningValidationSupport = new UnknownCodeSystemWarningValidationSupport(ctx);
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
        FhirInstanceValidator instanceValidator = new FhirInstanceValidator(validationSupportChain);
        // instanceValidator.setNoTerminologyChecks(true);
        validator.registerValidatorModule(instanceValidator);
        return validator;
    }

    public Map<ResultSeverityEnum, List<SingleValidationMessage>> validate(String input) {
        ValidationResult result = validator.validateWithResult(input);

        // The result object now contains the validation results
        for (SingleValidationMessage next : result.getMessages()) {
            logger.info("Validator message: " + next.getSeverity() + " " + next.getLocationString() + " " + next.getMessage());
        }
        Map<ResultSeverityEnum, List<SingleValidationMessage>> errors = result.getMessages().stream().collect(Collectors.groupingBy(SingleValidationMessage::getSeverity, Collectors.toList()));
        return errors;
    }

}
