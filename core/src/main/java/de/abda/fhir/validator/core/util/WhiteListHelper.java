package de.abda.fhir.validator.core.util;

import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;

import java.util.List;

public class WhiteListHelper {

    public static void applyWhiteLists(List<SingleValidationMessage> messages) {
        removeKBVIdentifierWarnings(messages);
        //add custom whitelist filters here
    }

    public static void removeKBVIdentifierWarnings(List<SingleValidationMessage> messages) {
        messages.removeIf(singleValidationMessage -> {
            if(singleValidationMessage.getSeverity() == ResultSeverityEnum.WARNING &&
                    (singleValidationMessage.getMessage().contains("http://fhir.de/CodeSystem/identifier-type-de-basis#GKV")) ||
                    (singleValidationMessage.getMessage().contains("http://terminology.hl7.org/CodeSystem/v2-0203#LANR")) ||
                    (singleValidationMessage.getMessage().contains("http://terminology.hl7.org/CodeSystem/v2-0203#BSNR"))
            ) {
                return true;
            }
            else return false;
        });
    }

}
