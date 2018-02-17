package org.elixir.utils;

import java.util.ArrayList;

/*
*purpose of this class is to extract caselaw related statements with its citation
 */
public class CaseLawStatementExtractor {

    //public String prevSentence = "";
    public static ArrayList<String> caseList = new ArrayList<>();
    public static String prevCase = "";

    //todo
    //extract all caseList where multiple citations are in the same sentence
    //The cited case when citation is given as Id.
    //solve if the petitoners, defendant name is not a single word


    public static ArrayList<String> extractUsingDirectCitation(String sentence) {
        String[] words = sentence.split(" ");
        ArrayList<String> outputCitation = new ArrayList<>();

        for (int i = 1; i < words.length - 1; i++) {
            if (words[i].equals("v.")) {
                caseList.add(new String(words[i - 1].replaceAll("[^a-zA-Z]", "")));
                caseList.add(new String(words[i + 1].replaceAll("[^a-zA-Z]", "")));
                if (sentence.toLowerCase().contains("See")) {
                    outputCitation.add("see");
                }else if(i==1){
                    outputCitation.add("prev");
                }
                else {
                    outputCitation.add("true");
                }
                outputCitation.add(words[i-1]+" v. "+words[i+1]);
                for (int j=i+1; j<words.length - 1; j++){
                    if(words[j].equals("v.")){
                        caseList.add(new String(words[j - 1].replaceAll("[^a-zA-Z]", "")));
                        caseList.add(new String(words[j + 1].replaceAll("[^a-zA-Z]", "")));
                        outputCitation.add(words[j-1]+" v. "+words[j+1]);
                    }
                }
                prevCase = outputCitation.get(outputCitation.size()-1);
                return outputCitation;
            } else if (words[i - 1].matches("[0-9]+") && words[i].equals("U.") && words[i + 1].equals("S.")) {
                outputCitation.add("U.S. code "+ words[i-1]);
                return outputCitation;
            }
        }
        outputCitation.add("false");
        return null;
    }

    //todo
    //assumed that if a case is referred by a single name, it should be the first name in the case citation A v. B :- A likewise
    public static ArrayList<String> extractUsingIndirectReference(String sentence){
        String[] words = sentence.split(" ");
        ArrayList<String> outputCitation = new ArrayList<>();
        if(!sentence.contains("v.")){
            for (int j = 0; j < words.length; j++) {
                String newString = words[j].replaceAll("[^a-zA-Z]", "");
                if (caseList.contains(newString)) {
                    outputCitation.add("true");
                    outputCitation.add(newString + " v. " + caseList.get(caseList.indexOf(newString)+1));
                    return outputCitation;

                } else if (words[j].toLowerCase().contains("id.")) {
                    outputCitation.add("true");
                    outputCitation.add(prevCase);
                    return outputCitation;
                }
            }
        }
        outputCitation.add("false");
        return null;
    }

}
