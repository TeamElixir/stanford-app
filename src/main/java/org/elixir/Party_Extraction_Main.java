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

    public static void main(String[] args) throws IOException {

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

        BufferedWriter br = new BufferedWriter(new FileWriter(new File("/home/viraj/Desktop/coreNLP_party_fullinfo_jaelee.txt")));
        BufferedWriter br2 = new BufferedWriter(new FileWriter(new File("/home/viraj/Desktop/jayLee_parties.txt")));

        ArrayList<Subject_combination> combinationArrayList = new ArrayList<>();

        while (sc.hasNextLine()){
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
                innerSentence = WhTagFilterInEnd(innerSentence);

                IndexedWord innersubject = CoreNLPDepParser.findSubjectForGivenVerb(ann,array[0]);
                String innerSubjectContext = "none-innersubject";
                if(innersubject != null){
                    innerSubjectContext = CoreNLPDepParser.findSubjectContext(ann,innersubject);

                }

                IndexedWord outerVerb = CoreNLPDepParser.findRelatedGovWordForGivenWord(ann,"ccomp",array[0]);
                String outerVerbContext = CoreNLPDepParser.findVerbContext(ann,outerVerb);

                IndexedWord outerSubject = CoreNLPDepParser.findSubjectForGivenVerb(ann,outerVerb);

                String outerSubjectContext = "none-outerSubject";
                if(outerSubject != null){
                    outerSubjectContext = CoreNLPDepParser.findSubjectContext(ann, outerSubject);
                }


                CustomizeSentimentAnnotator.createPosTagMapForSentence(innerSentence);
                Annotation ann2 = new Annotation(innerSentence);
                System.out.println(innerSentence);
                sentimentPipeline.annotate(ann2);

                List<CoreMap> sentences = ann2.get(CoreAnnotations.SentencesAnnotation.class);
                br.write(text+"\n");

                System.out.println("innerSentence : "+innerSentence);
                br.write("\t"+ "innerSentence : " + innerSentence +"\n");

                Subject_combination subject_combination = new Subject_combination();
                subject_combination.party_1 = innerSubjectContext;
                subject_combination.party_2 = outerSubjectContext;

                for(CoreMap coreMapSentence : sentences) {
                    final Tree tree = coreMapSentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                    final SimpleMatrix sm = RNNCoreAnnotations.getPredictions(tree);
                    final String sentiment = coreMapSentence.get(SentimentCoreAnnotations.SentimentClass.class);
                    System.out.println("sentence:  "+coreMapSentence);
                    System.out.println("sentiment: "+sentiment);
                    System.out.println("matrix:    "+sm);
                    br.write("\t\t" + "coremap sentence : " + coreMapSentence+"\n");
                    br.write("\t\t" + "sentiment : " + sentiment+"\n");
                    br.write("\t\t" + "matrix" + sm+"\n");
                    br.write("\t\t---------------------------------------------------------\n");
                    System.out.println();
                    subject_combination.sm1 =sm;
                    if(sentiment.toLowerCase().equals("negative")){
                        subject_combination.inner_sentiment = "negative";
                    }else{
                        subject_combination.inner_sentiment = "non-negative";
                    }
                }


                String outerSentence = outerSubjectContext.trim()+" "+ outerVerbContext+ "something.";
                CustomizeSentimentAnnotator.createPosTagMapForSentence(outerSentence);

                ann2 = new Annotation(outerSentence);
                sentimentPipeline.annotate(ann2);
                sentences = ann2.get(CoreAnnotations.SentencesAnnotation.class);

                System.out.println("outerSentence");
                br.write("\t"+ "innerSentence : " + outerSentence +"\n");
                subject_combination.innerSentence = innerSentence;
                subject_combination.outerSentence = outerSentence;
                for(CoreMap coreMapSentence : sentences) {

                    final Tree tree = coreMapSentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                    final SimpleMatrix sm = RNNCoreAnnotations.getPredictions(tree);
                    final String sentiment = coreMapSentence.get(SentimentCoreAnnotations.SentimentClass.class);
                    System.out.println("sentence:  "+coreMapSentence);
                    System.out.println("sentiment: "+sentiment);
                    System.out.println("matrix:    "+sm);
                    br.write("\t\t" + "coremap sentence : " + coreMapSentence+"\n");
                    br.write("\t\t" + "sentiment : " + sentiment+"\n");
                    br.write("\t\t" + "matrix" + sm+"\n");
                    br.write("\t\t---------------------------------------------------------\n");
                    System.out.println("\n");
                    subject_combination.sm2 = sm;

                    if(sentiment.toLowerCase().equals("negative")){
                        subject_combination.outer_sentiment = "negative";
                    }else{
                        subject_combination.outer_sentiment = "non-negative";
                    }
                }

                br.write("\t outer Subject : "+outerSubjectContext);
                br.write(", \t inner Subject : "+ innerSubjectContext+"\n");
                combinationArrayList.add(subject_combination);
            }
            br.newLine();
        }
        br.close();

        br2.write("one negative and the other is non-negative\n\n");
        for(Subject_combination combination : combinationArrayList){
            if((combination.inner_sentiment.equals("negative") && combination.outer_sentiment.equals("non-negative"))
                    || combination.inner_sentiment.equals("non-negative") && combination.outer_sentiment.equals("negative")){
                br2.write(combination.party_1 + " - " + combination.party_2 + "\n");
            }
        }
        br2.write("-----------------\n\n");

        br2.write("both non-negative\n\n");
        for(Subject_combination combination : combinationArrayList){
            if(combination.inner_sentiment.equals("negative") && combination.outer_sentiment.equals("negative")){
                br2.write(combination.party_1 + " - " + combination.party_2 + "\n");
            }
        }
        br2.write("-----------------\n\n");
        br2.flush();
        br2.close();

        for(Subject_combination combination : combinationArrayList){
            if(combination.inner_sentiment.equals("non-negative") && combination.outer_sentiment.equals("non-negative")){
                System.out.println(combination.innerSentence);
                System.out.println(combination.sm1);
                System.out.println(combination.outerSentence);
                System.out.println(combination.sm2);
                System.out.println(combination.party_1 + " - " +combination.party_2);
                System.out.println("------------------");
                System.out.println();

            }
        }
    }


    //should provide the part of sentence begins after the term "that" : fine
    public static String endExtractedInnerSentence(int startIndex , String text){
        //todo : if that followed by when, where or although kind a word dismiss for now
        char[] endPointChars = {',', ':',';','?'};

        int firstEndPointIndex = text.length();

        for(int count =startIndex; count<text.length(); count++){
            if(endPointChars[0] == text.charAt(count) || endPointChars[1] == text.charAt(count) ){
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

        char[] startChars = {',',':',';'};
        int startIndex1 = indexOfThat+4;

        for(int i=verbIndex;i>0;i--){
            if (startChars[0] == sentence.charAt(i) && startIndex1<i+1){
                    startIndex1 = i+1;
                    break;
            }
        }
        return startIndex1;
    }

    //when the sentence is like " ---- when <some sentence>" needs to remove that when part (any wh word)
    //todo : use the previously used annotation
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

    static class Subject_combination{
        String party_1;
        String party_2;
        String inner_sentiment;
        String outer_sentiment;
        SimpleMatrix sm1;
        SimpleMatrix sm2;
        String innerSentence;
        String outerSentence;

    }




}
