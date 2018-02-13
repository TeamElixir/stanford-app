package org.elixir;

import org.elixir.models.Node;

import java.util.*;
import java.lang.*;
import java.util.regex.*;

public class NodeRelationsDeterminer {

    private static ArrayList<String> regexMatches = new ArrayList<>();

    public static ArrayList<Node> nodesRelations;
    public static boolean regexResult = false;


    static final String[] testcases = new String[]{
            "A 'reasonable probability' of a different result\" is one in which the suppressed evidence \"" +
                    " 'undermines confidence in the outcome of the trial.' \""
    };

    private static final ArrayList<String> transitionWords = new ArrayList<>(
            Arrays.asList("To make that"));

    static String previousSentence = null;
            //"Such \"evidence is 'material' . . . when there is a reasonable probability  had the evidence been disclosed, the result of the proceeding would have been different.\"";

    static String previousArgumentId=null;

    public static void main(String[] args) {




    }

    public static void initialize(){
        nodesRelations=ArgumentTreeGenerator.nodes;
    }



    public static void checkRelationships(){
        for (int i=0;i<nodesRelations.size();i++) {
            // System.out.print("Input: "+arg+" -> Matches: ");
            Node node = nodesRelations.get(i);
            String arg = node.getArgument();
            if(node.getId().length()>2) {
                previousSentence = nodesRelations.get(i-1).getArgument();
                previousArgumentId=nodesRelations.get(i-1).getId();
                regexResult = checkElaborationRegex(arg,node);
                if(!regexResult){
                    checkTransitionRelation(arg,node);
                }


                System.out.println(regexMatches.toString());
                System.out.println(regexResult);
                System.out.println(node.getId().length());
            }

            regexMatches.clear();

        }

    }

    public static boolean checkElaborationRegex(String argument,Node node) {
        Pattern p = Pattern.compile("(?:^|\\s)'([^']*?)'(?:$|\\s)", Pattern.MULTILINE);
        Matcher m = p.matcher(argument);
        if (m.find()) {
            regexMatches.add(m.group());
            while (m.find()) {
                //System.out.print(", " + m.group());
                //System.out.println();
                regexMatches.add(m.group());
            }
            for (String regexMatch:regexMatches){

                String regexMatch1 = regexMatch.replaceAll("(')", "");

                if(previousSentence.toLowerCase().indexOf(regexMatch1.toLowerCase()) != -1){
                    node.setParent(previousArgumentId);
                    System.out.println("found : "+regexMatch1);
                    return true;
                }

            }

        }



        System.out.println("NONE");



        return false;

    }

    public static void checkTransitionRelation(String argument, Node node){

        for(int i=0;i<transitionWords.size();i++){
            if(argument.toLowerCase().indexOf(transitionWords.get(i).toLowerCase()) != -1){
                node.setParent(previousArgumentId);
                System.out.println("found transition: "+argument);
            }

        }

    }


}
