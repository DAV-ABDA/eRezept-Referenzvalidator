# eRezept-Referenzvalidator auf Basis des HAPI-FHIR-Validators

## Verwendung
Der Referenzvalidator wird auf zwei Arten bereitgestellt: Als Fat-Jar und als Artefakt auf Maven Central

### Fat-Jar
Das Fat-Jar enthält alle Abhängigkeiten und kann daher standalone ohne das Nachladen von Ressourcen
verwendet werden. Es dient als "Schiedsichter", mit dem ohne weitere Voraussetzungen FHIR Dateien
auf Ihre Validität getestet werden können.

`````shell
java -jar reference-validator-cli.jar myFhirResource.xml
`````

### Einbindung in Maven oder Gradle Builds
Die Bereitstellung des Validators als Artefakte auf Maven Central dient dazu, den Validator in
andere Systeme einbindbar zu machen und so die Validierung als Funktionalität zu integrieren, z.B. 
in Abrechnungssysteme oder Warenwirtschaftssysteme. 

Die Artefakte werden unter der GroupId `de.abda` bereitgestellt. Es gibt zwei Artefakte mit den 
ArtefactIds:
* `fhir-validator-core`: Die einbindbaren Klassen
* `fhir-validator-packages`: Die notwendigen FHIR Profile für den Validator.

**Achtung:** Der Validator benutzt die FHIR Basisfunktionalitäten von `ca.uhn.hapi.fhir`. Die 
zugesicherte Korrektheit der Validierung ist nur gegeben, wenn zur Laufzeit auch exakt die angegebenen
Versionen verwendet werden. Bitte stellen Sie sicher, dass Ihr Maven oder Gradle Build entsprechend
konfiguriert ist.

Dafür können sie die ebenfalls veröffentlichte BOM (Bill of Materials) verwenden, ArtefaktId ist
`fhir-validator-bom`. Bitte lesen Sie in der Maven bzw. Gradle Dokumentation nach, wie man diese
BOM so einbindet, dass immer die korrekten Versionen verwendet werden.

# Contribution
   
In diesem Kapitel finden sich Informationen für alle, die den Validator entweder selbst bauen
oder zum Projekt beitragen möchten.

## Build
Es gibt drei Submodule:
* `core` enthält die eigentliche Validator Implementierung. Diese wird im CLI verwendet, kann aber auch 
  direkt in Java Projekten als Bibliothek verwendet werden. Zur Laufzeit besteht eine Abhängigkeit zum packages
  Modul
* `packages` enthält die zu ladenden Profile
* `cli` enthält eine Kommandozeilen Implementierung

Das Projekt arbeitet mit einem Gradle Build. Das Projekt kann mittels des Gradle-Wrappers gebaut
werden, ohne vorher ein Gradle selbst installieren zu müssen:
````shell
gradlew build
````

### Releasen
                   
Es gibt folgende Gradle Tasks, die zum Releasen verwendet werden können:
* `snapshot`: Erzeugt ein Snaphot Release. Dieses kennzeichnet sich durch ein `-SNAPSHOT` als Suffix 
  der Versionsnummer. Snapshot Releases werden auch als solche zu Maven Central hochgeladen und können
  dort abgerufen werden, wenn die korrekte Repository URL im Gradle oder Maven Build verwendet wird. Bei Snapshot
  Releases wird der Sourcecode NICHT getaggt.
* `final`: Erzeugt ein offizielles Release. Dazu wird auf Basis des letzten Versionstags im Git 
  standardmäßig die Minor Nummer hochgezählt (es wird das Nummernschema von sematik Versioning
  verwendet: major.minor.patch). Das Release wird zu Maven Central ins Staging System hochgeladen,
  und der SourceCode Stand wird mit der Versionsnummer getaggt.
  * Sollte nicht die Minor Number, sondern die Patch Number erhöht werden, dann bitte folgenden 
    Parameter setzen: `gradlew final -Prelease.scope=patch`
  * Sollte nicht die Minor Number, sondern die Majir Number erhöht werden, dann bitte folgenden
    Parameter setzen: `gradlew final -Prelease.scope=major`    
   
#### Ablauf eines offiziellen Release:
* Wir befinden uns auf dem `main` Branch, alle lokalen Änderungen wurden committed und gepushed.
* Es wird `gradlew final` aufgerufen und damit die Artefakte gebaut und hochgeladen (das `cli` 
  Subprojekt wird NICHT zu Maven Central hochgeladen)
* In einem Browser wird XXX geöffnet und sich dort eingelogged. Dort sieht man das hochgeladene
  Release bei Maven Central und kann es entweder wiederrufen oder freigeben, letzteres ist der Normalfall. Erst nach 
  der Freigabe können andere Nutzer auf dieses offizielle Release zugreifen!
* In GitHub unter "Releases" "Create a new Release" anklicken. In der Maske unter "Choose a tag" das
  erzeugte Versionstag auswählen, im Feld "Release title" die Versionsnummer nochmal eingeben
  und im Feld "Describe this release" die Release Notes eintragen. Darunter kann man Dateien
  hochladen, das unter `cli/build/libs` erzeugte Fat-Jar damit hochladen und "Publish release"
  anklicken.

## Packages
Anpassungen der Packages:
 - Add Package dav.kbv.sfhir.cs.vs-1.0.2-json.tgz (KBV Schlüsseltabellen - externe CodeSytseme/ValueSets)
 - Delete examples
 - de.gematik.erezept-workflow.r4-1.0.3-1.tgz
   - Delete ProFile StructureDefinition-ChargeItem-erxChargeItem.json (keine Relevanz - future use)
 - kbv.ita.erp-1.0.1.tgz
   - Change ProFile KBV_PR_ERP_Prescription.json (MedicationRequest.insurance = "type":[{"code":"Reference","targetProfile":["https://fhir.kbv.de/StructureDefinition/KBV_PR_FOR_Coverage|1.0.3"]}])

