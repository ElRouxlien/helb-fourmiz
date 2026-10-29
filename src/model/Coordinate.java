package model;

public class Coordinate {

    public int x = 0;
    public int y = 0;

    // Crée une coordonnée à la position (x, y).
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Renvoie la distance euclidienne jusqu'à une autre coordonnée.
    public double getDistanceWith(Coordinate coord) {
        return Math.sqrt((coord.y - this.y) * (coord.y - this.y) + (coord.x - this.x) * (coord.x - this.x));
    }

    // Indique si deux coordonnées désignent la même case (equals() n'étant pas redéfini).
    public boolean isSameAs(Coordinate coord) {
        return this.x == coord.x && this.y == coord.y;
    }
}
