package org.elixir.parseTree;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Shuffle {
    public static void main(String[] args) throws IOException {
        String sourceFilePath = ClassLoader.getSystemClassLoader().getResource("SentimentAnalysis/Performance_test_sentiment/PhraseTestDataSet_clean_3.csv").getPath();
        String homeDir = FileSystemView.getFileSystemView().getHomeDirectory().getPath();

        String targetFilePath = homeDir + "/PhraseTestDataSet_1000.csv";

        Scanner sc = new Scanner(new File(sourceFilePath));

        ArrayList<String> sentenceList = new ArrayList();
        ArrayList<String> selectedList = new ArrayList();

        while(sc.hasNextLine()){
            sentenceList.add(sc.nextLine());
        }

        Collections.shuffle(sentenceList);

        int negativeCount = 0;
        int nonNegativeCount = 0;

        String regex = "\",\"";

        for(String sentence: sentenceList){
            String[] words = sentence.split(regex);
            String sentiment = words[2].toLowerCase();

            if(sentiment.equals("negative") && negativeCount<500){
                selectedList.add(sentence);
                negativeCount++;
            }else  if(sentiment.equals("negative") && negativeCount>=500){
                break;
            }
        }

        for(String sentence: sentenceList){
            String[] words = sentence.split(regex);
            String sentiment = words[2].toLowerCase();

            if(!sentiment.equals("negative") && nonNegativeCount<500){
                selectedList.add(sentence);
                nonNegativeCount++;
            }else  if(!sentiment.equals("negative") && nonNegativeCount>=500){
                break;
            }
        }

        Collections.shuffle(selectedList);

        BufferedWriter bf = new BufferedWriter(new FileWriter(new File(targetFilePath)));

        for(String item : selectedList){
            bf.write(item);
            bf.newLine();
            bf.flush();
        }
        bf.close();

    }
}
