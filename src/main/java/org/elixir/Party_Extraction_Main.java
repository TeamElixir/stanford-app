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
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.ejml.simple.SimpleMatrix;
import org.elixir.utils.CoreNLPDepParser;
import org.elixir.utils.CustomizeSentimentAnnotator;

import java.io.*;
import java.util.*;

/*task 3
*design algorithm;
*/


public class Party_Extraction_Main {

    public static void main(String[] args) throws FileNotFoundException {

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,natlog,depparse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Properties propsSentiment = new Properties();
        propsSentiment.setProperty("annotators","tokenize,ssplit,tokenize,pos,lemma,parse,natlog,sentiment");
        StanfordCoreNLP sentimentPipeline = new StanfordCoreNLP(propsSentiment);

        Scanner sc = new Scanner(new File("/home/viraj/Desktop/case_11.txt"));

        CustomizeSentimentAnnotator.addSentimentLayerToCoreNLPSentiment("sentimentAnnotator/DeviatedSentimentWords/non_positive_mini.csv",
                "sentimentAnnotator/DeviatedSentimentWords/non_negative_mini.csv",
                "sentimentAnnotator/DeviatedSentimentWords/non_neutral_mini.csv");

//        PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream("/home/viraj/Desktop/result_corenlp.txt")));
//        System.setOut(ps);




            String text = sc.nextLine();
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
            System.out.println(ann.get(CoreAnnotations.SentencesAnnotation.class).size());

            for(IndexedWord[] array : CoreNLPDepParser.findIndicesOfOuterVerbAndInnerVerb(ann, text)){
                IndexedWord thatIndex = array[1];
                IndexedWord innerVerbIndex = array[0];

                String innerSentence = endExtractedInnerSentence(startPointIndexInnerSentence(array[1].beginPosition(),array[0].beginPosition(),text),text);
                innerSentence = innerSentence + " when police arrives";
                innerSentence = WhTagFilterInEnd(innerSentence);

                String innerVerb = text.substring(array[0].beginPosition(), text.indexOf(" ",array[0].beginPosition()));
                IndexedWord innersubject = CoreNLPDepParser.findRelatedDepWordForGivenWord(ann,"nsubj",innerVerb);
                String innerSubjectContext = "none-innersubject";
                if(innersubject != null){
                    innerSubjectContext = CoreNLPDepParser.findSubjectContext(ann,innersubject);

                }

                IndexedWord outerVerb = CoreNLPDepParser.findRelatedGovWordForGivenWord(ann,"ccomp",innerVerb);
                String outerVerbContext = CoreNLPDepParser.findVerbContext(ann,outerVerb);

                IndexedWord outerSubject = CoreNLPDepParser.findRelatedDepWordForGivenWord(ann,"nsubj",outerVerb.originalText());
                String outerSubjectContext = "none-outerSubject";
                if(outerSubject != null){
                    outerSubjectContext = CoreNLPDepParser.findSubjectContext(ann, outerSubject);
                }


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
//                    System.out.println("sentiment: "+sentiment);
//                    System.out.println("matrix:    "+sm);
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
//                    System.out.println("sentiment: "+sentiment);
//                    System.out.println("matrix:    "+sm);
                    System.out.println("\n");

                }
            }


    }


    //should provide the part of sentence begins after the term "that" : fine
    public static String endExtractedInnerSentence(int startIndex , String text){
        //todo : if that followed by when, where or although kind a word dismiss for now
        char[] endPointChars = {',', '.', '?', '\n'};

        int firstEndPointIndex = text.length();

        for(int count =startIndex; count<text.length(); count++){
            if(endPointChars[0] == text.charAt(count) || endPointChars[1] == text.charAt(count) || endPointChars[2] == text.charAt(count) || endPointChars[3] == text.charAt(count)){
                if(firstEndPointIndex>count){
                    firstEndPointIndex = count;
                    break;
                }
            }
        }
        String returnString = null;
        if(firstEndPointIndex > -1){
            returnString = text.substring(startIndex,firstEndPointIndex);
        }
        return returnString;
    }

    //returns the start of inner sentence, should provide the index of that: verb related with the verb prior to that, and whole sentence
    public static int startPointIndexInnerSentence(int indexOfThat,int verbIndex, String sentence){

        char[] startChars = {','};
        int startIndex1 = indexOfThat+4;

        for(int i=verbIndex;i>0;i--){
            if (startChars[0] == sentence.charAt(i) && startIndex1>i+1){
                    startIndex1 = i+1;
                    break;
            }
        }
        return startIndex1;
    }

    //when the sentence is like " ---- when <some sentence>" needs to remove that when part (any wh word)
    public static String WhTagFilterInEnd(String text){
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
