package de.abda.fhir.validator.core.util;

import org.hl7.fhir.instance.model.api.IBaseResource;

public class ProfileHelper {

    public static Profile getProfile(IBaseResource resource) {
        String metaProfile = resource.getMeta().getProfile().get(0).getValueAsString();
        String[] splittedString = metaProfile.split("\\|");
        // TODO: Was wenn keine Version angegeben (gematik)?
        if (splittedString.length < 2)
        {
            //String defaultVersion = "0.0.0";
            return new Profile(metaProfile, splittedString[0], "0.0.0");
        }
        else
        {
            return new Profile(metaProfile, splittedString[0], splittedString[1]);
        }
    }
}
