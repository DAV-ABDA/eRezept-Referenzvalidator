# eRezept-Referenzvalidator auf Basis des HAPI-FHIR-Validators

### Inhaltsverzeichnis
* Projektziele
  * NICHT-Projektziele
  * Was der Validator nicht prüft
* Verwendung
  * Fat-Jar
  * Einbindung in Maven oder Gradle Builds
* Contribution
  * Build
    * Releasen
      * Ablauf eines offiziellen Release
* Packages & Profile & Versionen
  * unterstütze Profile und Versionen
  * eingebundene Packages (R4)
    * Anpassungen der Packages

## Projektziele

* Der Referenzvalidator dient als "Schiedsrichter", mit dem ohne weitere Voraussetzungen FHIR Dateien auf Ihre Validität (valid oder nicht valid) getestet werden können. Die einzig relevante Ausgabe ist 'Validation result: true' oder 'Validation result: false'. Zusätzlich ausgegebene Warnings sind ausschließlich informell und haben keinen Einfluss auf das Ergebnis. Das positive Testergebnis ist __eine__ Voraussetzung für die Annahme von Datenlieferungen zwischen Apothekenrechenzentren und Krankenkassen(-Annahmestellen). __Alle__ weiteren Akzeptanzkriterien werden in der TA7 Spezifikation festgelegt.
* Es werden ausschließlich in den entsprechenden FHIR-Profilen enthaltene Constraints geprüft.
* Es wird immer nur genau eine FHIR-XML-Datei geprüft. Als Base64-eingebettete weitere FHIR-Daten müssen durch separate Schritte/Aufrufe extrahiert und geprüft werden.

### NICHT-Projektziele

* Falsche oder fehlende Constraints in den FHIR-Profilen werden __nicht__ mit dem Validator nachkorrigiert sondern durch versionierte Änderung der Profile.
* Der Referenzvalidator __kann__ in produktiven Umgebungen eingesetzt werden, der gibt aber derzeit __keine__ Garantie für Stabilität oder Optimierungen im Bezug auf Performance, Speicherverbrauch und Laufzeit.
* Eine Signaturvalidierung oder Zertifikatsprüfung findet __nicht__ statt.
* Das Ergebnis des Validators ist __keine__ Garantie für ein retaxierungsfreies E-Rezept.

Die Projektziele können sich zukünftig ändern.
Fehler werden über Issues gemeldet und nach den Projektzielen priorisiert bzw. akzeptiert oder abgelehnt.

### Was der Validator nicht prüft:
* Referenzen [type]/[id] <p>
  relative URL "[type]/[id]" when resource has fullUrl "urn:uuid:[id]"
* fehlerhafte UUIDs<p>
  * UUIDs werden momentan nicht auf Konformität nach RFC4122 geprüft.
* Kontrolle ob die Profilversion (nach TA7) einer Instance zum Zeitpunkt dessen Erstellung gültig war<p>
  Eine Validierung der korrekten Profilversion wird "momentan" nicht umgesetzt.
  * Verordnung, Datum der Ausstellung (KBV_PR_ERP_Prescription: MedicationRequest.authoredOn)
  * MedicationDispense, Abgabedatum (Gem_erxMedicationDispense: MedicationDispense.whenHandedOver)
  * Quittung, Ausstellungsdatum (Gem_erxComposition: Composition.date)
  * Abgabedaten, Abgabedatum (DAV_PR_ERP_Abgabeinformationen: MedicationDispense.whenHandedOver)
  * Abrechnungsdaten, Abrechnungsmonat (GKVSV_PR_TA7_Sammelrechnung_Composition: Composition.date)
* UTF-8-BOM <p>
  Das Format für alle E-Rezept-Dateien ist XML im UTF8 ohne BOM-Zeichensatz. Dieser Zeichensatz ist von der gematik für alle E-Rezept-Daten vorgegeben.

## Verwendung
Der Referenzvalidator wird auf zwei Arten bereitgestellt: Als Fat-Jar und als Artefakt auf Maven Central

### Fat-Jar
Das Fat-Jar enthält alle Abhängigkeiten und kann daher standalone ohne das Nachladen von Ressourcen
verwendet werden. Es dient als "Schiedsrichter", mit dem ohne weitere Voraussetzungen FHIR Dateien
auf Ihre Validität getestet werden können. Zum Starten ist lediglich (mindestens) ein Java 8 JRE (Java Runtime
Environment) notwendig.

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

Dafür können Sie die ebenfalls veröffentlichte BOM (Bill of Materials) verwenden, ArtefaktId ist
`fhir-validator-bom`. Bitte lesen Sie in der Maven bzw. Gradle Dokumentation nach, wie man diese
BOM so einbindet, dass immer die korrekten Versionen verwendet werden.

Die Klasse ReferenceValidator stellt das API dar und sollte in Applikationen verwendet werden:

````java
//An existing FhirContext can optionally be passed to the constructor for performance reasons
ReferenceValidator validator = new ReferenceValidator(); 

//path can be a String or a Path object
Map<ResultSeverityEnum, List<SingleValidationMessage>> errors = validator.validateFile(path);

//If the file content is already read to a String, you can use validateString
Map<ResultSeverityEnum, List<SingleValidationMessage>> errors2 = validator.validateString(stringContent);

````

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

Es gibt folgende Gradle Tasks, die zum Releasen verwendet werden können. Für diese Tasks sind
die notwendigen Zugangsdaten für Sonatype und die Daten zum Signieren notwendig, diese sind nicht
eingecheckt und werden von der ABDA nicht veröffentlicht.
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
  * Soll exakt eine bestimmte Versionsnummer gesetzt werden, dann bitte folgenden Parameter setzen:
    `gradlew final -Prelease.version=0.1.1`, wobei sie 0.1.1 durch die exakte neue Versionsnummer ersetzen

#### Ablauf eines offiziellen Release:
* Sie befinden sich auf dem `main` Branch, alle lokalen Änderungen wurden committed und gepushed.
* Es wird `gradlew clean final` aufgerufen und damit die Artefakte gebaut und hochgeladen (das `cli`
  Subprojekt wird NICHT zu Maven Central hochgeladen)
* Freigeben des Release für Maven Central:
  * In einem Browser wird https://s01.oss.sonatype.org geöffnet und sich dort mit dem ABDA-FHIR-Team
    User eingelogged.
  * Wenn man links im Menü auf "Staging Repositories" klickt, dann sieht man in der
    Mitte in der Liste ein Repository mit Namen `deabda-1234` wobei 1234 eine laufende Nummer ist.
  * Wenn man das Repository anklickt kann man unten im Tab "Content" sich die hochgeladenen Daten
    ansehen.
  * Zur Freigabe wird in horizontalen Menüleiste in der Mitte oben auf "Close" geklickt. Dann läuft
    eine interne, asynchrone Prüfung der hochgeladenen Artefakte an. Diese Prüfung dauert einige
    Sekunden, evtl. muss man mehrmals "Refresh" klicken, bis man entweder den Status "Closed" oder
    die Prüfung mit "Failed" geendet ist
  * Ist die Prüfung fehlgeschlagen kann das Repository mit dem Punkt "Drop" aus der mittleren oberen
    Menüleiste gelöscht werden, und es nochmal versucht werden.
  * Ist die Prüfung erfolgreich, dann kann das Repository mit dem Punkt "Release" aus der mitteleren
    oberen Menüleiste das Release freigegeben werden. Erst danach können andere Benutzer dieses
    Release über Maven Central abrufen!
* In GitHub unter "Releases" "Create a new Release" anklicken. In der Maske unter "Choose a tag" das
  erzeugte Versionstag auswählen, im Feld "Release title" die Versionsnummer nochmal eingeben
  und im Feld "Describe this release" die Release Notes eintragen. Darunter kann man Dateien
  hochladen, das unter `cli/build/libs` erzeugte Fat-Jar damit hochladen und "Publish release"
  anklicken.

## Packages & Profile & Versionen
### unterstütze Profile und Versionen
* https://fhir.kbv.de/StructureDefinition/KBV_PR_ERP_Bundle
  * 1.0.1
  * 1.0.2
  * 1.1.0
* https://gematik.de/fhir/StructureDefinition/ErxMedicationDispense
  * 1.0.3
  * 1.0.3-1 (Instancen ohne Versionsangaben werden gegen die Version 1.0.3-1 validiert)
  * 1.1.1
* https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_MedicationDispense
  * 1.2
* https://gematik.de/fhir/StructureDefinition/ErxReceipt
  * 1.0.3
  * 1.0.3-1 (Instancen ohne Versionsangaben werden gegen die Version 1.0.3-1 validiert)
  * 1.1.1
* https://gematik.de/fhir/erp/StructureDefinition/GEM_ERP_PR_Bundle
  * 1.2
* https://gematik.de/fhir/erpchrg/StructureDefinition/GEM_ERPCHRG_PR_ChargeItem
  * 1.0 (PreRelease)
* http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-PR-ERP-AbgabedatenBundle
  * 1.0.3
  * 1.1.0
  * 1.2
  * 1.3 (PreRelease)
* https://fhir.gkvsv.de/StructureDefinition/GKVSV_PR_TA7_Sammelrechnung_Bundle
  * 1.0.4
  * 1.0.5
  * 1.0.6
  * 1.1.0
  * 1.2
* https://fhir.gkvsv.de/StructureDefinition/GKVSV_PR_TA7_Rechnung_Bundle
  * 1.3 (PreRelease)

### eingebundene Packages (R4)
* de.basisprofil.r4-0.9.13.tgz
* de.basisprofil.r4-1.3.2.tgz
* kbv.basis-1.1.3.tgz
* kbv.basis-1.3.0.tgz (ACHTUNG! expansion SNOMED entfernt...)
* kbv.ita.for-1.0.3.tgz
* kbv.ita.for-1.1.0.tgz
* kbv.ita.erp-1.0.1.tgz
* kbv.ita.erp-1.0.2.tgz
* kbv.ita.erp-1.1.0.tgz
* ~~de.gematik.erezept-workflow.r4-1.0.3.tgz~~ (entfernt v0.9.8)
* de.gematik.erezept-workflow.r4-1.0.3-1.tgz
* de.gematik.erezept-workflow.r4-1.1.1.tgz
* de.gematik.erezept-workflow.r4-1.2.0.tgz
* de.gematik.erezept-patientenrechnung.r4-1.0.0-rc3.tgz (ACHTUNG! PreReleasePreview)
* de.abda.erezeptabgabedaten-1.0.3.tgz
* de.abda.erezeptabgabedatenbasis-1.1.0.tgz (ACHTUNG! fix issue)
* ~~de.abda.erezeptabgabedatenbasis-1.1.2.tgz~~ (entfernt v0.9.8)
* de.abda.erezeptabgabedatenbasis-1.1.3.tgz (KorrekturRelease für v1.1.0)
* de.abda.erezeptabgabedatenbasis-1.2.0.tgz
* ~~de.abda.erezeptabgabedatenbasis-1.2.1.tgz ~~ (entfernt vx.x.x TODO)
* de.abda.erezeptabgabedatenbasis-1.3.0-rc3.tgz (ACHTUNG! PreReleasePreview)
* ~~de.abda.erezeptabgabedaten-1.1.0.tgz~~  (entfernt v0.9.7)
* ~~de.abda.erezeptabgabedaten-1.1.1.tgz~~ (entfernt v0.9.8)
* de.abda.erezeptabgabedaten-1.1.2.tgz (KorrekturRelease für v1.1.0)
* de.abda.erezeptabgabedaten-1.2.0.tgz
* de.abda.erezeptabgabedaten-1.3.0-rc3.tgz (ACHTUNG! PreReleasePreview)
* de.abda.erezeptabgabedatenpkv-1.1.0-rc11.tgz (ACHTUNG! PreReleasePreview)
* de.gkvsv.erezeptabrechnungsdaten-1.0.4.tgz
* de.gkvsv.erezeptabrechnungsdaten-1.0.5.tgz
* de.gkvsv.erezeptabrechnungsdaten-1.0.6.tgz
* de.gkvsv.erezeptabrechnungsdaten-1.1.0.tgz
* de.gkvsv.erezeptabrechnungsdaten-1.2.0.tgz
* de.gkvsv.erezeptabrechnungsdaten-1.3.0-rc2.tgz (ACHTUNG! PreReleasePreview)

#### Anpassungen der Packages (TODO: ACHTUNG! Hinweis Instanzegültigkeiten mit PackageKontext (abwärtskompatible?!?))
- Add Package dav.kbv.sfhir.cs.vs-1.0.2-json.tgz (KBV Schlüsseltabellen - externe CodeSytseme/ValueSets)
- Add Package dav.kbv.sfhir.cs.vs-1.0.3-json.tgz (KBV Schlüsseltabellen - externe CodeSytseme/ValueSets)
  - Edit DARREICHUNGSFORM v1.09 ab 01.04.2022
- Add Package dav.kbv.sfhir.cs.vs-1.0.4-json.tgz (KBV Schlüsseltabellen - externe CodeSytseme/ValueSets)
  - Edit DARREICHUNGSFORM v1.10 ab 01.07.2022
  - Add SFHIR_EAU_AU_ERROR_KASSE, "status": "draft", "date": "2023-01-01" // TODO: raus da draft !!!
- Add Package dav.kbv.sfhir.cs.vs-1.0.5-json.tgz (KBV Schlüsseltabellen - externe CodeSytseme/ValueSets)
  - Edit SFHIR_EAU_AU_ERROR_KASSE, "status": "active", "date": "2023-07-01"
  
- delete examples in Packages
- de.gematik.erezept-workflow.r4-1.0.3-1.tgz
  - Delete ProFile StructureDefinition-ChargeItem-erxChargeItem.json (keine Relevanz - future use)
- de.gematik.erezept-workflow.r4-1.2.0.tgz
  - defined by package dav.kbv.sfhir.cs.vs-1.0.5-json.tgz
    - Delete KBV_CS_SFHIR_KBV_DARREICHUNGSFORM_V1.10.json
    - Delete KBV_CS_SFHIR_KBV_NORMGROESSE_V1.00.json
    - Delete KBV_VS_SFHIR_KBV_DARREICHUNGSFORM_V1.10.json
    - Delete KBV_VS_SFHIR_KBV_NORMGROESSE_V1.00.json

- kbv.ita.erp-1.0.1.tgz
  - Change Profile KBV_PR_ERP_Prescription.json (MedicationRequest.insurance = "type":[{"code":"Reference","targetProfile":["https://fhir.kbv.de/StructureDefinition/KBV_PR_FOR_Coverage|1.0.3"]}])
- ACHTUNG! FIX Issue in de.abda.erezeptabgabedatenbasis-1.1.0.tgz -> siehe ChangeLog.md - Version 0.9.6
- kbv.basis-1.3.0.tgz (ACHTUNG! expansion SNOMED entfernt...)
  - KBV_VS_Base_Diagnosis_SNOMED_CT.json
  - KBV_VS_Base_Procedure_SNOMED_CT.json
  - KBV_VS_Base_Allergy_Substance_SNOMED_CT.json
  - KBV_VS_Base_Device_SNOMED_CT.json

### Copyright 2022 Deutscher Apothekerverband (DAV)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

>    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.