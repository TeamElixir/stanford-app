package org.elixir;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class ArgumentTreeGenerator {


    private static ArrayList<String> subjectList =new ArrayList<>(Arrays.asList("Petitioner", "Government"));
    private ArrayList<String> currentSubjects= new ArrayList<>();


    public static void main(String[] args) {

        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog, openie, ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // read some text in the text variable
        String text = "Petitioners' main argument is that, had they known about the withheld evidence, they could have\n" +
                "challenged the Government's basic group attack theory by raising an alternative theory, namely, that a single\n" +
                "perpetrator (or two at most) had attacked Fuller. Considering the withheld evidence \"in the context of the\n" +
                "entire record,\" Agurs, supra, at 112, that evidence is too little, too weak, or too distant from the main\n" +
                "evidentiary points to meet Brady's standards.";

        String[] rawSentences = text.split("\\.");

        for (String rawSentence : rawSentences){


            String ss= rawSentence.replaceAll("(that,)","");
            ss.replaceAll("(that)","");

            System.out.println("splited sentence : "+ ss);
            // create an empty Annotation just with the given text
            Annotation document = new Annotation(ss);

            // run all Annotators on this text
            pipeline.annotate(document);


            // these are all the sentences in this document
            // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
            List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

            for (CoreMap sentence : sentences) {
                String rawSentence1 = sentence.get(CoreAnnotations.TextAnnotation.class);
                // traversing the words in the current sentence
                // a CoreLabel is a CoreMap with additional token-specific methods
                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    // this is the text of the token
                    String word = token.get(CoreAnnotations.TextAnnotation.class);
                    // this is the POS tag of the token
                    String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                    // this is the NER label of the token
                    String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                    //System.out.println(word + " " + pos + " " + ne);
                }

                Collection<RelationTriple> triples =
                        sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
                // Print the triples
                for (RelationTriple triple : triples) {
                    /*System.out.println(triple.confidence + "\t" +
                            triple.subjectLemmaGloss() + "\t" +
                            triple.relationLemmaGloss() + "\t" +
                            triple.objectLemmaGloss());*/
                    String sentenceSubject = triple.subjectLemmaGloss();
                    System.out.println("subject : "+sentenceSubject);
                    if(sentenceSubject.toLowerCase().indexOf(subjectList.get(0).toLowerCase()) != -1){
                        System.out.println("true : + "+ subjectList.get(0));
                    }
                }

                System.out.println("Line break");
                System.out.println("");


            }
        }

    }
}
