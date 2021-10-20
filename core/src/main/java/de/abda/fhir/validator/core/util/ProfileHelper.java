package de.abda.fhir.validator.core.util;

import org.hl7.fhir.instance.model.api.IBaseResource;

public class ProfileHelper {

    public static Profile getProfile(IBaseResource resource) {
        String metaProfile = resource.getMeta().getProfile().get(0).getValueAsString();
        String[] splittedString = metaProfile.split("\\|");
        return new Profile(metaProfile, splittedString[0] ,splittedString[1]);
    }

}
