# HELBFourmiz

Simulation d'une colonie de fourmis en **Java / JavaFX**, réalisée dans le cadre du cours de Java (HELB, Q3).

## Principe

Deux colonies de fourmis (rouge et bleue) évoluent sur une grille. Elles se déplacent, ramassent des graines et laissent des traces de phéromones. Des pierres de couleur (jaune, orange, rouge) peuvent transformer une fourmi en ressource, avec une probabilité qui dépend de leur couleur. L'état de la simulation peut être sauvegardé dans un fichier.

## Architecture

Le projet suit le patron **MVC** :

- `model/` : grille, fourmis, graines, pierres, carte des traces, sauvegarde
- `view/` : affichage JavaFX
- `controller/` : boucle de simulation et interactions

## Lancer le projet

JavaFX doit être installé (sous Linux : `sudo apt install openjfx`).

```bash
./run.sh
```

Le chemin vers JavaFX se règle dans la variable `JAVAFX` de `run.sh`.

## Rapport

Le rapport détaillé se trouve dans [`JulienBaronJavaQ3AoutRapport.pdf`](JulienBaronJavaQ3AoutRapport.pdf).
