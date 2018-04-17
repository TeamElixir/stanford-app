package org.elixir;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

public class RoleClassifier {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,natlog,sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        //update the two arraylists in SentimentGradientAndCost class

        /*
        todo
        for each case from case_11 to case_20
        fetch sentence one after another
        needs to get pos tag of each word in the sentence
        for each triple in sentence
            1. identify the sentences with negative sentiment in relation part only
            2. identify the sentences with negative sentiment in complete sentence
        */

    }
}
