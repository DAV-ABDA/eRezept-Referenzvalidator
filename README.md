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
