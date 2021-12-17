package de.abda.fhir.validator.core.support;

import ca.uhn.fhir.context.support.IValidationSupport;
import java.util.Optional;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.instance.model.api.IBaseResource;

/**
 * Wrapper für den Lookup von Resourcen sie mit <em>Pipe Notation</em> für die Version spezifiziert sind.
 * 
 * @author Georg Tsakumagos
 *
 */
public class VersionPipeAwareSupportChain extends ValidationSupportChain {

	private static final String SPLITTER = "|";

	/**
	 * Konstruktor
	 * @param theValidationSupportModules {@link IValidationSupport} varargs array
	 */
	public VersionPipeAwareSupportChain(IValidationSupport... theValidationSupportModules) {
		super(theValidationSupportModules);
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
				return this.filterVersion(IBaseResource.class, result, fragments[1]);
			}
		}
		return result;
	}

	private <T extends IBaseResource> T  filterVersion(Class<T> theClass, T result, String desiredVersion) {
		if ( null != result) {
			String resourceVersion = null;
			
			if (org.hl7.fhir.dstu3.model.MetadataResource.class.isAssignableFrom(result.getClass())) {
				resourceVersion = ((org.hl7.fhir.dstu3.model.MetadataResource)result).getVersion();
			} else if (org.hl7.fhir.r4.model.MetadataResource.class.isAssignableFrom(result.getClass())) {
				resourceVersion = ((org.hl7.fhir.r4.model.MetadataResource)result).getVersion();
			} else if (org.hl7.fhir.r5.model.MetadataResource.class.isAssignableFrom(result.getClass())) {
				resourceVersion = ((org.hl7.fhir.r5.model.MetadataResource)result).getVersion();
			}

			if (null != resourceVersion && resourceVersion.equals(desiredVersion)) {
				return result;
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
				return this.filterVersion(IBaseResource.class, result, fragments[1]);
			}
		}
		return result;
	}

	
	
	@Override
	public <T extends IBaseResource> T fetchResource(Class<T> theClass, String resourceUIR) {
		T result = super.fetchResource(theClass, resourceUIR);
		
		if ( null == result) {
			Optional<String[]> fragmentOP = this.splitVersion(resourceUIR);
			
			if (fragmentOP.isPresent()) {
				String[] fragments = fragmentOP.get();
				result = super.fetchResource(theClass, fragments[0]);
				return this.filterVersion(theClass, result, fragments[1]);
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
				return this.filterVersion(IBaseResource.class, result, fragments[1]);
			}
		}
		return result;
	}

	
	
}
