package de.abda.fhir.validator.core.support;

import ca.uhn.fhir.context.FhirContext;
import java.util.Optional;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;
import org.hl7.fhir.dstu3.model.MetadataResource;
import org.hl7.fhir.instance.model.api.IBaseResource;

/**
 * Wrapper für den Lookup von Resourcen sie mit <em>Pipe Notation</em> für die Version spezifiziert sind.
 * 
 * @author Georg Tsakumagos
 *
 */
public class VersionPipePrePopulatedValidationSupport extends PrePopulatedValidationSupport {

	private static final String SPLITTER = "|";

	/**
	 * Konstruktor
	 * @param theContext Der FHIR Kontext. DArf nicht <code>null</code> sein.
	 */
	public VersionPipePrePopulatedValidationSupport(FhirContext theContext) {
		super(theContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IBaseResource fetchCodeSystem(String resourceUIR) {
		IBaseResource result = super.fetchCodeSystem(resourceUIR);
		
		if ( null == result) {
			Optional<String[]> fragmentOP = this.splitVersion(resourceUIR);
			
			if (fragmentOP.isPresent()) {
				String[] fragments = fragmentOP.get();
				result = super.fetchCodeSystem(fragments[0]);
				return this.filterVersion(result, fragments[1]);
			}
		}
		return result;
	}

	private IBaseResource filterVersion(IBaseResource result, String string) {
		if ( null != result) {
			if (MetadataResource.class.isAssignableFrom(result.getClass())) {
				MetadataResource versionedResult = (MetadataResource)result;
				
				if (string.equals(versionedResult.getVersion())) {
					return result;
				}
			}
		}
		return null;
	}

	private Optional<String[]> splitVersion(String resourceUIR) {
		final int lastIndexOf = resourceUIR.lastIndexOf(SPLITTER);

		if ( lastIndexOf > 0 ) {
			return Optional.of(
					new String[] {
							resourceUIR.substring(0, lastIndexOf),
							resourceUIR.substring(lastIndexOf + 1)
					}
					);
		}
		return Optional.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IBaseResource fetchValueSet(String resourceUIR) {
		IBaseResource result = super.fetchValueSet(resourceUIR);
		
		if ( null == result) {
			Optional<String[]> fragmentOP = this.splitVersion(resourceUIR);
			
			if (fragmentOP.isPresent()) {
				String[] fragments = fragmentOP.get();
				result = super.fetchValueSet(fragments[0]);
				return this.filterVersion(result, fragments[1]);
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IBaseResource fetchStructureDefinition(String resourceUIR) {
		IBaseResource result = super.fetchStructureDefinition(resourceUIR);
		
		if (null == result) {
			Optional<String[]> fragmentOP = this.splitVersion(resourceUIR);
			
			if (fragmentOP.isPresent()) {
				String[] fragments = fragmentOP.get();
				result = super.fetchStructureDefinition(fragments[0]);
				return this.filterVersion(result, fragments[1]);
			}
		}
		return result;
	}
	
	
	

}
