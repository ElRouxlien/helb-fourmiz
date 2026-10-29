package model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SaveFile {

    private static final String FILE_NAME = "save.jds";

    // Écrit le fichier de sauvegarde : fourmis rouges, fourmis bleues, ressources déposées (une valeur par ligne).
    public static void save(int redAntCount, int blueAntCount, int collectedCount) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME));
            writer.println(redAntCount);
            writer.println(blueAntCount);
            writer.println(collectedCount);
            writer.close();
        } catch (IOException exception) {
            System.out.println("Error while writing " + FILE_NAME + " : " + exception.getMessage());
        }
    }
}
