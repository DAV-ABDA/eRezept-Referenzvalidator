package de.abda.fhir.validator.core.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.instance.model.api.IBaseResource;

public class ParserHelper {

    private FhirContext ctx = null;

    public ParserHelper(FhirContext ctx) {
        this.ctx = ctx;
    }

    public ParserHelper() {
        this.ctx = FhirContext.forR4();
    }

    public IBaseResource parseString(String xmlInput) {
        return ParserHelper.parseString(xmlInput, this.ctx);
    }

    static public IBaseResource parseString(String xmlInput, FhirContext ctx) {
        IParser xmlParser = ctx.newXmlParser();
        IBaseResource resource = xmlParser.parseResource(xmlInput);
        return resource;
    }

}
