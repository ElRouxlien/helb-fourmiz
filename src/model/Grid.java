package model;

import java.util.ArrayList;
import java.util.List;

public class Grid {

    private final int size;

    // Crée une grille carrée de la taille donnée.
    public Grid(int size) {
        this.size = size;
    }

    // Indique si une coordonnée se trouve à l'intérieur de la grille.
    public boolean isCoordinateInBoard(Coordinate coord) {
        return coord.x >= 0 && coord.y >= 0 && coord.x < size && coord.y < size;
    }

    // Renvoie les 8 cases voisines d'une coordonnée (diagonales comprises).
    public List<Coordinate> getAdjacentCoordinates(Coordinate coord) {
        List<Coordinate> resultList = new ArrayList<Coordinate>();
        for (int i = coord.x - 1; i <= coord.x + 1; i++) {
            for (int j = coord.y - 1; j <= coord.y + 1; j++) {
                if (!(i == coord.x && j == coord.y)) {
                    resultList.add(new Coordinate(i, j));
                }
            }
        }
        return resultList;
    }

    // Renvoie les cases voisines qui restent à l'intérieur de la grille.
    public List<Coordinate> getAccessibleAdjacentCoordinates(Coordinate coord) {
        List<Coordinate> resultList = new ArrayList<Coordinate>();
        for (Coordinate adjacent : getAdjacentCoordinates(coord)) {
            if (isCoordinateInBoard(adjacent)) {
                resultList.add(adjacent);
            }
        }
        return resultList;
    }
}
