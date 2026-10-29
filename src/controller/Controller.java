package controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.AntType;
import model.Model;
import model.SaveFile;
import view.View;

public class Controller {

    // Période de rafraîchissement en secondes (modifiable pour accélérer la partie)
    private static final double UPDATE_PERIOD = 0.1;

    private final Model model;
    private final View view;
    private Timeline timeline;
    private boolean simulationEnded = false;

    // Crée le modèle et la vue, branche le clavier et lance la boucle de jeu.
    public Controller(Stage stage) {
        model = new Model();
        view = new View(stage, model);

        setActions();
        startGameLoop();

        stage.show();
    }

    // Branche le clavier : chaque touche est transmise au traducteur de cheat codes.
    private void setActions() {
        view.scene.setOnKeyPressed(event -> handleKey(event.getCode()));
    }

    // Lance la boucle : à chaque tick, met à jour le modèle puis redessine la vue.
    private void startGameLoop() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(UPDATE_PERIOD), event -> {
            model.update();
            view.render();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    // Traduit une touche en action du modèle (ignorée si la simulation est terminée).
    private void handleKey(KeyCode code) {
        if (simulationEnded) {
            return;
        }
        switch (code) {
            case A:
                model.spawnRandomAnt(AntType.RED);
                break;
            case Z:
                model.spawnRandomAnt(AntType.BLUE);
                break;
            case E:
                model.spawnRandomResource();
                break;
            case R:
                model.spawnRandomSeed();
                break;
            case U:
                model.spawnRandomStone();
                break;
            case W:
                model.toggleFreezeRedAnts();
                break;
            case X:
                model.toggleFreezeBlueAnts();
                break;
            case C:
                model.toggleFreezeSeeds();
                break;
            case V:
                model.toggleFreezeAll();
                break;
            case J:
                model.collectAllResources();
                break;
            case K:
                model.forceReturnToCamp();
                break;
            case L:
                model.forceSearchResource();
                break;
            case I:
                model.resetRedTraces();
                break;
            case O:
                model.resetBlueTraces();
                break;
            case P:
                model.resetAllTraces();
                break;
            case M:
                endSimulation();
                break;
            default:
                break;
        }
        view.render();
    }

    // Arrête la simulation, fige les commandes et écrit le fichier de sauvegarde save.jds.
    private void endSimulation() {
        timeline.stop();
        simulationEnded = true;
        SaveFile.save(model.countAnts(AntType.RED), model.countAnts(AntType.BLUE), model.getCollectedCount());
    }
}
