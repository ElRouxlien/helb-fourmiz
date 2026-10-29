package view;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Ant;
import model.AntType;
import model.Coordinate;
import model.Model;
import model.Seed;
import model.Stone;
import model.StoneType;

public class View {

    // Taille d'une case en pixels
    private static final int CELL_SIZE = 30;
    // Fourmilière (hexagone)
    private static final String CAMP_COLOR = "647687";
    private static final String CAMP_STROKE = "314354";
    private static final int HEXAGON_SIDES = 6;
    // Ressource (losange)
    private static final String RESOURCE_COLOR = "f0a30a";
    private static final String RESOURCE_STROKE = "bd7000";
    // Fourmis (cercles)
    private static final String ANT_RED_COLOR = "e51400";
    private static final String ANT_RED_STROKE = "b20000";
    private static final String ANT_BLUE_COLOR = "0050ef";
    private static final String ANT_BLUE_STROKE = "001dbc";
    // Graine (étoile à 8 branches)
    private static final String SEED_COLOR = "cdeb8b";
    private static final String SEED_STROKE = "36393d";
    private static final int STAR_BRANCHES = 8;
    private static final double STAR_INNER_RATIO = 0.4;
    // Pierres philosophales (soleils), une couleur par type
    private static final String STONE_YELLOW_COLOR = "e3c800";
    private static final String STONE_YELLOW_STROKE = "b09500";
    private static final String STONE_ORANGE_COLOR = "fa6800";
    private static final String STONE_ORANGE_STROKE = "c73500";
    private static final String STONE_RED_COLOR = "a20025";
    private static final String STONE_RED_STROKE = "6f0000";
    private static final int SUN_RAYS = 8;
    private static final double SUN_INNER_RATIO = 0.45;

    public Scene scene;

    private final Model model;
    private GraphicsContext gc;

    // Construit la scène et le canvas, puis dessine l'état initial.
    public View(Stage stage, Model model) {

        this.model = model;

        int canvasSize = model.getGridSize() * CELL_SIZE;

        Group root = new Group();
        Canvas canvas = new Canvas(canvasSize, canvasSize);
        root.getChildren().add(canvas);
        gc = canvas.getGraphicsContext2D();

        scene = new Scene(root);
        stage.setTitle("HELBFourmiz");
        stage.setScene(scene);

        render();
    }

    // Redessine tout l'écran dans l'ordre de superposition (grille en dessous, fourmis au-dessus).
    public void render() {
        drawGrid();
        drawCamp();
        drawResources();
        drawSeeds();
        drawStones();
        drawAnts();
    }

    // Dessine le pavage et les traces de chaque case.
    private void drawGrid() {
        for (int i = 0; i < model.getGridSize(); i++) {
            for (int j = 0; j < model.getGridSize(); j++) {
                gc.setFill(Color.web(model.getCellColor(i, j)));
                gc.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    // Dessine la fourmilière sous la forme d'un hexagone.
    private void drawCamp() {
        double centerX = Model.CAMP_ROW * CELL_SIZE + CELL_SIZE / 2.0;
        double centerY = Model.CAMP_COLUMN * CELL_SIZE + CELL_SIZE / 2.0;
        double radius = CELL_SIZE / 2.0 - 1;

        double[] xs = new double[HEXAGON_SIDES];
        double[] ys = new double[HEXAGON_SIDES];

        for (int index = 0; index < HEXAGON_SIDES; index++) {
            double angle = index * 2 * Math.PI / HEXAGON_SIDES;
            xs[index] = centerX + radius * Math.cos(angle);
            ys[index] = centerY + radius * Math.sin(angle);
        }

        gc.setFill(Color.web(CAMP_COLOR));
        gc.fillPolygon(xs, ys, HEXAGON_SIDES);
        gc.setStroke(Color.web(CAMP_STROKE));
        gc.strokePolygon(xs, ys, HEXAGON_SIDES);
    }

    // Dessine chaque ressource sous la forme d'un losange.
    private void drawResources() {
        for (Coordinate resource : model.getResources()) {
            double x = resource.x * CELL_SIZE;
            double y = resource.y * CELL_SIZE;

            double[] xs = { x + CELL_SIZE / 2.0, x + CELL_SIZE, x + CELL_SIZE / 2.0, x };
            double[] ys = { y, y + CELL_SIZE / 2.0, y + CELL_SIZE, y + CELL_SIZE / 2.0 };

            gc.setFill(Color.web(RESOURCE_COLOR));
            gc.fillPolygon(xs, ys, 4);
            gc.setStroke(Color.web(RESOURCE_STROKE));
            gc.strokePolygon(xs, ys, 4);
        }
    }

    // Dessine chaque graine sous la forme d'une étoile à 8 branches.
    private void drawSeeds() {
        for (Seed seed : model.getSeeds()) {
            double centerX = seed.position.x * CELL_SIZE + CELL_SIZE / 2.0;
            double centerY = seed.position.y * CELL_SIZE + CELL_SIZE / 2.0;
            double outerRadius = CELL_SIZE / 2.0 - 1;
            double innerRadius = outerRadius * STAR_INNER_RATIO;

            double[] xs = new double[STAR_BRANCHES * 2];
            double[] ys = new double[STAR_BRANCHES * 2];

            for (int index = 0; index < STAR_BRANCHES * 2; index++) {
                double radius;
                if (index % 2 == 0) {
                    radius = outerRadius;
                } else {
                    radius = innerRadius;
                }
                double angle = -Math.PI / 2 + index * Math.PI / STAR_BRANCHES;
                xs[index] = centerX + radius * Math.cos(angle);
                ys[index] = centerY + radius * Math.sin(angle);
            }

            gc.setFill(Color.web(SEED_COLOR));
            gc.fillPolygon(xs, ys, STAR_BRANCHES * 2);
            gc.setStroke(Color.web(SEED_STROKE));
            gc.strokePolygon(xs, ys, STAR_BRANCHES * 2);
        }
    }

    // Dessine chaque pierre sous la forme d'un soleil, coloré selon son type.
    private void drawStones() {
        for (Stone stone : model.getStones()) {
            double centerX = stone.position.x * CELL_SIZE + CELL_SIZE / 2.0;
            double centerY = stone.position.y * CELL_SIZE + CELL_SIZE / 2.0;
            double outerRadius = CELL_SIZE / 2.0 - 1;
            double innerRadius = outerRadius * SUN_INNER_RATIO;

            gc.setFill(Color.web(getStoneColor(stone)));
            gc.setStroke(Color.web(getStoneStroke(stone)));
            gc.setLineWidth(2);

            for (int index = 0; index < SUN_RAYS; index++) {
                double angle = index * 2 * Math.PI / SUN_RAYS;
                gc.strokeLine(centerX + innerRadius * Math.cos(angle),
                        centerY + innerRadius * Math.sin(angle),
                        centerX + outerRadius * Math.cos(angle),
                        centerY + outerRadius * Math.sin(angle));
            }

            gc.fillOval(centerX - innerRadius, centerY - innerRadius, innerRadius * 2, innerRadius * 2);
            gc.strokeOval(centerX - innerRadius, centerY - innerRadius, innerRadius * 2, innerRadius * 2);
            gc.setLineWidth(1);
        }
    }

    // Renvoie la couleur de remplissage d'une pierre selon son type.
    private String getStoneColor(Stone stone) {
        if (stone.type == StoneType.YELLOW) {
            return STONE_YELLOW_COLOR;
        }
        if (stone.type == StoneType.ORANGE) {
            return STONE_ORANGE_COLOR;
        }
        return STONE_RED_COLOR;
    }

    // Renvoie la couleur de contour d'une pierre selon son type.
    private String getStoneStroke(Stone stone) {
        if (stone.type == StoneType.YELLOW) {
            return STONE_YELLOW_STROKE;
        }
        if (stone.type == StoneType.ORANGE) {
            return STONE_ORANGE_STROKE;
        }
        return STONE_RED_STROKE;
    }

    // Dessine chaque fourmi sous la forme d'un cercle, coloré selon son type.
    private void drawAnts() {
        for (Ant ant : model.getAnts()) {
            double x = ant.position.x * CELL_SIZE;
            double y = ant.position.y * CELL_SIZE;

            if (ant.type == AntType.RED) {
                gc.setFill(Color.web(ANT_RED_COLOR));
                gc.setStroke(Color.web(ANT_RED_STROKE));
            } else {
                gc.setFill(Color.web(ANT_BLUE_COLOR));
                gc.setStroke(Color.web(ANT_BLUE_STROKE));
            }

            gc.fillOval(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
            gc.strokeOval(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
        }
    }
}
