package org.elixir;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;
import org.elixir.models.Node;
import org.elixir.utils.NLPUtils;
import org.elixir.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class RoleExtraction {

    static ArrayList<String> personEntities = new ArrayList<>();

    /*
     *1st step is to pick the main two character names from the topic so that we have two names
     * Resolve ET AL.  as well
     */

    public static void populatePersonEntities(){
        personEntities.add("petitioner");
        personEntities.add("defendant");
        personEntities.add("government");
        personEntities.add("couple");
        personEntities.add("official");
        personEntities.add("respondent");
        personEntities.add("gay");
        personEntities.add("lesbian");
        personEntities.add("counsel");
        personEntities.add("attorney");
        personEntities.add("man");
        personEntities.add("woman");
        personEntities.add("partner");
        personEntities.add("director");
        personEntities.add("department");
        personEntities.add("people");
        personEntities.add("person");
        personEntities.add("husband");
        personEntities.add("wife");
        personEntities.add("citizen");
//        personEntities.add("");
//        personEntities.add("");
//        personEntities.add("");





        }

    public static void main(String[] args) {

        ArrayList<String> plaintiffTermList = new ArrayList<>();
        ArrayList<String> defendantTermList = new ArrayList<>();


        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog, openie, ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

                                                         //read case
        String text = "Michigan, Kentucky, Ohio, and Tennessee define marriage as a union between two people. The petitioners, John, 14 same-sex couples and two men whose same-sex partners are deceased, filed suits in Federal District Courts in their home States, claiming that respondent state officials violate the Fourteenth Amendment by denying them the right to marry or to have marriages lawfully performed in another State given full recognition.";


        ArrayList<String> rawSentences = new ArrayList<>();
        Document doc = new Document(text);
        List<Sentence> sentences1 = doc.sentences();
        for (Sentence sentence : sentences1) {
            rawSentences.add(sentence.toString());                          //extract the sentences in the given case
        }

        for (String rawSentence : rawSentences) {

            //String ss = rawSentence.replaceAll("(that,|that|'s)", "");            //replace words that misleads triple extraction
            //finalRawSentence = rawSentences.get(rawSentences.size() - 1);

            // create an empty Annotation just with the given text
            Annotation document = new Annotation(rawSentence);

            // run all Annotators on this text
            pipeline.annotate(document);

            // these are all the sentences in this document
            // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
            List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

            for (CoreMap sentence : sentences) {

               // String rawSentence1 = sentence.get(CoreAnnotations.TextAnnotation.class);

                // traversing the words in the current sentence
                // a CoreLabel is a CoreMap with additional token-specific methods
                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    // this is the text of the token
                    String word = token.get(CoreAnnotations.LemmaAnnotation.class);
                    // this is the POS tag of the token
                    String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                    // this is the NER label of the token
                    String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                    System.out.println(word + " " + pos + " " + ne);



                }

                //if comma separated add them to the two separate lists accordingly
                //todo
                //for now let's assume topic contains only two parties
                List<CoreLabel> tokenList = sentence.get(CoreAnnotations.TokensAnnotation.class);

                for (CoreLabel token:tokenList){
                    String word = token.get(CoreAnnotations.TextAnnotation.class);
                    if(word.equals(plaintiffTermList.get(0))){
                        int prevTokenIndex = tokenList.indexOf(token);
                        while (prevTokenIndex > 0){
                            CoreLabel prevToken = tokenList.get(prevTokenIndex);
                            String prevPOS = prevToken.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                            String prevNER = prevToken.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                            String prevWord = prevToken.get(CoreAnnotations.TextAnnotation.class);
                            String prevLemma = prevToken.get(CoreAnnotations.LemmaAnnotation.class);
                            if(prevWord.equals(",") || prevWord.equals("and") || prevPOS.equals("JJ") || prevPOS.equals("CD")){
                                prevTokenIndex -= 1;
                                continue;
                            }

                            if(prevNER.equals("PERSON") || prevNER.equals("ORGANIZATION")){
                                plaintiffTermList.add(prevWord);
                                prevTokenIndex -= 1;
                                continue;
                            }

                            if(personEntities.contains(prevLemma)){
                                plaintiffTermList.add(prevLemma);
                                prevTokenIndex -= 1;
                                continue;
                            }

                            break;

                        }
                    }
                }


            }
        }
    }   // main
}
