import controller.Controller;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    // Point d'entrée JavaFX : instancie le contrôleur principal.
    @Override
    public void start(Stage stage) {
        new Controller(stage);
    }

    // Lance l'application JavaFX.
    public static void main(String[] args) {
        launch(args);
    }
}
