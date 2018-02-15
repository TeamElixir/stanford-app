package org.elixir.utils;

import java.util.ArrayList;

/*
*purpose of this class is to extract caselaw related statements with its citation
 */
public class CaseLawStatementExtractor {

    //public String prevSentence = "";
    public ArrayList<String> caseList = new ArrayList<>();
    public String prevCase = "";

    //todo
    //extract all caseList where multiple citations are in the same sentence
    //The cited case when citation is given as Id.
    //solve if the petitoners, defendant name is not a single word
    private String[] extractUsingDirectCitation(String sentence) {
        String[] words = sentence.split(" ");
        String[] outputArray = new String[2];
        for (int i = 1; i < words.length - 1; i++) {
            if (words[i].equals("v.")) {
                caseList.add(new String(words[i - 1].replaceAll("[^a-zA-Z]", "")));
                caseList.add(new String(words[i + 1].replaceAll("[^a-zA-Z]", "")));
                outputArray[1] = words[i-1]+" v. "+words[i+1];
                if (sentence.contains("See") || sentence.contains("see")) {
                    outputArray[0] = "see";
                } else {
                    outputArray[0] = "true";
                }
                prevCase = outputArray[1];
                return outputArray;
            } else if (words[i - 1].matches("[0-9]+") && words[i].equals("U.") && words[i + 1].equals("S.")) {
                outputArray[0] = "true";
                outputArray[1] = "code";
                return outputArray;
            }
        }
        outputArray[0] = "false";
        return outputArray;
    }

    //todo
    //assumed that when cited using a single word, always petitioners one is considered
    public String[] extractUsingIndirectReference(String sentence){
        String[] words = sentence.split(" ");
        String[] outputArray = new String[2];
        for (int j = 0; j < words.length; j++) {
            String newString = words[j].replaceAll("[^a-zA-Z]", "");
            if (caseList.contains(newString)) {
                outputArray[0] = "true";
                outputArray[1] = newString + " v. " + caseList.get(caseList.indexOf(newString)+1);
                return outputArray;

            } else if (words[j].toLowerCase().contains("id.")) {
                outputArray[0] = "true";
                outputArray[1] = prevCase;
                return outputArray;
            }
        }
        outputArray[0] = "false";
        return outputArray;
    }

}
