package de.abda.fhir.validator.core;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import de.abda.fhir.validator.core.configuration.*;
import de.abda.fhir.validator.core.exception.ValidatorInitializationException;
import de.abda.fhir.validator.core.support.VersionRemovingNpmPackageValidationSupport;
import de.abda.fhir.validator.core.util.FhirPackagePropertiesHelper;
import de.abda.fhir.validator.core.util.Profile;
import de.abda.fhir.validator.core.util.ValidationSupportChainHelper;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValidatorFactory {

    static Logger logger = LoggerFactory.getLogger(ValidatorFactory.class);

    private FhirPackageProperties fhirPackageProperties;
    private FhirContext ctx;

    public ValidatorFactory() {
        new ValidatorFactory(FhirContext.forR4());
    }

    public ValidatorFactory(FhirContext ctx) {
        this.fhirPackageProperties = FhirPackagePropertiesHelper.loadFhirPackageProperties();
        this.ctx = ctx;
    }

    public Validator createValidatorForProfile(Profile profile) {
        VersionRemovingNpmPackageValidationSupport npmPackageSupport = new VersionRemovingNpmPackageValidationSupport(ctx);
        try {

            FhirProfile supportedProfile = fhirPackageProperties.getSupportedProfiles()
                .get(profile.getBaseCanonical());
            if (supportedProfile == null){
                String msg = "Profile \"" + profile.getBaseCanonical() +"\" not supported";
                logger.error(msg);
                throw new ValidatorInitializationException(msg);
            }
            Map<String, FhirProfileVersion> supportedProfileVersions = supportedProfile.getVersions();
            if (supportedProfileVersions.isEmpty()) {
                String msg = "Profile version \"" + profile.getBaseCanonical() +"\" not supported";
                logger.error(msg);
                throw new ValidatorInitializationException(msg);
            }

            FhirProfileVersion fhirProfileVersion = supportedProfileVersions.get(profile.getVersion());

            if (fhirProfileVersion == null) {
                String msg = "Version \"" + profile.getVersion() + "\" for profile \"" + profile.getBaseCanonical() + "\" is not supported";
                logger.error(msg);
                throw new ValidatorInitializationException(msg);
            }

            List<String> packageFilesToLoad = getPackageFilenameListToLoadFor(fhirProfileVersion);
            for(String packageFilename : packageFilesToLoad) {
                npmPackageSupport.loadPackageFromClasspath("classpath:package/" + packageFilename);
            }

            ValidationSupportChain validationSupportChain = ValidationSupportChainHelper.createValidationSupportChain(npmPackageSupport, ctx);

            FhirValidator fhirValidator = ctx.newValidator();
            FhirInstanceValidator instanceValidator = new FhirInstanceValidator(
                    validationSupportChain);
            // instanceValidator.setNoTerminologyChecks(true); //TODO check if this needs to be configured
            fhirValidator.registerValidatorModule(instanceValidator);
            logger.debug("Validator initialization succeeded for profile \"" + profile.getBaseCanonical() + "\" version \"" + profile.getVersion() + "\"");
            return new Validator(fhirValidator);
        } catch (ValidatorInitializationException e){
            logger.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw new ValidatorInitializationException("Validator could not be initialized correctly for profile \"" + profile.getBaseCanonical() + "\" version \"" + profile.getVersion() + "\"", e);
        }
    }


    public List<String> getPackageFilenameListToLoadFor(FhirProfileVersion fhirProfileVersion) throws Exception {
        ArrayList<String> packageFilenameList = new ArrayList<>();
        String requiredPackageName = fhirProfileVersion.getRequiredPackage().getPackageName();
        String requiredPackageVersion = fhirProfileVersion.getRequiredPackage().getPackageVersion();

        FhirPackageInfo requiredFhirPackageInfo = fhirPackageProperties.getFhirPackages().get(requiredPackageName);
        if (requiredFhirPackageInfo == null) {
            String msg = "Could not find required package \"" + requiredPackageName + "\"";
            logger.error(msg);
            throw new Exception(msg);
        }

        FhirPackageVersion requiredFhirPackageVersion = requiredFhirPackageInfo.getVersions().get(requiredPackageVersion);
        if (requiredFhirPackageVersion == null) {
            String msg = "Could not find required package version \"" + requiredPackageVersion + "\" for package \"" + requiredPackageName + "\"";
            logger.error(msg);
            throw new Exception(msg);
        }

        packageFilenameList.add(requiredFhirPackageVersion.getFilename());
        List<FhirPackage> dependencies = requiredFhirPackageVersion.getDependencies();
        if (dependencies != null && dependencies.isEmpty() == false) {
            packageFilenameList.addAll(getFilenamesFromPackageDependencies(dependencies));
        }

        return packageFilenameList;
    }

    public List<String> getFilenamesFromPackageDependencies(List<FhirPackage> dependencies) throws Exception {
        //TODO redundancy check
        ArrayList<String> packageFilenameList = new ArrayList<>();

        if (dependencies == null || dependencies.isEmpty() == true) {
            return  packageFilenameList;
        }

        for(FhirPackage dependency : dependencies) {
            String requiredPackageName = dependency.getPackageName();
            String requiredPackageVersion = dependency.getPackageVersion();

            FhirPackageInfo requiredFhirPackageInfo = fhirPackageProperties.getFhirPackages().get(requiredPackageName);
            if (requiredFhirPackageInfo == null) {
                String msg = "Could not find required package \"" + requiredPackageName;
                logger.error(msg);
                throw new Exception(msg);
            }

            FhirPackageVersion requiredFhirPackageVersion = requiredFhirPackageInfo.getVersions().get(requiredPackageVersion);
            if (requiredFhirPackageVersion == null) {
                String msg = "Could not find required package version \"" + requiredPackageVersion + "\" for package \"" + requiredPackageName;
                logger.error(msg);
                throw new Exception(msg);
            }

            packageFilenameList.add(requiredFhirPackageVersion.getFilename());
            List<FhirPackage> furtherDependencies = fhirPackageProperties.getFhirPackages().get(dependency.getPackageName()).getVersions().get(dependency.getPackageVersion()).getDependencies();
            if (furtherDependencies != null && furtherDependencies.isEmpty() == false) {
                packageFilenameList.addAll(getFilenamesFromPackageDependencies(furtherDependencies));
            }
        }
        return packageFilenameList;
    }

}
