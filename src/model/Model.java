package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Model {

    // Dimensions de la grille et position de la fourmilière
    public static final int GRID_SIZE = 20;
    public static final int CAMP_ROW = 0;
    public static final int CAMP_COLUMN = 0;
    // Nombre de ressources (et de fourmis pouvant sortir du camp) au lancement
    public static final int RESOURCE_COUNT = 6;

    // Proportion de fourmis rouges (le reste est bleu)
    private static final double RED_RATIO = 0.4;

    // Responsabilités déléguées : géométrie de la grille et système de traces
    private final Grid grid = new Grid(GRID_SIZE);
    private final TraceMap traceMap = new TraceMap(GRID_SIZE);

    // Éléments du jeu
    private final List<Coordinate> resources = new ArrayList<Coordinate>();
    private final List<Ant> ants = new ArrayList<Ant>();
    private final List<Seed> seeds = new ArrayList<Seed>();
    private final List<Stone> stones = new ArrayList<Stone>();

    // Compteurs de la partie
    private int collectedCount = 0;
    private int emergedAntCount = 0;

    // Drapeaux de gel (cheat codes)
    private boolean redAntsFrozen = false;
    private boolean blueAntsFrozen = false;
    private boolean seedsFrozen = false;

    // Crée le modèle : place les ressources initiales et fait apparaître la première pierre.
    public Model() {
        generateResources(RESOURCE_COUNT);
        spawnStone();
    }

    public int getGridSize() {
        return GRID_SIZE;
    }

    public int getCollectedCount() {
        return collectedCount;
    }

    // Compte les fourmis d'un type donné présentes sur la grille.
    public int countAnts(AntType type) {
        int count = 0;
        for (Ant ant : ants) {
            if (ant.type == type) {
                count++;
            }
        }
        return count;
    }

    // Renvoie la couleur d'une case (pavage + traces) au format 6 hexadécimaux.
    public String getCellColor(int row, int column) {
        return traceMap.getCellColor(row, column);
    }

    // Indique si la case correspond à la fourmilière.
    public boolean isCamp(int row, int column) {
        return row == CAMP_ROW && column == CAMP_COLUMN;
    }

    public List<Coordinate> getResources() {
        return resources;
    }

    public List<Ant> getAnts() {
        return ants;
    }

    public List<Seed> getSeeds() {
        return seeds;
    }

    public List<Stone> getStones() {
        return stones;
    }

    // Fait avancer la simulation d'un tick : émergence, déplacement des fourmis puis des graines.
    public void update() {
        spawnAnt();
        moveAnts();
        moveSeeds();
    }

    // Cheat code : fait apparaître une fourmi du type donné sur une case libre aléatoire.
    public void spawnRandomAnt(AntType type) {
        Coordinate position = findRandomFreeCoordinate();
        if (position == null) {
            return;
        }
        ants.add(new Ant(position, type));
    }

    // Cheat code : fait apparaître une ressource sur une case libre aléatoire.
    public void spawnRandomResource() {
        Coordinate position = findRandomFreeCoordinate();
        if (position == null) {
            return;
        }
        resources.add(position);
    }

    // Cheat code : fait apparaître une graine (avec sa destination) sur une case libre aléatoire.
    public void spawnRandomSeed() {
        Coordinate position = findRandomFreeCoordinate();
        if (position == null) {
            return;
        }
        Coordinate target = findLeastVisitedCoordinate();
        if (target == null) {
            return;
        }
        seeds.add(new Seed(position, target));
    }

    // Cheat code : fait apparaître une pierre philosophale additionnelle.
    public void spawnRandomStone() {
        spawnStone();
    }

    // Cheat code : gèle ou dégèle le déplacement des fourmis rouges.
    public void toggleFreezeRedAnts() {
        redAntsFrozen = !redAntsFrozen;
    }

    // Cheat code : gèle ou dégèle le déplacement des fourmis bleues.
    public void toggleFreezeBlueAnts() {
        blueAntsFrozen = !blueAntsFrozen;
    }

    // Cheat code : gèle ou dégèle le déplacement des graines.
    public void toggleFreezeSeeds() {
        seedsFrozen = !seedsFrozen;
    }

    // Cheat code : gèle tout, ou dégèle tout si tous les éléments étaient déjà gelés.
    public void toggleFreezeAll() {
        boolean allFrozen = redAntsFrozen && blueAntsFrozen && seedsFrozen;
        redAntsFrozen = !allFrozen;
        blueAntsFrozen = !allFrozen;
        seedsFrozen = !allFrozen;
    }

    // Cheat code : collecte toutes les ressources (chacune génère une graine, sans dépôt au camp).
    public void collectAllResources() {
        for (Coordinate resource : new ArrayList<Coordinate>(resources)) {
            removeResource(resource);
            spawnSeed(resource);
        }
    }

    // Cheat code : force toutes les fourmis à retourner au camp.
    public void forceReturnToCamp() {
        for (Ant ant : ants) {
            ant.state = AntState.RETURNING;
            ant.target = new Coordinate(CAMP_ROW, CAMP_COLUMN);
        }
    }

    // Cheat code : force toutes les fourmis à repartir chercher (elles lâchent ce qu'elles portent).
    public void forceSearchResource() {
        for (Ant ant : ants) {
            ant.state = AntState.SEARCHING;
            ant.target = null;
            ant.carryingResource = false;
        }
    }

    // Cheat code : efface les traces des fourmis rouges.
    public void resetRedTraces() {
        traceMap.resetRed();
    }

    // Cheat code : efface les traces des fourmis bleues.
    public void resetBlueTraces() {
        traceMap.resetBlue();
    }

    // Cheat code : efface toutes les traces.
    public void resetAllTraces() {
        traceMap.resetAll();
    }

    // Place `count` ressources sur des cases libres tirées au hasard.
    private void generateResources(int count) {
        while (resources.size() < count) {
            int x = (int) (Math.random() * GRID_SIZE);
            int y = (int) (Math.random() * GRID_SIZE);
            if (isFree(x, y)) {
                resources.add(new Coordinate(x, y));
            }
        }
    }

    // Fait émerger une fourmi sur une case adjacente libre au camp, tant que le quota de sorties n'est pas atteint.
    private void spawnAnt() {
        if (emergedAntCount >= RESOURCE_COUNT) {
            return;
        }
        Coordinate spawn = findFreeAdjacentToCamp();
        if (spawn == null) {
            return;
        }
        ants.add(new Ant(spawn, randomType()));
        emergedAntCount++;
    }

    // Tire un type de fourmi au hasard selon la répartition 40 % rouges / 60 % bleues.
    private AntType randomType() {
        if (Math.random() < RED_RATIO) {
            return AntType.RED;
        }
        return AntType.BLUE;
    }

    // Renvoie une case libre adjacente au camp (choisie au hasard), ou null si aucune n'est libre.
    private Coordinate findFreeAdjacentToCamp() {
        Coordinate camp = new Coordinate(CAMP_ROW, CAMP_COLUMN);
        List<Coordinate> candidates = grid.getAccessibleAdjacentCoordinates(camp);
        Collections.shuffle(candidates);
        for (Coordinate candidate : candidates) {
            if (isFree(candidate.x, candidate.y)) {
                return candidate;
            }
        }
        return null;
    }

    // Déplace chaque fourmi non gelée d'un pas et lui fait éventuellement ramasser une pierre.
    private void moveAnts() {
        for (Ant ant : new ArrayList<Ant>(ants)) {
            if (isFrozen(ant)) {
                continue;
            }
            moveAnt(ant);
            collectStone(ant);
        }
    }

    // Indique si la fourmi est actuellement gelée, selon son type.
    private boolean isFrozen(Ant ant) {
        if (ant.type == AntType.RED) {
            return redAntsFrozen;
        }
        return blueAntsFrozen;
    }

    // Applique l'effet d'une pierre sur la case de la fourmi : transformation (1/P) ou dédoublement, puis réapparition.
    private void collectStone(Ant ant) {
        Stone stone = getStoneAt(ant.position);
        if (stone == null) {
            return;
        }
        stones.remove(stone);

        if (Math.random() < 1.0 / stone.type.probability) {
            transformAntIntoResource(ant);
        } else {
            duplicateAnt(ant);
        }
        spawnStone();
    }

    // Transforme la fourmi en ressource à sa position.
    private void transformAntIntoResource(Ant ant) {
        ants.remove(ant);
        resources.add(ant.position);
    }

    // Crée une copie de la fourmi sur la case libre la plus proche.
    private void duplicateAnt(Ant ant) {
        Coordinate position = findNearestFreeCoordinate(ant.position);
        if (position == null) {
            return;
        }
        ants.add(new Ant(position, ant.type));
    }

    // Fait disparaître puis réapparaître la pierre traversée par une graine (aucun autre effet).
    private void collectStoneWithSeed(Seed seed) {
        Stone stone = getStoneAt(seed.position);
        if (stone == null) {
            return;
        }
        stones.remove(stone);
        spawnStone();
    }

    // Renvoie la pierre située sur la case donnée, ou null.
    private Stone getStoneAt(Coordinate coord) {
        for (Stone stone : stones) {
            if (stone.position.isSameAs(coord)) {
                return stone;
            }
        }
        return null;
    }

    // Fait apparaître une pierre de type aléatoire sur une case libre aléatoire.
    private void spawnStone() {
        Coordinate position = findRandomFreeCoordinate();
        if (position == null) {
            return;
        }
        stones.add(new Stone(position, randomStoneType()));
    }

    // Tire un type de pierre au hasard (jaune, orange ou rouge).
    private StoneType randomStoneType() {
        StoneType[] types = StoneType.values();
        return types[(int) (Math.random() * types.length)];
    }

    // Renvoie une case libre tirée au hasard, ou null si la grille est pleine.
    private Coordinate findRandomFreeCoordinate() {
        List<Coordinate> freeCoordinates = new ArrayList<Coordinate>();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (isFree(i, j)) {
                    freeCoordinates.add(new Coordinate(i, j));
                }
            }
        }
        if (freeCoordinates.isEmpty()) {
            return null;
        }
        return freeCoordinates.get((int) (Math.random() * freeCoordinates.size()));
    }

    // Fait avancer une fourmi d'une case vers sa cible, laisse une trace, et gère son arrivée.
    private void moveAnt(Ant ant) {
        if (hasLostItsTargetResource(ant)) {
            ant.target = null;
        }
        if (ant.target == null) {
            ant.target = chooseTarget(ant);
        }
        if (ant.target == null) {
            return;
        }
        if (!ant.position.isSameAs(ant.target)) {
            ant.position = getNextCoordinateForTarget(ant.position, ant.target);
            traceMap.addTrace(ant.type, ant.position.x, ant.position.y);
        }
        if (ant.position.isSameAs(ant.target)) {
            reachTarget(ant);
        }
    }

    // Indique si la ressource ciblée par une fourmi en recherche a disparu entre-temps.
    private boolean hasLostItsTargetResource(Ant ant) {
        if (ant.state != AntState.SEARCHING || ant.target == null) {
            return false;
        }
        return !isResource(ant.target.x, ant.target.y);
    }

    // Traite l'arrivée d'une fourmi : collecte de la ressource, ou dépôt au camp, puis bascule de l'état.
    private void reachTarget(Ant ant) {
        if (ant.state == AntState.SEARCHING) {
            if (removeResource(ant.target)) {
                ant.carryingResource = true;
                ant.state = AntState.RETURNING;
                spawnSeed(ant.position);
            }
        } else {
            if (ant.carryingResource) {
                collectedCount++;
                ant.carryingResource = false;
            }
            ant.state = AntState.SEARCHING;
        }
        ant.target = null;
    }

    // Retire la ressource située aux coordonnées données ; renvoie true si elle existait.
    private boolean removeResource(Coordinate coord) {
        for (int index = 0; index < resources.size(); index++) {
            if (resources.get(index).isSameAs(coord)) {
                resources.remove(index);
                return true;
            }
        }
        return false;
    }

    // Choisit la cible d'une fourmi : le camp si elle rentre, sinon une ressource.
    private Coordinate chooseTarget(Ant ant) {
        if (ant.state == AntState.RETURNING) {
            return new Coordinate(CAMP_ROW, CAMP_COLUMN);
        }
        return chooseTargetResource(ant);
    }

    // Choisit la ressource cible : la plus proche pour une rouge, la plus éloignée pour une bleue.
    private Coordinate chooseTargetResource(Ant ant) {
        Coordinate target = null;
        double bestDistance;

        if (ant.type == AntType.RED) {
            bestDistance = Double.MAX_VALUE;
        } else {
            bestDistance = -1;
        }

        for (Coordinate resource : resources) {
            double distance = ant.position.getDistanceWith(resource);
            if (ant.type == AntType.RED) {
                if (distance < bestDistance) {
                    bestDistance = distance;
                    target = resource;
                }
            } else {
                if (distance > bestDistance) {
                    bestDistance = distance;
                    target = resource;
                }
            }
        }
        return target;
    }

    // Renvoie la case voisine accessible la plus proche de la cible (ou la position actuelle si aucune n'est libre).
    private Coordinate getNextCoordinateForTarget(Coordinate currentCoordinate, Coordinate targetCoordinate) {
        List<Coordinate> accessibleAdjacentCoordinatesList = getAccessibleAdjacentCoordinates(currentCoordinate, targetCoordinate);

        if (accessibleAdjacentCoordinatesList.isEmpty()) {
            return currentCoordinate;
        }

        Coordinate bestCoordinate = accessibleAdjacentCoordinatesList.get(0);
        double minDistance = bestCoordinate.getDistanceWith(targetCoordinate);

        for (Coordinate coord : accessibleAdjacentCoordinatesList) {
            double distance = coord.getDistanceWith(targetCoordinate);
            if (distance < minDistance) {
                minDistance = distance;
                bestCoordinate = coord;
            }
        }
        return bestCoordinate;
    }

    // Fait naître une graine près du lieu de collecte, avec pour destination une case peu fréquentée.
    private void spawnSeed(Coordinate collectPosition) {
        Coordinate start = findNearestFreeCoordinate(collectPosition);
        if (start == null) {
            return;
        }
        Coordinate target = findLeastVisitedCoordinate();
        if (target == null) {
            return;
        }
        seeds.add(new Seed(start, target));
    }

    // Déplace chaque graine vers sa destination ; à l'arrivée, elle germe en ressource.
    private void moveSeeds() {
        if (seedsFrozen) {
            return;
        }
        Iterator<Seed> iterator = seeds.iterator();
        while (iterator.hasNext()) {
            Seed seed = iterator.next();

            if (hasLostItsTargetCell(seed)) {
                seed.target = findLeastVisitedCoordinate();
            }
            if (seed.target == null) {
                continue;
            }
            if (!seed.position.isSameAs(seed.target)) {
                seed.position = getNextCoordinateForTarget(seed.position, seed.target);
            }
            collectStoneWithSeed(seed);
            if (seed.position.isSameAs(seed.target)) {
                iterator.remove();
                germinate(seed);
            }
        }
    }

    // Indique si la case de destination d'une graine n'est plus libre (hors case déjà atteinte).
    private boolean hasLostItsTargetCell(Seed seed) {
        if (seed.target == null) {
            return true;
        }
        if (seed.position.isSameAs(seed.target)) {
            return false;
        }
        return !isFree(seed.target.x, seed.target.y);
    }

    // Transforme la graine en ressource à sa position, si la case est libre.
    private void germinate(Seed seed) {
        if (isFree(seed.position.x, seed.position.y)) {
            resources.add(seed.position);
        }
    }

    // Renvoie la case libre la plus proche d'une position donnée.
    private Coordinate findNearestFreeCoordinate(Coordinate from) {
        Coordinate nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (!isFree(i, j)) {
                    continue;
                }
                Coordinate candidate = new Coordinate(i, j);
                double distance = from.getDistanceWith(candidate);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = candidate;
                }
            }
        }
        return nearest;
    }

    // Renvoie une case libre parmi les moins fréquentées (tirage au hasard entre les ex æquo).
    private Coordinate findLeastVisitedCoordinate() {
        List<Coordinate> bestCoordinates = new ArrayList<Coordinate>();
        int minVisits = Integer.MAX_VALUE;

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (!isFree(i, j)) {
                    continue;
                }
                int visits = traceMap.visits(i, j);
                if (visits < minVisits) {
                    minVisits = visits;
                    bestCoordinates.clear();
                    bestCoordinates.add(new Coordinate(i, j));
                } else if (visits == minVisits) {
                    bestCoordinates.add(new Coordinate(i, j));
                }
            }
        }

        if (bestCoordinates.isEmpty()) {
            return null;
        }
        return bestCoordinates.get((int) (Math.random() * bestCoordinates.size()));
    }

    // Indique si une case peut accueillir un nouvel élément (la pierre bloque, contrairement à isWalkable).
    private boolean isFree(int x, int y) {
        return isWalkable(x, y) && !isOccupiedByStone(x, y);
    }

    // Indique si une case est traversable (camp, ressources, fourmis et graines bloquent ; pas les pierres).
    private boolean isWalkable(int x, int y) {
        if (isCamp(x, y)) {
            return false;
        }
        if (isResource(x, y)) {
            return false;
        }
        if (isOccupiedByAnt(x, y)) {
            return false;
        }
        if (isOccupiedBySeed(x, y)) {
            return false;
        }
        return true;
    }

    // Indique si une fourmi occupe la case.
    private boolean isOccupiedByAnt(int x, int y) {
        return isOccupiedBy(ants, x, y);
    }

    // Indique si une graine occupe la case.
    private boolean isOccupiedBySeed(int x, int y) {
        return isOccupiedBy(seeds, x, y);
    }

    // Indique si une pierre occupe la case.
    private boolean isOccupiedByStone(int x, int y) {
        return isOccupiedBy(stones, x, y);
    }

    // Indique si l'une des entités données occupe la case (traite Ant/Seed/Stone via leur type mère Entity).
    private boolean isOccupiedBy(List<? extends Entity> entities, int x, int y) {
        for (Entity entity : entities) {
            if (entity.position.x == x && entity.position.y == y) {
                return true;
            }
        }
        return false;
    }

    // Indique si une ressource occupe la case.
    private boolean isResource(int x, int y) {
        for (Coordinate resource : resources) {
            if (resource.x == x && resource.y == y) {
                return true;
            }
        }
        return false;
    }

    // Renvoie les voisines traversables, en autorisant la case-cible même si elle contient une ressource.
    private List<Coordinate> getAccessibleAdjacentCoordinates(Coordinate coord, Coordinate target) {
        List<Coordinate> resultList = new ArrayList<Coordinate>();
        for (Coordinate adjacent : grid.getAdjacentCoordinates(coord)) {
            if (!grid.isCoordinateInBoard(adjacent)) {
                continue;
            }
            if (adjacent.isSameAs(target)) {
                if (!isOccupiedByAnt(adjacent.x, adjacent.y) && !isOccupiedBySeed(adjacent.x, adjacent.y)) {
                    resultList.add(adjacent);
                }
                continue;
            }
            if (isWalkable(adjacent.x, adjacent.y)) {
                resultList.add(adjacent);
            }
        }
        return resultList;
    }
}
