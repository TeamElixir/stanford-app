package org.elixir.utils;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.*;

//to resolve coreferences of the given paragraph
public class CorefChainMapping {
    static class CorefSentenceMapping{
        int sentenceNumber;
        String term;

    }

    public ArrayList<CorefSentenceMapping> termList = new ArrayList<>();
    public String longestTerm="--";
    public String non_prep_words = ";";
    public TreeSet<String> referredTermList = new TreeSet<>();

    public void assignTerm(String term, int sentenceNumber){

        CorefSentenceMapping csm = new CorefSentenceMapping();
        csm.term = term;
        csm.sentenceNumber = sentenceNumber;
        termList.add(csm);

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation ann = new Annotation(term);
        pipeline.annotate(ann);

        String postag = ann.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class);
        //String word = ann.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.TextAnnotation.class);


        if(termList.size() == 0){
            longestTerm = term;
        }
        if(!postag.equals("PRP") && !postag.equals("PRP$")){
            if(term.length() >= longestTerm.length()){
                longestTerm = term;
            }

            referredTermList.add(term);
        }
    }

    //created for this class
    private String[] specialSplit(String text){
        String[] intermediateSplit = text.split(",");

        String concatString = "";
        for(String i : intermediateSplit){
            if(!i.matches(".*[0-9]$")){
                concatString += i.trim() + ", ";
            }else{
                concatString += i.trim() + ";;";
            }
        }
        return concatString.split(";;");
    }

    //needs to process this kind a string
    //CHAIN50-["the defendant" in sentence 1, "his" in sentence 1, "the defendant" in sentence 5, "he" in sentence 5]
    public void processCorefChainString(String corefChainString){

        String BracketsRemovedString = corefChainString.substring(corefChainString.indexOf("[")+1,corefChainString.indexOf("]"));
        String[] commaSeperatedReferences = specialSplit(BracketsRemovedString);

        for(String reference : commaSeperatedReferences){
            int sentenceNumber = Integer.parseInt(reference.substring(reference.indexOf("in sentence")+12));
            String term = reference.substring(1,reference.indexOf("in sentence")-2);
            assignTerm(term,sentenceNumber);
        }
    }

    //resolve the terms
    public String resolveTerm(String givenTerm, int sentence_id){

        String[] partialTerms = givenTerm.split(" ");

        for(CorefSentenceMapping i : termList){
            if(i.sentenceNumber == sentence_id && Arrays.asList(partialTerms).contains(i.term)){
                String listString = "";

                for (String s : referredTermList)
                {
                    listString += s + ";";
                }
                if(listString.length()==0){
                    return longestTerm;
                }
                return  listString;
            }
        }
        return null;
    }
}
