# HAPI Referenz Validator

## Build
Es gibt drei Submodule:
* core enth�lt die eigentliche Validator Implementierung. Diese wird im CLI verwendet, kann aber auch 
  direkt in Java Projekten als Bibliothek verwendet werden. Zur Laufzeit besteht eine Abh�ngigkeit zum packages
  Modul
* packages enth�lt die zu ladenden Profile
* cli enth�lt eine Kommandozeilen Implementierung

### Releasen
Die drei Module werden in ein maven Repository hochgeladen:
* `gradlew publishToMavenLocal` baut eine rein lokale Release, die sich im .m2 Ordner im Home Verzeichnis des Users
* `gradlew publish` baut eine offizielle Release und l�dt diese ins �ffentliche Repository hoch

Soll nur das cli
