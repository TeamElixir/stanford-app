package org.elixir;

//party identification and extraction happens in this class.

/*task 1
* provide manually extracted phrases and check the sentiment
*/

/*task 2
* solve coreferences in sentences and save in the database, along with that it may require to store mentions
*/

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import org.ejml.simple.SimpleMatrix;
import org.elixir.utils.CoreNLPDepParser;
import org.elixir.utils.CustomizeSentimentAnnotator;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/*task 3
*design algorithm;
*/


public class Party_Extraction_Main {

    public static void main(String[] args) throws FileNotFoundException {

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,natlog,depparse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Properties propsSentiment = new Properties();
        propsSentiment.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,natlog,sentiment");
        StanfordCoreNLP sentimentPipeline = new StanfordCoreNLP(propsSentiment);

        CustomizeSentimentAnnotator.addSentimentLayerToCoreNLPSentiment("sentimentAnnotator/DeviatedSentimentWords/non_positive_mini.csv",
                "sentimentAnnotator/DeviatedSentimentWords/non_negative_mini.csv",
                "sentimentAnnotator/DeviatedSentimentWords/non_neutral_mini.csv");

        String text = "Applying the two-part test for ineffective assistance claims from Strickland v. Washington, 466 U. S. 668, the Sixth Circuit concluded that, while the Government conceded that Lee's counsel had performed deficiently, Lee could not show that he was prejudiced by his attorney's erroneous advice.";

        Annotation ann = new Annotation(text);
        pipeline.annotate(ann);
//        for (CoreMap sent : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
//            SemanticGraph sg = sent.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
//            System.out.println(IOUtils.eolChar + sg.toString(SemanticGraph.OutputFormat.LIST));
//
//            for (TypedDependency i : sg.typedDependencies()){
//                System.out.println(i.dep().index() + " : " + i.dep());
//            }
//        }

        for(int[] array : CoreNLPDepParser.findIndicesOfOuterVerbAndInnerVerb(ann, text)){
            int thatIndex = array[1];
            int innerVerbIndex = array[0];

            String innerSentence = endExtractedInnerSentence(startPointIndexInnerSentence(array[1],array[0],text),text)[0];
            innerSentence = WhtagFilterInEnd(innerSentence);

            String innerVerb = text.substring(array[0], text.indexOf(" ",array[0]));
            IndexedWord innersubject = CoreNLPDepParser.findRelatedDepWordForGivenWord(ann,"nsubj",innerVerb);
            if(innersubject != null){
                String innerSubjectContext = CoreNLPDepParser.findSubjectContext(ann,innersubject);

            }

            IndexedWord outerVerb = CoreNLPDepParser.findRelatedGovWordForGivenWord(ann,"ccomp",innerVerb);
            String outerVerbContext = CoreNLPDepParser.findVerbContext(ann,outerVerb);

            IndexedWord outerSubject = CoreNLPDepParser.findRelatedDepWordForGivenWord(ann,"nsubj",outerVerb.originalText());
            String outerSubjectContext = CoreNLPDepParser.findSubjectContext(ann, outerSubject);

            CustomizeSentimentAnnotator.createPosTagMapForSentence(innerSentence);
            Annotation ann2 = new Annotation(innerSentence);
            System.out.println(innerSentence);
            sentimentPipeline.annotate(ann2);

            List<CoreMap> sentences = ann2.get(CoreAnnotations.SentencesAnnotation.class);

            System.out.println("innerSentence : "+innerSentence);
        for(CoreMap coreMapSentence : sentences) {
            final Tree tree = coreMapSentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
            final SimpleMatrix sm = RNNCoreAnnotations.getPredictions(tree);
            final String sentiment = coreMapSentence.get(SentimentCoreAnnotations.SentimentClass.class);
            System.out.println("sentence:  "+coreMapSentence);
            System.out.println("sentiment: "+sentiment);
            System.out.println("matrix:    "+sm);
            System.out.println();

        }


        String outerSentence = outerSubjectContext.trim()+" "+ outerVerbContext+ "something.";
        CustomizeSentimentAnnotator.createPosTagMapForSentence(outerSentence);

        ann2 = new Annotation(outerSentence);
        sentimentPipeline.annotate(ann2);
            sentences = ann2.get(CoreAnnotations.SentencesAnnotation.class);

            System.out.println("outerSentence");
            for(CoreMap coreMapSentence : sentences) {

                final Tree tree = coreMapSentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                final SimpleMatrix sm = RNNCoreAnnotations.getPredictions(tree);
                final String sentiment = coreMapSentence.get(SentimentCoreAnnotations.SentimentClass.class);
                System.out.println("sentence:  "+coreMapSentence);
                System.out.println("sentiment: "+sentiment);
                System.out.println("matrix:    "+sm);
                System.out.println("\n");

            }
        }







//
//        Annotation document =  new Annotation(text);
//        pipeline.annotate(document);
//        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
//
//        for(CoreMap sentence : sentences){
//
//            final Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
//            final SimpleMatrix sm = RNNCoreAnnotations.getPredictions(tree);
//            final String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
//            System.out.println("sentence:  "+sentence);
//            System.out.println("sentiment: "+sentiment);
//            System.out.println("matrix:    "+sm);
//
//        }

    }


    //should provide the part of sentence begins after the term "that"
    public static String[] endExtractedInnerSentence(int startIndex , String text){
        //todo : if that followed by when, where or although kind a word dismiss for now
        String[] endPointChars = {",", ".", "?", "\\n"};

        int firstEndPointIndex = text.length();


        for(int count =startIndex; count<text.length(); count++){
            if(Arrays.asList(endPointChars).contains(text.charAt(count))){
                if(firstEndPointIndex>count){
                    firstEndPointIndex = count;
                }
            }
        }
        String[] returnString = {null,null};
        if(firstEndPointIndex > -1){
            returnString[0] = text.substring(startIndex,firstEndPointIndex);
            if(firstEndPointIndex < text.length()-1){
                returnString[1] = text.substring(firstEndPointIndex+1);
            }
        }
        return returnString;
    }

    //returns the start of inner sentence, should provide the index of that: verb related with the verb prior to that, and whole sentence
    public static int startPointIndexInnerSentence(int indexOfThat,int verbIndex, String sentence){

        char[] startChars = {',','.'};
        int startIndex1 = indexOfThat+4;

        for(int i=verbIndex;i<0;i--){
            if (startChars[0] == sentence.charAt(i) || startChars[1] == sentence.charAt(i)){
                if(startIndex1<i){
                    startIndex1 = i;
                    break;
                }
            }
        }

        return startIndex1;
    }

    public static String WhtagFilterInEnd(String text){
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation ann = new Annotation(text);
        pipeline.annotate(ann);

        String[] whPosTags = {"WDT","WP","WRB"};


        for (CoreMap coreMapSentence : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : coreMapSentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String postag = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

                if(Arrays.asList(whPosTags).contains(postag.toUpperCase())){
                    return text.substring(0,text.indexOf(word)-1);
                }
            }
        }
        return text;
    }




}
