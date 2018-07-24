package org.elixir.parseTree;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Constituent;
import edu.stanford.nlp.trees.LabeledScoredConstituentFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.ejml.simple.SimpleMatrix;
import org.elixir.controllers.PhrasesController;
import org.elixir.controllers.SentencesController;
import org.elixir.controllers.TermsController;
import org.elixir.models.Phrase;
import org.elixir.models.Sentence;
import org.elixir.models.SentimentMatrix;
import org.elixir.models.Term;
import org.elixir.utils.CustomizeSentimentAnnotator;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.elixir.utils.NLPUtils.getParsedSentimentMatrix;

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

//        processSentences(pipeline);
//        processTerms(pipeline);
        updatePhraseSentimentsInDB(pipeline);
    }

    private static void exampleUsageOfSentiment(StanfordCoreNLP pipeline) {
        String sentment = getSentimentOfPhrase("his attorney had provided constitutionally ineffective assistance.", pipeline);
        System.out.println(sentment);
    }

    private static void processSentences(StanfordCoreNLP pipeline) {
        for (int i = 1; i <= 19; i++) {
            String caseFileName = "cases_set/case_" + i + ".txt";
            // get sentences from database
            ArrayList<Sentence> sanitizedSentencesOfCase = SentencesController.getSanitizedSentencesOfCase(i);
            if (sanitizedSentencesOfCase != null) {
                for (Sentence sentence : sanitizedSentencesOfCase) {
                    extractPhrasesFromSentence(pipeline, sentence.getSentence(), caseFileName);
                }
            } else {
                System.err.println("No sentences found for case " + i);
            }
        }
    }

    private static void processTerms(StanfordCoreNLP pipeline) {
        ArrayList<Term> allTerms = TermsController.getTermsWithASentiment();
        for (Term t : allTerms) {
            String sentiment = getSentimentOfPhrase(t.getTerm(), pipeline);
            t.setSentiment(sentiment);
            System.out.println("Term: " + t);
            boolean updated = TermsController.updateSentimentOfTerm(t);
            if (!updated) {
                System.out.println("Update failed: " + t);
            }
        }
    }

    private static void updatePhraseSentimentsInDB(StanfordCoreNLP pipeline) {
        ArrayList<Phrase> allPhrases = PhrasesController.getAllPhrases();
        for (Phrase p : allPhrases) {
            String sentiment = getSentimentOfPhrase(p.getPhrase(), pipeline);
            p.setSentiment(sentiment);
            boolean updated = PhrasesController.updateSentimentOfPhrase(p);
            if (!updated) {
                System.out.println("Update failed: " + p);
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
                String sentenceFromTreeLeafList = getPhraseFromTreeLeafList(trees);
                String sentimentOfSentence = getSentimentOfPhrase(sentenceFromTreeLeafList, pipeline);
                Phrase phrase = new Phrase(sentenceFromTreeLeafList, sentimentOfSentence, caseFileName);
                boolean phraseUseful = isPhraseUseful(phrase.getPhrase());
                if (phraseUseful) {
                    // phrase useful
                    boolean inserted = PhrasesController.insertPhraseToDB(phrase);
                    if (!inserted) {
                        System.out.println("Failed to insert " + phrase);
                    }
                } else {
                    // phrase not useful
                    System.out.println(phrase.getPhrase() + ": not useful");
                }
            }

        }
    }

    private static boolean isPhraseUseful(String phrase) {
        String[] splits = phrase.trim().split(" ");
        return splits.length >= 2;
    }

    // combine the words in the list to a string
    private static String getPhraseFromTreeLeafList(List<Tree> treeLeaves) {
        StringBuilder sentence = new StringBuilder();
        for (Tree treeLeaf : treeLeaves) {
            sentence.append(treeLeaf).append(" ");
        }

        return sentence.toString();
    }

    // return the sentiment (very positive, positive, neutral, negative, very negative) given a sentence
    // and the annotation pipeline
    private static String getSentimentOfPhrase(String phrase, StanfordCoreNLP pipeline) {
        CustomizeSentimentAnnotator.createPosTagMapForSentence(phrase);
        Annotation annotation = new Annotation(phrase);
        pipeline.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        CoreMap coreMapSentence = sentences.get(0);
        String sentiment = coreMapSentence.get(SentimentCoreAnnotations.SentimentClass.class);

        final Tree tree = coreMapSentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        final SimpleMatrix sm = RNNCoreAnnotations.getPredictions(tree);
        SentimentMatrix parsedSentimentMatrix = getParsedSentimentMatrix(sm, coreMapSentence.toString());
        System.out.println(parsedSentimentMatrix);

        if (parsedSentimentMatrix.getNegativeValue() > 0.4) {
            return "negative";
        }

        return sentiment.toLowerCase();
    }
}

