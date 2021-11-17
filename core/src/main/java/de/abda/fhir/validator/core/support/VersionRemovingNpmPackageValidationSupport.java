package de.abda.fhir.validator.core.support;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.util.ClasspathUtil;
import de.abda.fhir.validator.core.util.InputHelper;
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.utilities.npm.NpmPackage;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class VersionRemovingNpmPackageValidationSupport extends NpmPackageValidationSupport {
    /**
     * Constructor
     *
     * @param theFhirContext {@link FhirContext}, must not be null
     */
    public VersionRemovingNpmPackageValidationSupport(@Nonnull FhirContext theFhirContext) {
        super(theFhirContext);
    }

    @Override
    public void loadPackageFromClasspath(String theClasspath) throws IOException {
        try (InputStream is = ClasspathUtil.loadResourceAsStream(theClasspath)) {
            NpmPackage pkg = NpmPackage.fromPackage(is);
            if (pkg.getFolders().containsKey("package")) {
                NpmPackage.NpmPackageFolder packageFolder = pkg.getFolders().get("package");

                for (String nextFile : packageFolder.listFiles()) {
                    if (nextFile.toLowerCase(Locale.US).endsWith(".json")) {
                        String input = new String(packageFolder.getContent().get(nextFile), StandardCharsets.UTF_8);
                        input = InputHelper.removeVersionInCanonicals(input);
                        IBaseResource resource = getFhirContext().newJsonParser().parseResource(input);
                        super.addResource(resource);
                    }
                }

            }
        }
    }
}
