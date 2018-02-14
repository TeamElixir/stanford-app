package org.elixir.utils;

import java.util.ArrayList;

/*
*purpose of this class is to extract caselaw related statements with its citation
 */
public class CaseLawStatementExtractor {

    public String prevSentence = "";
    public ArrayList<String> caseList = new ArrayList<>();

    //todo
    //extract all caseList where multiple citations are in the same sentence
    //The cited case when citation is given as Id.
    private String extractFromCurrentSentence(String sentence) {
        String[] words = sentence.split(" ");
        for (int i = 1; i < words.length - 1; i++) {

            if (words[i].equals("v.")) {
                if (sentence.contains("See") || sentence.contains("see")) {
                    return prevSentence;
                } else {
                    caseList.add(new String(words[i - 1].replaceAll("[^a-zA-Z]", "")));
                    caseList.add(new String(words[i + 1].replaceAll("[^a-zA-Z]", "")));
                    return sentence;
                }
            } else if (words[i - 1].matches("[0-9]+") && words[i].equals("U.") && words[i + 1].equals("S.")) {
                return sentence;
            }
        }
        for (int j = 0; j < words.length; j++) {
            String newString = words[j].replaceAll("[^a-zA-Z]", "");
            if (caseList.contains(newString)) {
                return sentence;

            } else if (words[j].toLowerCase().contains("id.")) {
                return sentence;
            }
        }
        return null;
    }

    public String extractCaseLawStatement(String sentence){
        String extractedSentence = extractFromCurrentSentence(sentence);
        prevSentence = sentence;
        return extractedSentence;
    }
}
