package model;

public class Seed extends MovingEntity {

    // Crée une graine à une position, avec sa destination de germination.
    public Seed(Coordinate position, Coordinate target) {
        super(position);
        this.target = target;
    }
}
