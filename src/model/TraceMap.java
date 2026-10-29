package model;

public class TraceMap {

    // Incrément de trace à chaque passage d'une fourmi
    public static final int TRACE_DELTA = 12;

    // Valeurs de vert du pavage initial (deux cases voisines alternent) et plafond d'une composante
    private static final int GREEN_A = 0x59;
    private static final int GREEN_B = 0x95;
    private static final int MAX_TRACE = 255;

    private final int size;
    private final int[][] redTraces;
    private final int[][] blueTraces;

    // Crée les deux cartes de traces (rouge et bleue) vides, de la taille donnée.
    public TraceMap(int size) {
        this.size = size;
        redTraces = new int[size][size];
        blueTraces = new int[size][size];
    }

    // Ajoute une trace sur une case selon le type de fourmi (plafonnée à 255).
    public void addTrace(AntType type, int x, int y) {
        if (type == AntType.RED) {
            redTraces[x][y] = Math.min(MAX_TRACE, redTraces[x][y] + TRACE_DELTA);
        } else {
            blueTraces[x][y] = Math.min(MAX_TRACE, blueTraces[x][y] + TRACE_DELTA);
        }
    }

    // Renvoie le total de fréquentation d'une case (traces rouges + bleues).
    public int visits(int x, int y) {
        return redTraces[x][y] + blueTraces[x][y];
    }

    // Reconstruit la couleur d'une case : rouge = traces rouges, vert = pavage initial, bleu = traces bleues.
    public String getCellColor(int row, int column) {
        int green;
        if ((row + column) % 2 == 0) {
            green = GREEN_A;
        } else {
            green = GREEN_B;
        }
        return String.format("%02X%02X%02X", redTraces[row][column], green, blueTraces[row][column]);
    }

    // Efface les traces des fourmis rouges.
    public void resetRed() {
        clear(redTraces);
    }

    // Efface les traces des fourmis bleues.
    public void resetBlue() {
        clear(blueTraces);
    }

    // Efface toutes les traces.
    public void resetAll() {
        resetRed();
        resetBlue();
    }

    // Remet à zéro une carte de traces.
    private void clear(int[][] traces) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                traces[i][j] = 0;
            }
        }
    }
}
