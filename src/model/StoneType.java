package model;

public enum StoneType {

    YELLOW(2),
    ORANGE(3),
    RED(4);

    public final int probability;

    // Associe à chaque type sa probabilité P (1 chance sur P de transformer la fourmi en ressource).
    StoneType(int probability) {
        this.probability = probability;
    }
}
