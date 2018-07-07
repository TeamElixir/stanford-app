package org.elixir.parseTree;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Constituent;
import edu.stanford.nlp.trees.LabeledScoredConstituentFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.elixir.controllers.SentencesController;
import org.elixir.models.Phrase;
import org.elixir.models.Sentence;
import org.elixir.utils.CustomizeSentimentAnnotator;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class ParseTreeDemo {

    public static void main(String[] args) {
        // set up pipeline properties
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,natlog,sentiment");

        // use faster shift reduce parser
        props.setProperty("parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz");
        props.setProperty("parse.maxlen", "100");

        // set up Stanford CoreNLP pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        try {
            CustomizeSentimentAnnotator.addSentimentLayerToCoreNLPSentiment("sentimentAnnotator/DeviatedSentimentWords/non_positive_mini.csv",
                    "sentimentAnnotator/DeviatedSentimentWords/non_negative_mini.csv",
                    "sentimentAnnotator/DeviatedSentimentWords/non_neutral_mini.csv");
        } catch (FileNotFoundException e) {
            System.out.println("sentiment file is not found.");
        }

//        String sentence = "The small red car turned very quickly around the corner.";

        for (int i = 11; i < 19; i++) {
            String caseFileName = "cases_set/case_" + i + ".txt";
            ArrayList<Sentence> sanitizedSentencesOfCase = SentencesController.getSanitizedSentencesOfCase(i);
            if (sanitizedSentencesOfCase != null) {
                for (Sentence sentence : sanitizedSentencesOfCase) {
                    extractPhrasesFromSentence(pipeline, sentence.getSentence(), caseFileName);
                }
            } else {
                System.err.println("No sentences found for case" + i);
            }
        }
    }

    // extract noun or verb phrases from a given sentence and find the sentiment of each phrase
    private static void extractPhrasesFromSentence(StanfordCoreNLP pipeline, String sentence, String caseFileName) {
        // build annotation for a review
        Annotation annotation = new Annotation(sentence);

        // annotate
        pipeline.annotate(annotation);

        // get tree
        Tree tree = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(TreeCoreAnnotations.TreeAnnotation.class);
        System.out.println(tree);
        Set<Constituent> treeConstituents = tree.constituents(new LabeledScoredConstituentFactory());
        for (Constituent constituent : treeConstituents) {
            if (constituent.label() != null &&
                    (constituent.label().toString().equals("VP") || constituent.label().toString().equals("NP"))) {
                List<Tree> trees = tree.getLeaves().subList(constituent.start(), constituent.end() + 1);
                String sentenceFromTreeLeafList = getSentenceFromTreeLeafList(trees);
                String sentimentOfSentence = getSentimentOfSentence(sentenceFromTreeLeafList, pipeline);
                Phrase phrase = new Phrase(sentenceFromTreeLeafList, sentimentOfSentence, caseFileName);

                System.out.println(sentenceFromTreeLeafList + ": " + sentimentOfSentence);
            }
        }
    }

    // combine the words in the list to a string
    private static String getSentenceFromTreeLeafList(List<Tree> treeLeaves) {
        StringBuilder sentence = new StringBuilder();
        for (Tree treeLeaf : treeLeaves) {
            sentence.append(treeLeaf).append(" ");
        }

        return sentence.toString();
    }

    // return the sentiment (positive, neutral, negative) given a sentence
    // and the annotation pipeline
    private static String getSentimentOfSentence(String sentence, StanfordCoreNLP pipeline) {
        Annotation annotation = new Annotation(sentence);
        pipeline.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        CoreMap coreMapSentence = sentences.get(0);
//        System.out.println(coreMapSentence.toString());
        CustomizeSentimentAnnotator.createPosTagMapForSentence(coreMapSentence.toString());
        String sentiment = coreMapSentence.get(SentimentCoreAnnotations.SentimentClass.class);
        return sentiment.toLowerCase();
    }
}

