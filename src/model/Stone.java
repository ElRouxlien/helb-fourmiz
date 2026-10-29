package model;

public class Stone extends Entity {

    public final StoneType type;

    // Crée une pierre philosophale du type donné à la position donnée.
    public Stone(Coordinate position, StoneType type) {
        super(position);
        this.type = type;
    }
}
