package org.elixir.utils;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCostAndGradient;
import edu.stanford.nlp.util.CoreMap;
import org.elixir.models.PosTaggedWord;
import org.elixir.models.Sentence;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class CustomizeSentimentAnnotator {

    /*
    *the words with deviated sentiment are stored in files, the directories of those files should
     * be provided. (location in the resources directory
     * */
    public static void addSentimentLayerToCoreNLPSentiment(String nonPositiveFilePath,
                                                           String nonNegativeFilePath,
                                                           String nonNeutralFilePath){

        String filePath = new File("").getAbsolutePath();
        filePath += "/src/main/resources/";

        Scanner nonPositiveScanner = new Scanner(filePath + nonPositiveFilePath);
        while(nonPositiveScanner.hasNextLine()){
            SentimentCostAndGradient.nonNeutralList.add(nonPositiveScanner.nextLine());
        }

        Scanner nonNeutralScanner = new Scanner(filePath + nonNeutralFilePath);
        while(nonNeutralScanner.hasNextLine()){
            SentimentCostAndGradient.nonNeutralList.add(nonNeutralScanner.nextLine());
        }

        Scanner nonNegativeScanner = new Scanner(filePath + nonNegativeFilePath);
        while(nonNegativeScanner.hasNextLine()){
            SentimentCostAndGradient.nonNegativeList.add(nonNegativeScanner.nextLine());
        }
    }

    /*
    this method is compulsory for using this customized SentimentAnnotator
    postag map for each sentence should be provided to the SentiementGradientAndCost class
    //todo :  still this doesnot support if same word having two pos tags in single sentence, should simplify further to support that
     */
    public static void createPosTagMapForSentence(Sentence sentence){

        //to create empty map for the word-postag combinations inside SentimentCostAndGradient class
        SentimentCostAndGradient.createPosTagMap();

        //to add the word-postag combinations to the map inside SentimentCostAndGradient class
        for(PosTaggedWord posTaggedWord: sentence.getPosTaggedWords()) {
            SentimentCostAndGradient.addPosTagsOfWords(posTaggedWord.getWord(),posTaggedWord.getPosTag());
        }

    }


    // if the sentences are not in database: use this method
    public static void createPosTagMapForSentence(String sentence){

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation annotation = new Annotation(sentence);
        pipeline.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap coreMapSentence : sentences){
            for (CoreLabel token : coreMapSentence.get(CoreAnnotations.TokensAnnotation.class)){

                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String posTag = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                SentimentCostAndGradient.addPosTagsOfWords(word,posTag);

            }
        }
    }

    /*
    * -1 for negative, 0 for neutral and 1 for positive
    */
    public static int getPhraseSentiment(CoreMap coreMapSentence){
        String sentiment = coreMapSentence.get(SentimentCoreAnnotations.SentimentClass.class);

        switch(sentiment){
            case "Negative":
                return -1;

            case "Neutral" :
                return 0 ;

            case "Positive" :
                return 1;

            default :
                return 0;
        }
    }
}
