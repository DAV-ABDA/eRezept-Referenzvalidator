# eRezept-Referenzvalidator auf Basis des HAPI-FHIR-Validators

## Build
Es gibt drei Submodule:
* core enthält die eigentliche Validator Implementierung. Diese wird im CLI verwendet, kann aber auch 
  direkt in Java Projekten als Bibliothek verwendet werden. Zur Laufzeit besteht eine Abhängigkeit zum packages
  Modul
* packages enthält die zu ladenden Profile
* cli enthält eine Kommandozeilen Implementierung

### Releasen
Die drei Module werden in ein maven Repository hochgeladen:
* `gradlew publishToMavenLocal` baut eine rein lokale Release, die sich im .m2 Ordner im Home Verzeichnis des Users
* `gradlew publish` baut eine offizielle Release und lädt diese ins öffentliche Repository hoch

## Packages
Anpassungen der Packages:
 - Add Package dav.kbv.sfhir.cs.vs-1.0.2-json.tgz (KBV Schlüsseltabellen - externe CodeSytseme/ValueSets)
 - Delete examples
 - de.gematik.erezept-workflow.r4-1.0.3-1.tgz
   - Delete ProFile StructureDefinition-ChargeItem-erxChargeItem.json (keine Relevanz - future use)
 - kbv.ita.erp-1.0.1.tgz
   - Change ProFile KBV_PR_ERP_Prescription.json (MedicationRequest.insurance = "type":[{"code":"Reference","targetProfile":["https://fhir.kbv.de/StructureDefinition/KBV_PR_FOR_Coverage|1.0.3"]}])

