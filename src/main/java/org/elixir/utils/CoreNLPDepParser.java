package org.elixir.utils;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
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

    //to find first that related verb.
    public static String depParseForGivenRelation(String dependency, String outerVerb, String text){
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
            if(typedDependency.reln().equals(dependency) && typedDependency.dep().equals(outerVerb)){
                return typedDependency.gov().toString();
            }
        }
        return null;

    }

    /*
    first element of array is the ccomp verb in inner sentence, second element is index of the relevant that
     */
    public static ArrayList<int[]> findIndicesOfOuterVerbAndInnerVerb(Annotation ann, String text){

        ArrayList<TypedDependency> ccompList = new ArrayList<>();
        ArrayList<TypedDependency> thatDependencyList = new ArrayList<>();
        ArrayList<Integer> occurancesOfThat = new ArrayList<>();

        ArrayList<int[]> startIndices = new ArrayList<>();

        for (CoreMap sent : ann.get(CoreAnnotations.SentencesAnnotation.class)) {

            SemanticGraph sg = sent.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);

            for(TypedDependency typedDependency : sg.typedDependencies()){

                if(typedDependency.dep().originalText().equals("that")){
                    if (!occurancesOfThat.contains(typedDependency.dep().index())){
                        occurancesOfThat.add(typedDependency.dep().index());
                    }

                }else if(typedDependency.gov().originalText().equals("that")){
                    if(!occurancesOfThat.contains(typedDependency.gov().index())){
                        occurancesOfThat.add(typedDependency.gov().index());
                    }
                }

                if(typedDependency.reln().toString().equals("ccomp")){
                    ccompList.add(typedDependency);
                }
                else if(typedDependency.reln().toString().equals("mark") && typedDependency.dep().originalText().equals("that")){
                    thatDependencyList.add(typedDependency);
                }
            }

            Collections.sort(occurancesOfThat);

            for(TypedDependency ccompDependency : ccompList){
                for(TypedDependency thatDependency : thatDependencyList){
                    if(ccompDependency.gov().index()+1 == thatDependency.dep().index()){
                        int[] array = {text.indexOf(ccompDependency.dep().originalText()),
                                findIthOccuranceOfWord("that", text, occurancesOfThat.indexOf(thatDependency.dep().index())+1)};
                        startIndices.add(array);
                    }
                }
            }
        }
        return startIndices;
    }

    //can find ith occurance of a certain word in a sentence : working
    public static int findIthOccuranceOfWord(String word, String sentence, int occurance){
        if(occurance <= 0){
            return -1;
        }
        else if(occurance == 1){
            return sentence.indexOf(word);
        }
        else{
            return sentence.indexOf(word,findIthOccuranceOfWord(word,sentence,occurance-1)+1);
        }
    }


    public static IndexedWord findRelatedGovWordForGivenWord(Annotation ann, String relation, String otherWord){
        for (CoreMap sent : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
            SemanticGraph sg = sent.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);

            for(TypedDependency td : sg.typedDependencies()){
                if(td.reln().toString().equals(relation) && td.dep().originalText().equals(otherWord)){
                    return td.gov();
                }
            }
        }

        return null;
    }

    public static IndexedWord findRelatedDepWordForGivenWord(Annotation ann, String relation, String otherWord){
        for (CoreMap sent : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
            SemanticGraph sg = sent.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);

            for(TypedDependency td : sg.typedDependencies()){
                if(td.reln().toString().equals(relation) && td.gov().originalText().equals(otherWord)){
                    return td.dep();
                }
            }
        }
        return null;
    }

    public static String findSubjectContext(Annotation ann, IndexedWord subject){
        ArrayList<IndexedWord> arrayList = new ArrayList<>();

        String[] depRelations = {"amod", "nmod:poss"};
        String[] govRelations = {"nmod:poss","amod"};

        for(String i : depRelations){
            IndexedWord temp = findRelatedDepWordForGivenWord(ann,i, subject.originalText());
            if(temp != null){
                arrayList.add(temp);
            }
        }

        for (String j : govRelations){
            IndexedWord temp = findRelatedGovWordForGivenWord(ann, j,subject.originalText());
            if(temp != null){
                arrayList.add(temp);
            }
        }
        arrayList.add(subject);

        arrayList.sort((a,b) ->(b.index()<a.index() ? 1 : 0));

        String subjectContext ="";

        for(IndexedWord i : arrayList){
            subjectContext += (i.originalText()+" ");
        }
        return subjectContext;
    }


    public static String findVerbContext(Annotation ann, IndexedWord verb){
        ArrayList<IndexedWord> arrayList = new ArrayList<>();

        String[] govRelations = {"aux","neg"};

        for (String j : govRelations){
            IndexedWord temp = findRelatedDepWordForGivenWord(ann, j,verb.originalText());
            if(temp != null){
                arrayList.add(temp);
            }
        }
        arrayList.add(verb);
        Collections.sort(arrayList);

        String verbContext ="";

        for(IndexedWord i : arrayList){
            verbContext += (i.originalText()+" ");
        }
        return verbContext;
    }


}
