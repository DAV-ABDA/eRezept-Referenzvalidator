# eRezept-Referenzvalidator auf Basis des HAPI-FHIR-Validators

## Verwendung
Der Referenzvalidator wird auf zwei Arten bereitgestellt: Als Fat-Jar und als Artefakt auf Maven Central

### Fat-Jar
Das Fat-Jar enthält alle Abhängigkeiten und kann daher standalone ohne das Nachladen von Ressourcen
verwendet werden. Es dient als "Schiedsrichter", mit dem ohne weitere Voraussetzungen FHIR Dateien
auf Ihre Validität getestet werden können. Zum Starten ist lediglich ein Java 11 JRE (Java Runtime
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
    `gw final -Prelease.version=0.1.1`, wobei sie 0.1.1 durch die exakte neue Versionsnummer ersetzen
   
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

## Packages
Anpassungen der Packages:
 - Add Package dav.kbv.sfhir.cs.vs-1.0.2-json.tgz (KBV Schlüsseltabellen - externe CodeSytseme/ValueSets)
 - Delete examples
 - de.gematik.erezept-workflow.r4-1.0.3-1.tgz
   - Delete ProFile StructureDefinition-ChargeItem-erxChargeItem.json (keine Relevanz - future use)
 - kbv.ita.erp-1.0.1.tgz
   - Change ProFile KBV_PR_ERP_Prescription.json (MedicationRequest.insurance = "type":[{"code":"Reference","targetProfile":["https://fhir.kbv.de/StructureDefinition/KBV_PR_FOR_Coverage|1.0.3"]}])

