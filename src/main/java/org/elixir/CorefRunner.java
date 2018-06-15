package org.elixir;

import edu.stanford.nlp.coref.data.CorefChain;
import org.elixir.controllers.CorefChainOfParagraphsController;
import org.elixir.controllers.ParagraphsController;
import org.elixir.controllers.SentencesOfParagraphsController;
import org.elixir.models.CorefChainOfParagraph;
import org.elixir.models.Paragraph;
import org.elixir.models.SentenceOfParagraph;
import org.elixir.utils.NLPUtils;
import org.elixir.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class CorefRunner {
    public static void main(String[] args) {
//        insertSentencesOfParasToDB();
//        insertParagraphsIntoDB();
//        insertCorefChainsOfParasIntoDB();
        exampleUsage();
    }

    private static void exampleUsage() {
        for (int i = 11; i < 21; i++) {
            System.out.println("Case: " + i);
            System.out.println("-----------------------------");
            ArrayList<Paragraph> paragraphsOfCase = ParagraphsController.getParagraphsOfCase(i);
            for (Paragraph p : paragraphsOfCase) {
                System.out.println("Paragraph: " + p);
                ArrayList<CorefChainOfParagraph> corefChainsOfPara = CorefChainOfParagraphsController.getCorefChainsOfPara(p.getId());
                for (CorefChainOfParagraph corefChain : corefChainsOfPara) {
                    System.out.println(corefChain);
                }
                System.out.println("--------------------\n");
            }
            System.out.println("========================\n");
        }
    }

    private static void insertSentencesOfParasToDB() {
        for (int i = 11; i <= 20; i++) {
            System.out.println("Paragraph: " + i);
            ArrayList<Paragraph> paragraphsOfCase = ParagraphsController.getParagraphsOfCase(i);
            for (Paragraph p : paragraphsOfCase) {
                ArrayList<String> sentencesOfText = NLPUtils.getSentencesOfText(p.getParagraph());
                for (String sentence : sentencesOfText) {
                    SentenceOfParagraph sentenceOfParagraph = new SentenceOfParagraph(p.getId(), sentence);
                    SentencesOfParagraphsController.insertSentenceOfParagraph(sentenceOfParagraph);
                }
            }
        }
    }

    private static void insertCorefChainsOfParasIntoDB() {
        for (int i = 11; i <= 20; i++) {
            System.out.println("Paragraph: " + i);
            ArrayList<Paragraph> paragraphsOfCase = ParagraphsController.getParagraphsOfCase(i);
            for (Paragraph p : paragraphsOfCase) {
                ArrayList<CorefChain> corefChains = NLPUtils.getCorefChains(p.getParagraph());
                for (CorefChain cc : corefChains) {
                    CorefChainOfParagraph chainOfPara = new CorefChainOfParagraph(p.getId(), cc.toString());
                    CorefChainOfParagraphsController.insertCorefChainOfParagraphs(chainOfPara);
                }
            }
        }
    }

    private static void insertParagraphsIntoDB() {
        ArrayList<String> fullCases = getFullCases();
        int i = 11;
        for (String case_ : fullCases) {
            String[] paragraphs = case_.split("\n");
            for (String paragraph : paragraphs) {
                Paragraph p = new Paragraph(i, paragraph.trim());
                // validate paragraph
                String[] wordsOfPara = p.getParagraph().split(" ");
                if (wordsOfPara.length > 5) {
                    boolean inserted = ParagraphsController.insertParagraph(p);
                }

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
