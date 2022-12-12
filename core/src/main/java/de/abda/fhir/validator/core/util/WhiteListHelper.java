package de.abda.fhir.validator.core.util;

import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import de.abda.fhir.validator.core.configuration.FhirProfileVersion;

import java.util.List;

public class WhiteListHelper {

    public static void applyWhiteLists(List<SingleValidationMessage> messages, FhirProfileVersion fhirProfileVersion) {
        removeDAVActorIdentifierErrors(messages, fhirProfileVersion);
        //add custom whitelist filters here
    }

    //This ain't failsafe. A better solution has to be found for the long term as this might ignore more errors than just the bad identifier definition in the profile (or better make a better/correct profile)
    // --> fixed in package de.abda.eRezeptAbgabedaten 1.1.0
    public static void removeDAVActorIdentifierErrors(List<SingleValidationMessage> messages, FhirProfileVersion fhirProfileVersion) {
        // TODO: eigentlich WhiteListHelper pro ProfileVersion / Package
        if ((fhirProfileVersion.getVersion().equals("1.0.3")) && (fhirProfileVersion.getRequiredPackage().getPackageName().equals("de.abda.erezeptabgabedaten"))) {
            messages.removeIf(singleValidationMessage -> {
                if (singleValidationMessage.getSeverity() == ResultSeverityEnum.ERROR &&
                        (
                                (singleValidationMessage.getMessage().contains("http://fhir.abda.de/Identifier/DAV-Herstellerschluessel") && singleValidationMessage.getMessage().contains("http://fhir.de/NamingSystem/arge-ik/iknr")) ||
                                        singleValidationMessage.getMessage().contains("http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-PR-ERP-ZusatzdatenEinheit") ||
                                        singleValidationMessage.getMessage().contains("http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-PR-ERP-ZusatzdatenHerstellung") ||
                                        singleValidationMessage.getMessage().contains("http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-PR-Base-ZusatzdatenHerstellung")
                        )
                ) return true;
                else return false;
            });
        }
    }

}
