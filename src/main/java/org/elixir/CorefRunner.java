package org.elixir;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.elixir.controllers.CasesController;
import org.elixir.controllers.CorefChainOfParagraphsController;
import org.elixir.controllers.ParagraphsController;
import org.elixir.controllers.SentencesOfParagraphsController;
import org.elixir.models.Case;
import org.elixir.models.CorefChainOfParagraph;
import org.elixir.models.Paragraph;
import org.elixir.models.SentenceOfParagraph;
import org.elixir.partyExtraction.PartyExtractionMain;
import org.elixir.utils.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class CorefRunner {
    public static void main(String[] args) throws IOException {


        try {
            CustomizeSentimentAnnotator.addSentimentLayerToCoreNLPSentiment("sentimentAnnotator/DeviatedSentimentWords/non_positive_mini.csv",
                    "sentimentAnnotator/DeviatedSentimentWords/non_negative_mini.csv",
                    "sentimentAnnotator/DeviatedSentimentWords/non_neutral_mini.csv");
        } catch (FileNotFoundException e) {
            System.out.println("SentimentCostAndGradientInputFile is not recognized");
        }

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,natlog,depparse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Properties propsSentiment = new Properties();
        propsSentiment.setProperty("annotators", "tokenize,ssplit,tokenize,pos,lemma,parse,natlog,sentiment");
        StanfordCoreNLP sentimentPipeline = new StanfordCoreNLP(propsSentiment);
        //int caseNumber = 15;
        for (int caseNumber = 16; caseNumber <= 20; caseNumber++) {
            Case aCase = CasesController.getCaseByNumber(caseNumber);
            System.out.println("Case: " + caseNumber);
            String caseName = "case_" + Integer.valueOf(caseNumber);
            ArrayList<Paragraph> paragraphs = aCase.getParagraphs();

            String absoluteFilePath = new File("").getAbsolutePath();
            String target_filePath = absoluteFilePath + "/src/main/resources/SentimentAnalysis/InnerOuterSentenceSentiment/";

            File full_infoFile = new File(target_filePath + caseName + "/fullinfo.txt");
            full_infoFile.getParentFile().mkdirs();

            File intermediate_sentiment_File = new File(target_filePath + caseName + "/InnerOuterSentiment.txt");
            intermediate_sentiment_File.getParentFile().mkdirs();

            File party_file = new File(target_filePath + caseName + "/intermediate.txt");
            party_file.getParentFile().mkdirs();

            BufferedWriter br = new BufferedWriter(new FileWriter(full_infoFile));
            BufferedWriter br2 = new BufferedWriter(new FileWriter(intermediate_sentiment_File));
            BufferedWriter br3 = new BufferedWriter(new FileWriter(party_file));

            for (Paragraph p : paragraphs) {

                ArrayList<CorefChainMapping> ccmList = new ArrayList<>();

                ArrayList<CorefChainOfParagraph> corefChains = p.getCorefChains();
                if (corefChains.size() == 0) {
                    System.out.println("No coref chains!");
                }
                for (CorefChainOfParagraph corefChain : corefChains) {
                    CorefChainMapping ccm = new CorefChainMapping();
                    ccm.processCorefChainString(corefChain.getCorefChain());
                    ccmList.add(ccm);
                }

                ArrayList<SentenceOfParagraph> sentences = p.getSentences();
                for (int i = 0; i < sentences.size(); i++) {
                    String currentSentence = sentences.get(i).getSentence();

                    //main logic continues from here
                    //to check whether "that" structure sentence
                    if (!ThatSentenceIdentifier.checkVerbThatStructure(currentSentence)) {
                        continue;
                    }
                    PartyExtractionMain.outputPartyExtraction(pipeline, sentimentPipeline, i + 1, currentSentence, caseName, ccmList, br, br2, br3);
                }

                System.out.println();

                System.out.println("--------\n");
                System.out.println("finished ---------");
            }
            br.close();
            br2.close();
            br3.close();
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
