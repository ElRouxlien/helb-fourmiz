package model;

public class Ant extends MovingEntity {

    public AntState state;
    public boolean carryingResource;
    public final AntType type;

    // Crée une fourmi du type donné, en état de recherche et sans ressource.
    public Ant(Coordinate position, AntType type) {
        super(position);
        this.type = type;
        this.state = AntState.SEARCHING;
        this.carryingResource = false;
    }
}
