package org.elixir.utils;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

import java.io.StringReader;
import java.util.List;

public class CoreNLPDepParser {

    public static String depParse(String text){
        String modelPath = DependencyParser.DEFAULT_MODEL;
        String taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";

        MaxentTagger tagger = new MaxentTagger(taggerPath);
        DependencyParser parser = DependencyParser.loadFromModelFile(modelPath);

        DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
        GrammaticalStructure gs =  null;
        for (List<HasWord> sentence : tokenizer) {
            List<TaggedWord> tagged = tagger.tagSentence(sentence);
            gs = parser.predict(tagged);

        }

        for(TypedDependency typedDependency : gs.typedDependenciesCCprocessed()){
            System.out.println(typedDependency.dep() +" : " + typedDependency.gov() + " : " +typedDependency.reln());
        }
        return "finished - testing ";
    }
}
