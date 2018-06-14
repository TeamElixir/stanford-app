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

    //depparse old method : not using currently
    public static String depParse(String text){

        //establishing the dependency parser utility
        String modelPath = DependencyParser.DEFAULT_MODEL;
        String taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
        MaxentTagger tagger = new MaxentTagger(taggerPath);
        DependencyParser parser = DependencyParser.loadFromModelFile(modelPath);

        //tokenize
        DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
        GrammaticalStructure gs =  null;
        for (List<HasWord> sentence : tokenizer) {
            List<TaggedWord> tagged = tagger.tagSentence(sentence);
            gs = parser.predict(tagged);

        }

        //print all dependency edges
        for(TypedDependency typedDependency : gs.typedDependenciesCCprocessed()){
            System.out.println(typedDependency.dep() +" : " + typedDependency.gov() + " : " +typedDependency.reln());
        }
        return "finished - testing ";
    }

    //to find first that related verb. : not used currently
    //todo : debug the thing using sentence by sentence
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
    //todo: debug -> output contains errors for certain inputs
     */
    public static ArrayList<IndexedWord[]> findIndicesOfOuterVerbAndInnerVerb(Annotation ann, String text){

        ArrayList<TypedDependency> ccompList = new ArrayList<>();
        ArrayList<TypedDependency> thatDependencyList = new ArrayList<>();
        ArrayList<Integer> occurancesOfThat = new ArrayList<>();

        ArrayList<IndexedWord[]> startIndices = new ArrayList<>();

        for (CoreMap sent : ann.get(CoreAnnotations.SentencesAnnotation.class)) {

            SemanticGraph sg = sent.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);

            for(TypedDependency typedDependency : sg.typedDependencies()){

                //to keep index of "that" word, if as the dep
                if(typedDependency.dep().originalText().equals("that")){
                    //if it already contains the relevant index , do not add
                    if (!occurancesOfThat.contains(typedDependency.dep().index())){
                        occurancesOfThat.add(typedDependency.dep().index());
                    }

                    //"that" index, as the gov
                }else if(typedDependency.gov().originalText().equals("that")){
                    if(!occurancesOfThat.contains(typedDependency.gov().index())){
                        occurancesOfThat.add(typedDependency.gov().index());
                    }
                }

                //inner outer sentence match is done by ccomp and mark
                //to keep "ccomp" typed dependency
                if(typedDependency.reln().toString().equals("ccomp")){
                    ccompList.add(typedDependency);
                }
                //to keep "mark" typedDependency
                else if(typedDependency.reln().toString().equals("mark") && typedDependency.dep().originalText().equals("that")){
                    thatDependencyList.add(typedDependency);
                }
            }

            Collections.sort(occurancesOfThat);

            //check if "mark" and "ccomp" synced. if add to the array.
            for(TypedDependency ccompDependency : ccompList){
                for(TypedDependency thatDependency : thatDependencyList){
                    if((ccompDependency.gov().index()+1 == thatDependency.dep().index()) && (!ccompDependency.gov().tag().equals("NN"))){
                        IndexedWord[] array = {ccompDependency.dep(),
                                thatDependency.dep()};
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


    public static IndexedWord findRelatedGovWordForGivenWord(Annotation ann, String relation, IndexedWord otherWord){
        for (CoreMap sent : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
            SemanticGraph sg = sent.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);

            for(TypedDependency td : sg.typedDependencies()){
                if(td.reln().toString().equals(relation) && td.dep().equals(otherWord)){
                    return td.gov();
                }
            }
        }

        return null;
    }

    public static IndexedWord findRelatedDepWordForGivenWord(Annotation ann, String relation, IndexedWord otherWord){
        for (CoreMap sent : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
            SemanticGraph sg = sent.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);

            for(TypedDependency td : sg.typedDependencies()){
                if(td.reln().toString().equals(relation) && td.gov().equals(otherWord)){
                    return td.dep();
                }
            }
        }
        return null;
    }

    //find
    public static IndexedWord findSubjectForGivenVerb(Annotation ann, IndexedWord verb){
        IndexedWord subject = findRelatedDepWordForGivenWord(ann,"nsubj", verb);

        if(subject != null){
            return subject;
        }

        subject = findRelatedDepWordForGivenWord(ann,"nsubjpass", verb);

        if(subject != null){
            return subject;
        }

        IndexedWord xcompGov = findRelatedGovWordForGivenWord(ann,"xcomp",verb);
        if(xcompGov != null){
            if(findSubjectForGivenVerb(ann,xcompGov) != null){
                return findSubjectForGivenVerb(ann,xcompGov);
            }
        }

        IndexedWord advclGov = findRelatedGovWordForGivenWord(ann,"advcl",verb);
        if(xcompGov != null){
            if(findSubjectForGivenVerb(ann,advclGov) != null){
                return findSubjectForGivenVerb(ann,advclGov);
            }
        }
        return null;
    }

    public static String findSubjectContext(Annotation ann, IndexedWord subject){
        ArrayList<IndexedWord> arrayList = new ArrayList<>();

        String[] depRelations = {"amod", "nmod:poss", "pos"};
        String[] govRelations = {"nmod:poss","amod", "pos"};

        for(String i : depRelations){
            IndexedWord temp = findRelatedDepWordForGivenWord(ann, i, subject);
            if(temp != null){
                arrayList.add(temp);
            }
        }

        for (String j : govRelations){
            IndexedWord temp = findRelatedGovWordForGivenWord(ann, j, subject);
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

        String[] govRelations = {"aux","neg", "auxpass"};

        for (String j : govRelations){

            IndexedWord temp = findRelatedDepWordForGivenWord(ann, j, verb);
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
