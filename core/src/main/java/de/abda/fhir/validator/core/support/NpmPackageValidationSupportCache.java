package de.abda.fhir.validator.core.support;

import ca.uhn.fhir.context.FhirContext;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;
import org.hl7.fhir.instance.model.api.IBaseResource;

/**
 * Erweiterung des std. Lademechanismus zur Unterstützung von Caching. D.h.
 * Versionierte Resourcen werden im Cache abgelegt und nicht bei jeder Anfragen
 * erneut geladen.
 * 
 * @author Georg Tsakumagos
 *
 */
public class NpmPackageValidationSupportCache {

	private final Map<String, PrePopulatedValidationSupport> packageCache = new ConcurrentHashMap<>(0);
	private final FhirContext fhirContext;
	

	/**
	 * Konstruktor
	 * 
	 * @param ctx Der Kontext. Darf nicht <code>null</code> sein.
	 */
	public NpmPackageValidationSupportCache(FhirContext ctx) {
		this.fhirContext = ctx;
	}

	public PrePopulatedValidationSupport createPrePopulatedValidationSupport(List<String> packageFilesToLoad) throws IOException {

		final PrePopulatedValidationSupport result = new PrePopulatedValidationSupport(this.fhirContext);
		
		
		for(String packagePath : packageFilesToLoad) {
			PrePopulatedValidationSupport fragment = this.packageCache.get(packagePath);
			
			if (null == fragment) {
				synchronized(this.packageCache) {
					fragment = this.packageCache.get(packagePath);
					
					if (null == fragment) {
						NpmPackageValidationSupport loader = new NpmPackageValidationSupport(this.fhirContext);
						loader.loadPackageFromClasspath(packagePath);
						fragment = loader;
						this.packageCache.put(packagePath, fragment);
					}
				}

				for(IBaseResource resource : fragment.fetchAllConformanceResources()) {
					result.addResource(resource);
				}
			}
		}
		
		/*
		 * Scheinbar gibt es Abhängigkeiten oder die erzeugten Instanzen werden nachträglich manipuliert.
		 * Wenn diese Instanzen wiederverwendet werden, führt dies zu Fehlern wenn unterschiedliche 
		 * Profilversionen hintereinander validiert werden. 
		 */
		this.packageCache.clear();
		
		return result;
	}


}
