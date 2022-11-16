package de.abda.fhir.validator.core.support;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.context.support.ValidationSupportContext;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.PreconditionFailedException;
import ca.uhn.fhir.validation.SingleValidationMessage;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.common.hapi.validation.support.SnapshotGeneratingValidationSupport;
import org.hl7.fhir.common.hapi.validation.validator.ProfileKnowledgeWorkerR5;
import org.hl7.fhir.common.hapi.validation.validator.VersionSpecificWorkerContextWrapper;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r5.conformance.ProfileUtilities;
import org.hl7.fhir.r5.context.IWorkerContext;
import org.hl7.fhir.utilities.validation.ValidationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class FixedSnapshotGeneratingValidationSupport extends SnapshotGeneratingValidationSupport {

    private static final Logger log = LoggerFactory.getLogger(FixedSnapshotGeneratingValidationSupport.class);
    private final FhirContext myCtx;

    /**
     * Constructor
     * @param theCtx {@link FhirContext}
     */
    public FixedSnapshotGeneratingValidationSupport(FhirContext theCtx) {
        super(theCtx);
        myCtx = theCtx;
    }

    @Nullable
    @Override
    public IBaseResource generateSnapshot(ValidationSupportContext theValidationSupportContext, IBaseResource theInput, String theUrl, String theWebUrl, String theProfileName) {
        try {
            FhirVersionEnum version = theInput.getStructureFhirVersionEnum();
            assert version == myCtx.getVersion().getVersion();

            VersionSpecificWorkerContextWrapper.IVersionTypeConverter converter = newVersionTypeConverter(version);
            Validate.notNull(converter, "Can not generate snapshot for version: %s", version);

            org.hl7.fhir.r5.model.StructureDefinition inputCanonical = (org.hl7.fhir.r5.model.StructureDefinition) converter.toCanonical(theInput);

            final String inputUrl = inputCanonical.getUrl();
            
            if (theValidationSupportContext.getCurrentlyGeneratingSnapshots().contains(inputUrl)) {
                log.warn("Detected circular dependency, already generating snapshot for: {}", inputUrl);
                return theInput;
            }
            
            theValidationSupportContext.getCurrentlyGeneratingSnapshots().add(inputUrl);
            
            try {
                String baseDefinition = inputCanonical.getBaseDefinition();
                //TODO check version handling
//              if (baseDefinition.contains("|")) {
//                  String[] splittedBaseDefinition = baseDefinition.split("\\|");
//                  log.info("Removed version " + splittedBaseDefinition[1] + " from canonical " + baseDefinition );
//                  baseDefinition = splittedBaseDefinition[0];
//              }
                if (isBlank(baseDefinition)) {
                    throw new PreconditionFailedException("StructureDefinition[id=" + inputCanonical.getIdElement().getId() + ", url=" + inputCanonical.getUrl() + "] has no base");
                }

                IBaseResource base = theValidationSupportContext.getRootValidationSupport().fetchStructureDefinition(baseDefinition);
                if (base == null) {
                    throw new PreconditionFailedException("Unknown base definition: " + baseDefinition);
                }

                org.hl7.fhir.r5.model.StructureDefinition baseCanonical = (org.hl7.fhir.r5.model.StructureDefinition) converter.toCanonical(base);

                if (baseCanonical.getSnapshot().getElement().isEmpty()) {
                    // If the base definition also doesn't have a snapshot, generate that first
                    theValidationSupportContext.getRootValidationSupport().generateSnapshot(theValidationSupportContext, base, null, null, null);
                    baseCanonical = (org.hl7.fhir.r5.model.StructureDefinition) converter.toCanonical(base);
                }

                ArrayList<ValidationMessage> messages = new ArrayList<>();
                org.hl7.fhir.r5.conformance.ProfileUtilities.ProfileKnowledgeProvider profileKnowledgeProvider = new ProfileKnowledgeWorkerR5(myCtx);
                IWorkerContext context = new VersionSpecificWorkerContextWrapper(theValidationSupportContext, converter);
                ProfileUtilities profileUtilities = new ProfileUtilities(context, messages, profileKnowledgeProvider);
                profileUtilities.generateSnapshot(baseCanonical, inputCanonical, theUrl, theWebUrl, theProfileName);

                //FIX Process snapshotGeneration messages (not in HAPI yet!!!) -> test with "DAV-PR-ERP-AbgabedatenBundle"
                messages.stream().forEach(e -> this.logValidationMessage(e));
                //List<ValidationMessage> errorsAndWarnings = messages.stream().filter(m -> m.getLevel().isError() || m.getLevel() == ValidationMessage.IssueSeverity.WARNING).collect(Collectors.toList());
                //if (errorsAndWarnings.size() > 0) {
                List<ValidationMessage> errors = messages.stream().filter(m -> m.getLevel().isError()).collect(Collectors.toList());
                if (errors.size() > 0) {
                    throw new PreconditionFailedException("Could not generate snapshot for " + inputUrl);
                }
                /**/
                switch (version) {
                    case DSTU3:
                        org.hl7.fhir.dstu3.model.StructureDefinition generatedDstu3 = (org.hl7.fhir.dstu3.model.StructureDefinition) converter.fromCanonical(inputCanonical);
                        ((org.hl7.fhir.dstu3.model.StructureDefinition) theInput).getSnapshot().getElement().clear();
                        ((org.hl7.fhir.dstu3.model.StructureDefinition) theInput).getSnapshot().getElement().addAll(generatedDstu3.getSnapshot().getElement());
                        break;
                    case R4:
                        org.hl7.fhir.r4.model.StructureDefinition generatedR4 = (org.hl7.fhir.r4.model.StructureDefinition) converter.fromCanonical(inputCanonical);
                        ((org.hl7.fhir.r4.model.StructureDefinition) theInput).getSnapshot().getElement().clear();
                        ((org.hl7.fhir.r4.model.StructureDefinition) theInput).getSnapshot().getElement().addAll(generatedR4.getSnapshot().getElement());
                        break;
                    case R5:
                        org.hl7.fhir.r5.model.StructureDefinition generatedR5 = (org.hl7.fhir.r5.model.StructureDefinition) converter.fromCanonical(inputCanonical);
                        ((org.hl7.fhir.r5.model.StructureDefinition) theInput).getSnapshot().getElement().clear();
                        ((org.hl7.fhir.r5.model.StructureDefinition) theInput).getSnapshot().getElement().addAll(generatedR5.getSnapshot().getElement());
                        break;
                    case DSTU2:
                    case DSTU2_HL7ORG:
                    case DSTU2_1:
                    default:
                        throw new IllegalStateException("Can not generate snapshot for version: " + version);
                }
            } finally {
               theValidationSupportContext.getCurrentlyGeneratingSnapshots().remove(inputUrl);
           }
            
            return theInput;

        } catch (BaseServerResponseException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalErrorException("Failed to generate snapshot", e);
        }
    }

    private void logValidationMessage(ValidationMessage e) {
        if(e.getLevel().isError())
            log.error(e.toString());
        else if(e.getLevel() == ValidationMessage.IssueSeverity.WARNING)
            log.warn(e.toString());
        else
            log.info(e.toString());
    }
}
