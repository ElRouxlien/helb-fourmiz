package model;

public abstract class Entity {

    public Coordinate position;

    // Crée une entité à la position donnée.
    public Entity(Coordinate position) {
        this.position = position;
    }
}
