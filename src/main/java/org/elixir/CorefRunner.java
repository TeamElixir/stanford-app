package org.elixir;

import org.elixir.controllers.ParagraphsController;
import org.elixir.models.Paragraph;
import org.elixir.utils.NLPUtils;
import org.elixir.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class CorefRunner {
    public static void main(String[] args) {
        for (int i = 11; i < 22; i++) {
            ArrayList<Paragraph> paragraphsOfCase = ParagraphsController.getParagraphsOfCase(i);
            for (Paragraph p : paragraphsOfCase) {
                NLPUtils.getCorefChains(p.getParagraph());
                System.exit(0);
            }
        }
    }

    private static void insertParagraphsIntoDB() {
        ArrayList<String> fullCases = getFullCases();
        int i = 11;
        for (String case_ : fullCases) {
            String[] paragraphs = case_.split("\n");
            for (String paragraph : paragraphs) {
                Paragraph p = new Paragraph(i, paragraph);
                boolean inserted = ParagraphsController.insertParagraph(p);
            }
            System.out.println("Case: " + i++);
        }
    }

    private static ArrayList<String> getFullCases() {
        ArrayList<String> fullCases = new ArrayList<>();
        for (int i = 11; i < 21; i++) {
            String casesPath = ClassLoader.getSystemClassLoader().getResource("").getPath() + "cases";
            String filePath = casesPath + File.separator + "case_" + i + ".txt";
            fullCases.add(Utils.readFile(filePath));
        }

        return fullCases;
    }
}
