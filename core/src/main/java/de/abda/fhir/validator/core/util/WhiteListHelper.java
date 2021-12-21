package de.abda.fhir.validator.core.util;

import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;

import java.util.List;

public class WhiteListHelper {

    public static void applyWhiteLists(List<SingleValidationMessage> messages) {
        removeKBVIdentifierWarnings(messages);
        removeDAVActorIdentifierErrors(messages);
        //add custom whitelist filters here
    }

    public static void removeKBVIdentifierWarnings(List<SingleValidationMessage> messages) {
        messages.removeIf(singleValidationMessage -> {
            if(singleValidationMessage.getSeverity() == ResultSeverityEnum.WARNING && (
                    (singleValidationMessage.getMessage().contains("http://fhir.de/CodeSystem/identifier-type-de-basis#GKV")) ||
                    (singleValidationMessage.getMessage().contains("http://terminology.hl7.org/CodeSystem/v2-0203#LANR")) ||
                    (singleValidationMessage.getMessage().contains("http://terminology.hl7.org/CodeSystem/v2-0203#BSNR"))
            )) return true;
            else return false;
        });
    }

    //TODO This ain't failsafe. A better solution has to be found for the long term as this might ignore more errors than just the bad identifier definition in the profile (or better make a better/correct profile)
    public static void removeDAVActorIdentifierErrors(List<SingleValidationMessage> messages) {
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
