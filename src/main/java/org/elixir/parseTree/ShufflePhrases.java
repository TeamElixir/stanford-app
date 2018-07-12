package org.elixir.parseTree;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import javax.swing.filechooser.FileSystemView;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ShufflePhrases {
    public static void main(String[] args) throws IOException {

        String sourceFilePath = ClassLoader.getSystemClassLoader().getResource("SentimentAnalysis/Performance_test_sentiment/PhraseTestDataSet_leftover.csv").getPath();
        String homeDir = FileSystemView.getFileSystemView().getHomeDirectory().getPath();

        String targetFilePath = homeDir + "/PhraseTestDataSet_clean_3.csv";

        BufferedWriter bf = new BufferedWriter(new FileWriter(new File(targetFilePath)));

        Scanner scanner = new Scanner(new File(sourceFilePath));
        TreeSet<String> phrasesSet = new TreeSet<>();
        ArrayList<String> testDataList = new ArrayList<>();
        Random rand = new Random();

        String regex = "\",\"";

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos, lemma,ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);


        System.out.println(isUsefulPhrase("a car", null));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] wordlist = line.split(regex);
            String phrase = wordlist[1].trim();
            String sentiment = wordlist[2].toLowerCase();

            Annotation ann = new Annotation(phrase);
            pipeline.annotate(ann);

            if (!phrasesSet.contains(phrase) && isUsefulPhrase(phrase, ann)) {
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

    public static boolean isUsefulPhrase(String text, Annotation ann) {
        if (text.split(" ").length < 3 && (text.matches("^the\\s.*") || text.matches("^a\\s.*") || text.matches("^an\\s.*") || text.matches(".*\'s$"))) {

            //System.out.println(text);
            return false;
        }
        if (text.equals(text.toUpperCase())) {

            return false;
        }
        if (nerOnlyPhrase(text, ann)) {
            System.out.println(text);
            return false;
        }

        return true;
    }

    public static boolean nerOnlyPhrase(String text, Annotation ann) {
        List<CoreMap> sentences = ann.get(CoreAnnotations.SentencesAnnotation.class);

        //String[] posList = {"SYM","CC","CD","DT"};
        ArrayList<String> posList = new ArrayList<>();
        posList.add("SYM");
        posList.add("CC");
        posList.add("CD");
        posList.add("DT");

        ArrayList<String> nerList = new ArrayList<>();
        nerList.add("EMAIL");
        nerList.add("URL");
        nerList.add("CITY");
        nerList.add("STATE_OR_PROVINCE");
        nerList.add("COUNTRY");
        nerList.add("NATIONALITY");
        nerList.add("RELIGION");
        nerList.add("TITLE");

        Boolean flag = false;

        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String ner = token.ner();
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                if (nerList.contains(ner) || posList.contains(pos)) {
                    flag = true;
                    continue;
                } else {
                    return false;
                }
            }
        }
        return flag;
    }


}

