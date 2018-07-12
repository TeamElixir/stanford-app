package org.elixir;

import org.elixir.partyExtraction.PartyExtractionMain;

import java.io.IOException;

public class TempMain {
    public static void main(String[] args) throws IOException {
        PartyExtractionMain.processIntermediateFile("case_20");
//        Properties propsSentiment = new Properties();
//        propsSentiment.setProperty("annotators", "tokenize,ssplit,tokenize,pos,lemma,parse,natlog,sentiment");
//        StanfordCoreNLP sentimentPipeline = new StanfordCoreNLP(propsSentiment);
//
//        String innerSentence = "Lee's counsel has performed deficiently.";
//
//        try {
//            CustomizeSentimentAnnotator.addSentimentLayerToCoreNLPSentiment("sentimentAnnotator/DeviatedSentimentWords/non_positive_mini.csv",
//                    "sentimentAnnotator/DeviatedSentimentWords/non_negative_mini.csv",
//                    "sentimentAnnotator/DeviatedSentimentWords/non_neutral_mini.csv");
//        } catch (FileNotFoundException e) {
//            System.out.println("SentimentCostAndGradientInputFile is not recognized");
//        }
//        CustomizeSentimentAnnotator.createPosTagMapForSentence(innerSentence);
//
//        Annotation ann2 = new Annotation(innerSentence);
//        sentimentPipeline.annotate(ann2);
//
//        List<CoreMap> sentences = ann2.get(CoreAnnotations.SentencesAnnotation.class);
//
//        for (CoreMap coreMapSentence : sentences) {
//            final Tree tree = coreMapSentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
//            final SimpleMatrix sm = RNNCoreAnnotations.getPredictions(tree);
//            final String sentiment = coreMapSentence.get(SentimentCoreAnnotations.SentimentClass.class);
//            System.out.println("sentence:  " + coreMapSentence);
//            System.out.println("sentiment: " + sentiment);
//            System.out.println("matrix:    " + sm);
//        }
    }
}
