package org.elixir;

//party identification and extraction happens in this class.

/*task 1
* provide manually extracted phrases and check the sentiment
*/

/*task 2
* solve coreferences in sentences and save in the database, along with that it may require to store mentions
*/

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCostAndGradient;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.ejml.simple.SimpleMatrix;
import org.elixir.utils.CustomizeSentimentAnnotator;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Properties;

/*task 3
*design algorithm;
*/
public class Party_Extraction_Main {

    public static void main(String[] args) throws FileNotFoundException {

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,natlog,sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        String text = " government reject the fact.";

        CustomizeSentimentAnnotator.addSentimentLayerToCoreNLPSentiment("sentimentAnnotator/DeviatedSentimentWords/non_positive_mini.csv",
                "sentimentAnnotator/DeviatedSentimentWords/non_negative_mini.csv",
                "sentimentAnnotator/DeviatedSentimentWords/non_neutral_mini.csv");
        CustomizeSentimentAnnotator.createPosTagMapForSentence(text);

        Annotation document =  new Annotation(text);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for(CoreMap sentence : sentences){
            System.out.println(CustomizeSentimentAnnotator.getPhraseSentiment(sentence));

            System.out.println(SentimentCostAndGradient.nonNegativeList.size());

            final Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
            final SimpleMatrix sm = RNNCoreAnnotations.getPredictions(tree);
            final String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            System.out.println("sentence:  "+sentence);
            System.out.println("sentiment: "+sentiment);
            System.out.println("matrix:    "+sm);
        }

    }
}
