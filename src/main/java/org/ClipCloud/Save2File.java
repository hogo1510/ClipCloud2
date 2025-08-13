package org.ClipCloud;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public class Save2File {

    public static String getFilename() {
        File dir = new File("src/main/java/org/ClipCloud/berichten/");
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("Map 'berichten' bestaat niet of is geen map!");
            return null;
        }
        Optional<File> latestFile = Arrays.stream(dir.listFiles())
                .filter(file -> file.getName().matches("^\\d{4}\\.json$"))
                .max(Comparator.comparing(File::getName));

        return latestFile.map(File::getName).orElse(null);
    }


    public static void save(String data) {
        String dirPath = "src/main/java/org/ClipCloud/berichten/";
        File directory = new File(dirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        for (int num = 0; num <= 9999; num++) {
            String fileName = String.format("%04d", num) + ".json";
            File file = new File(dirPath + fileName);

            if (file.exists()) {
                continue;
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(data);
                System.out.println("Bestand aangemaakt en beschreven: " + fileName);
                return;
            } catch (IOException e) {
                System.err.println("Fout bij schrijven naar " + fileName + ": " + e.getMessage());
            }
        }
        System.err.println("Geen vrij bestandsnummer gevonden (0000-9999 zijn allemaal bezet).");
    }
}