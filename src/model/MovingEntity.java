package model;

public abstract class MovingEntity extends Entity {

    public Coordinate target;

    // Crée une entité mobile à la position donnée (cible non définie au départ).
    public MovingEntity(Coordinate position) {
        super(position);
    }
}
