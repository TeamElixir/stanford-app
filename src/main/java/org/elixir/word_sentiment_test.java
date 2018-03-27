package org.elixir;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.elixir.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class word_sentiment_test {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,depparse,natlog,ner,sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        String filePath = new File("").getAbsolutePath();
        filePath += "/src/main/resources/case2.txt";
        String textRaw = Utils.readFile(filePath);

        String[] splittedParagraphs = textRaw.split("\n");

        for(String splittedParagraph:splittedParagraphs){
            String text = splittedParagraph;
            Annotation document = new Annotation(text);
            pipeline.annotate(document);
            List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
            for (CoreMap sentence : sentences) {
                System.out.println(sentence.get(CoreAnnotations.TextAnnotation.class));
                // traversing the words in the current sentence
                // a CoreLabel is a CoreMap with additional token-specific methods
                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    // this is the text of the token
                    String word = token.get(CoreAnnotations.TextAnnotation.class);
                    // this is the POS tag of the token
                    String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                    // this is the NER label of the token
                    String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);

                    String sentiment = token.get(SentimentCoreAnnotations.SentimentClass.class);
                    System.out.println(word + " " + pos + " " + ne+"  "+sentiment);

                }
            }

        }
    }

    public static TreeSet<String> createDictionary(){

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);


        String filePath = new File("").getAbsolutePath();
        filePath += "/src/main/resources/case2.txt";
        File file = new File(filePath);
        TreeSet<String> words = new TreeSet<>();

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String word = scanner.next();
                if(word.charAt(0) != word.toLowerCase().charAt(0)){
                    Annotation annotation = new Annotation(word);
                    pipeline.annotate(annotation);
                    List<CoreMap> list = annotation.get(CoreAnnotations.SentencesAnnotation.class);
                    String tokenLemma = list
                            .get(0).get(CoreAnnotations.TokensAnnotation.class)
                            .get(0).get(CoreAnnotations.LemmaAnnotation.class);
                    words.add(tokenLemma);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            return words;
        }
    }
}
