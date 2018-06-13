package org.elixir.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PrintToFile {
    public static void print(String folder, String filename,String text){
        String filePath = new File("").getAbsolutePath() + "/src/main/resources/sentiment_analysis/arguments/";
        filePath = filePath + folder + "/" + filename;

        try {
            BufferedWriter bf = new BufferedWriter(new FileWriter(new File(filePath),true));
            bf.append(text);
            bf.flush();
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
