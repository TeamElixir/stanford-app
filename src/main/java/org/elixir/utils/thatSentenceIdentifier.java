package org.elixir.utils;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;
import java.util.Properties;

// to identify the structure : that followed by verb
public class thatSentenceIdentifier {
    public static boolean checkVerbThatStructure(String sentence) {

        //to check occurance of verb
        boolean verbOccured = false;

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation ann = new Annotation(sentence);
        pipeline.annotate(ann);

        List<CoreMap> coreMapSentences = ann.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap coreMapSentence : coreMapSentences) {
            System.out.println(coreMapSentence.get(CoreAnnotations.TextAnnotation.class));
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : coreMapSentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

                if(pos.equals("VB") ||
                        pos.equals("VBD") ||
                        pos.equals("VBG") ||
                        pos.equals("VBN") ||
                        pos.equals("VBP") ||
                        pos.equals("VBZ")){
                    verbOccured = true;
                    continue;
                }else{
                    if(verbOccured == true && word.toLowerCase().equals("that")){
                        return true;
                    }else{
                        verbOccured = false;
                    }
                }

            }
        }
        return false;
    }
}
