package de.abda.fhir.validator.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.abda.fhir.validator.core.configuration.FhirPackageProperties;
import de.abda.fhir.validator.core.exception.ValidatorInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class FhirPackagePropertiesHelper {

    static Logger logger = LoggerFactory.getLogger(FhirPackagePropertiesHelper.class);

    public static FhirPackageProperties loadFhirPackageProperties() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        FhirPackageProperties fhirPackageProperties;
        try {
            InputStream packagesConfigFile = FhirPackagePropertiesHelper.class.getClassLoader().getResourceAsStream("packages.yaml");
            fhirPackageProperties = mapper.readValue(packagesConfigFile, FhirPackageProperties.class);
            return fhirPackageProperties;
        } catch (Exception e) {
            logger.error("Unable to load package properties.");
            throw new ValidatorInitializationException("Unable to load package properties. Root cause: " + e.getMessage());
        }
    }

}
