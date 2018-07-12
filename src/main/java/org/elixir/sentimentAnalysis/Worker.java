package org.elixir.sentimentAnalysis;

import org.elixir.controllers.PhrasesController;
import org.elixir.models.Phrase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class Worker {
    public static void main(String[] args) {
        writeFilteredPhrasesToDB();
    }

    private static void writeFilteredPhrasesToDB() {
        String filePath = Objects.requireNonNull(ClassLoader.getSystemClassLoader().
                getResource("SentimentAnalysis/Performance_test_sentiment/PhraseTestDataSet_1000.csv")).
                getPath();
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\",\"";

        try {
            br = new BufferedReader(new FileReader(filePath));
            int i = 1;
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] split = line.split(cvsSplitBy);

                String phrase = removeQuotes(split[1]);
                String caseFileName = removeQuotes(split[3]);
                Phrase p = new Phrase(i++, phrase, null, caseFileName);
                System.out.println(p);
                boolean inserted = PhrasesController.insertPhraseToDB(p);
                if (!inserted) {
                    System.err.println(p.getPhrase() + ": failed to insert!");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String removeQuotes(String input) {
        input = input.trim();
        // remove leading and trailing quotes
        if ("\"".equals(input.substring(0, 1)) && "\"".equals(input.substring(input.length() - 1))) {
            input = input.substring(1, input.length() - 1);
        }
        return input;
    }
}
