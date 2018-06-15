package org.elixir;

import edu.stanford.nlp.coref.data.CorefChain;
import org.elixir.controllers.CasesController;
import org.elixir.controllers.CorefChainOfParagraphsController;
import org.elixir.controllers.ParagraphsController;
import org.elixir.controllers.SentencesOfParagraphsController;
import org.elixir.models.Case;
import org.elixir.models.CorefChainOfParagraph;
import org.elixir.models.Paragraph;
import org.elixir.models.SentenceOfParagraph;
import org.elixir.utils.NLPUtils;
import org.elixir.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class CorefRunner {
    public static void main(String[] args) {
        int caseNumber = 12;
        Case aCase = CasesController.getCaseByNumber(caseNumber);
        System.out.println("Case: " + caseNumber);
        ArrayList<Paragraph> paragraphs = aCase.getParagraphs();
        for (Paragraph p : paragraphs) {
            ArrayList<SentenceOfParagraph> sentences = p.getSentences();
            for (int i = 0; i < sentences.size(); i++) {
                System.out.println((i+1) + ": " + sentences.get(i).getSentence());
            }

            System.out.println();

            ArrayList<CorefChainOfParagraph> corefChains = p.getCorefChains();
            if (corefChains.size() == 0) {
                System.out.println("No coref chains!");
            }
            for (CorefChainOfParagraph corefChain : corefChains) {
                System.out.println(corefChain.getCorefChain());
            }

            System.out.println("--------\n");
        }
    }

    private static void insertSentencesOfParasToDB() {
        for (int i = 11; i <= 20; i++) {
            System.out.println("Paragraph: " + i);
            ArrayList<Paragraph> paragraphsOfCase = ParagraphsController.getParagraphsOfCase(i);
            for (Paragraph p : paragraphsOfCase) {
                ArrayList<String> sentencesOfText = NLPUtils.getSentencesOfText(p.getText());
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
                ArrayList<CorefChain> corefChains = NLPUtils.getCorefChains(p.getText());
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
        for (String aCase : fullCases) {
            String[] paragraphs = aCase.split("\n");
            for (String paragraph : paragraphs) {
                Paragraph p = new Paragraph(i, paragraph.trim());
                // validate paragraph
                String[] wordsOfPara = p.getText().split(" ");
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
