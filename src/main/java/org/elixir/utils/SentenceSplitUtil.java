package org.elixir.utils;

import org.elixir.controllers.SentencesController;
import org.elixir.models.Case;
import org.elixir.models.Sentence;

import java.io.*;
import java.util.ArrayList;

public class SentenceSplitUtil {

    public static ArrayList<String> prevSentenceRegexPatterns = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        addCitationRegex();
        String path = "/home/viraj/Desktop/sentences/";

        BufferedWriter br1 = null;
        BufferedWriter br2 = null;
        addPartOfPrevSentence();

        int[] caseNumbers = {11,12,13,14,15,16,17,18,19};
        ArrayList<Case> cases = new ArrayList<>();
        for (int n : caseNumbers) {
            Case aCase = new Case();
            aCase.setSentences(SentencesController.getSentencesOfCase(n));
            cases.add(aCase);
        }

        // sample usage
        int case_count = 11;
        for (Case _case : cases) {
            br1 = new BufferedWriter(new FileWriter(new File(path + "case_"+ Integer.valueOf(case_count))+".txt"));
            br2 = new BufferedWriter(new FileWriter(new File(path + "case_wrong_"+ Integer.valueOf(case_count))+".txt"));

            case_count += 1;
            int count = 1;

            for(Sentence sentence: _case.getSentences()){
                //br.write(Integer.valueOf(count) + ". " + sentence.getSentence()+"\n");
                if(!partOfPrevSentence(sentence.getSentence())){
                    System.out.println(Integer.valueOf(count) + ". " + sentence.getSentence()+"\n");
                    br1.write(Integer.valueOf(count) + ". " + sentence.getSentence()+"\n\n");
                    br1.flush();
                }
                else{
                    br2.write(Integer.valueOf(count) + ". " + sentence.getSentence()+"\n\n");
                    br2.flush();
                }
                count += 1;

                //br.flush();
            }
            br1.close();
            br2.close();
        }


    }

    public static boolean partOfPrevSentence (String text){

        for(String regex : prevSentenceRegexPatterns){
            if(text.matches(regex)){
                return true;
            }
        }

        return false;

    }

    public static void addPartOfPrevSentence (){
        prevSentenceRegexPatterns.add("^See.*");
        prevSentenceRegexPatterns.add("^Ibid.*");
        prevSentenceRegexPatterns.add("^Id.*");
        prevSentenceRegexPatterns.add("^Pp..*");
        prevSentenceRegexPatterns.add("^(F|f)(ootnote|OOTNOTE).*");
        prevSentenceRegexPatterns.add("(A|a)(ppx|PPX).*");
        prevSentenceRegexPatterns.add("(A|a)nte.*");
        prevSentenceRegexPatterns.add("(\\S+\\s){0,3}\\S+");
        prevSentenceRegexPatterns.add("^[0-9].*");

        prevSentenceRegexPatterns.add("^§.*(\\w+\\s){0,4}.*$");
        prevSentenceRegexPatterns.add("^\\w+\\sv\\.\\s\\w+.*");

        //only suits for one bracket
        prevSentenceRegexPatterns.add("^.*\\(((?!(\\))).)*$");
        prevSentenceRegexPatterns.add("^((?!(\\()).)*\\).*$");
        prevSentenceRegexPatterns.add("^Argued:.*");
        prevSentenceRegexPatterns.add("^((?!(([A-Za-z]+\\s){4})).)*$");



    }

    public static void addCitationRegex () {
        String regex = "^\\w+\\sv\\.\\s\\w+.*(([A-Za-z]+\\s){4}).*$";
        String text = "Medina v. California, 505 U. S. 437, controls dfd sdfd sdf hhh ggh";
        if(text.matches(regex)){
            System.out.println(true);
        }
        else{
            System.out.println(false);
        }
    }

}
