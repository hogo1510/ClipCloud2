package org.ClipCloud;

import java.io.FileWriter;
import java.io.IOException;

public class Save2File {

    public static void save(String Data){
    String FilePath = "src/main/java/org/ClipCloud/berichten/";
    try {
        FileWriter myWriter = new FileWriter(FilePath+"test.txt");
        myWriter.write(Data);
        myWriter.close();
        System.out.println("Successfully wrote to the file.");
    } catch (
    IOException e)

        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
