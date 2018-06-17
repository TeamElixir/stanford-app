package org.elixir;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.ejml.simple.SimpleMatrix;
import org.elixir.utils.CustomizeSentimentAnnotator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class TempMain {
    public static void main(String[] args) throws IOException {
        //Party_Extraction_Main.processIntermediateFile("case_11");
        Properties propsSentiment = new Properties();
        propsSentiment.setProperty("annotators", "tokenize,ssplit,tokenize,pos,lemma,parse,natlog,sentiment");
        StanfordCoreNLP sentimentPipeline = new StanfordCoreNLP(propsSentiment);

        String innerSentence = "Lee's counsel has performed deficiently.";

        try {
            CustomizeSentimentAnnotator.addSentimentLayerToCoreNLPSentiment("sentimentAnnotator/DeviatedSentimentWords/non_positive_mini.csv",
                    "sentimentAnnotator/DeviatedSentimentWords/non_negative_mini.csv",
                    "sentimentAnnotator/DeviatedSentimentWords/non_neutral_mini.csv");
        } catch (FileNotFoundException e) {
            System.out.println("SentimentCostAndGradientInputFile is not recognized");
        }
        CustomizeSentimentAnnotator.createPosTagMapForSentence(innerSentence);

        Annotation ann2 = new Annotation(innerSentence);
        sentimentPipeline.annotate(ann2);

        List<CoreMap> sentences = ann2.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap coreMapSentence : sentences) {
            final Tree tree = coreMapSentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
            final SimpleMatrix sm = RNNCoreAnnotations.getPredictions(tree);
            final String sentiment = coreMapSentence.get(SentimentCoreAnnotations.SentimentClass.class);
            System.out.println("sentence:  " + coreMapSentence);
            System.out.println("sentiment: " + sentiment);
            System.out.println("matrix:    " + sm);
        }
    }
}
