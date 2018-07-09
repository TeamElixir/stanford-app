package org.elixir.parseTree;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.*;
import java.util.*;

public class ShufflePhrases {
    public static void main(String[] args) throws IOException {
        BufferedWriter bf = new BufferedWriter(new FileWriter(new File("/home/viraj/Desktop/PhraseTestDataSet.csv")));

        String filename = "/home/viraj/Desktop/phrases-completed-for-10-cases.csv";
        Scanner scanner = new Scanner(new File(filename));
        TreeSet<String> phrasesSet = new TreeSet<>();
        ArrayList<String> testDataList = new ArrayList<>();
        Random rand = new Random();

        String regex = "\",\"";

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos, lemma,ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);



        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] wordlist = line.split(regex);
            String phrase = wordlist[1].trim();
            String sentiment = wordlist[2].toLowerCase();

            Annotation ann = new Annotation(phrase);
            pipeline.annotate(ann);

            if(!phrasesSet.contains(phrase) && isUsefulPhrase(phrase,ann)){
                phrasesSet.add(phrase);
                bf.write(line);
                bf.newLine();
                bf.flush();
//                if(sentiment.equals("negative")){
//                    if(rand.nextInt(5)==rand.nextInt(5)){
//                        testDataList.add(line);
//                    }
//                }else{
//                    if(rand.nextInt(10)==rand.nextInt(10)){
//                        testDataList.add(line);
//                    }
//                }
            }
        }

        //Collections.shuffle(testDataList);

            bf.close();


    }

    public static boolean isUsefulPhrase(String text, Annotation ann){
        if(text.length()==2 && (text.matches("^the ") || text.contains("^a ") || text.matches("^an "))){
            return false;
        }
        if(text.equals(text.toUpperCase())){
            return false;
        }
        if(nerOnlyPhrase(text,ann)){
            return false;
        }

        return true;
    }

    public static boolean nerOnlyPhrase(String text, Annotation ann){
        List<CoreMap> sentences = ann.get(CoreAnnotations.SentencesAnnotation.class);

        //String[] posList = {"SYM","CC","CD","DT"};
        ArrayList<String> posList = new ArrayList<>();
        posList.add("SYM");
        posList.add("CC");
        posList.add("CD");
        posList.add("DT");
        Boolean flag = false;

        for( CoreMap sentence : sentences){
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String ner = token.ner();
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                if(ner.length()!=0 || posList.contains(pos) ){
                    flag = true;
                    continue;
                }
                else{
                    return false;
                }
            }
        }
        return flag;
    }


}

