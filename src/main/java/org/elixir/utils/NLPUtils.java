package org.elixir.utils;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Quadruple;

import java.util.*;

public class NLPUtils {
    public String getSubject(String input) {
        // TODO: remove 'that' first
        Sentence sent = new Sentence(input);
        Collection<Quadruple<String, String, String, Double>> openie = sent.openie();
        String subject = "";
        for (Quadruple<String, String, String, Double> quadruple : openie) {
            subject = quadruple.first;
        }

        return subject;
    }


    public static ArrayList getPersonList(CoreMap sentence) {

        ArrayList<String> personArray = new ArrayList<>();

        for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
            String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            if ("PERSON".equals(ne)) {
                personArray.add(token.get(CoreAnnotations.TextAnnotation.class));
            }
        }

        return personArray;
    }

    /**
     * @param sentence sentence must be annotated with parse and sentiment
     * @return true for Positive and false for Negative
     */
    public static boolean getSentiment(CoreMap sentence) {

        String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

        return "Positive".equals(sentiment);
    }

    public static ArrayList<String> getCoref(String inputText) {
        ArrayList<String> corefs = new ArrayList<>();

        Annotation document = new Annotation(inputText);
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        pipeline.annotate(document);
        System.out.println("---");
        System.out.println("coref chains");
        for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
            System.out.println("cc: " + cc);
            List<CorefChain.CorefMention> mentionsInTextualOrder = cc.getMentionsInTextualOrder();
            System.out.println("Foreach in mentionsInTextualOrder");
            for (CorefChain.CorefMention mention : mentionsInTextualOrder) {
                System.out.println(mention);
            }
        }
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            System.out.println("---");
            System.out.println("mentions");
            for (Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
                System.out.println("mention: " + m);
                System.out.println("sentNum: " + m.sentNum);
//                System.out.println("mentionSpan: " + m.);
                System.out.println();
            }
        }

        return corefs;
    }

    /**
     * @param annotation     annotated string - targetSentence + " " + sourceSentence
     * @param sourceSentence
     * @param targetSentence
     * @return arraylist containing two changed sentences sourceSentence-0 , targetSentence-1
     */
    public ArrayList<String> replaceCoreferences(Annotation annotation, String sourceSentence, String targetSentence) {

        for (CorefChain chain : annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
            String represent = chain.getRepresentativeMention().mentionSpan;
            represent = represent.replaceAll("\\$", "&");

            for (CorefChain.CorefMention mention : chain.getMentionsInTextualOrder()) {
                int sentNo = mention.sentNum;
                int startIndex = mention.startIndex;
                int endIndex = mention.endIndex;
                String word = mention.mentionSpan;

                if (sentNo == 1) {
                    targetSentence = targetSentence.replaceAll(word, represent);
                } else if (sentNo == 2) {
                    sourceSentence = sourceSentence.replaceAll(word, represent);
                }
            }
        }

        return new ArrayList<>(Arrays.asList(sourceSentence, targetSentence));
    }
}
