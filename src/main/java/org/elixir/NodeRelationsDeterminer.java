package org.elixir;

import org.elixir.models.Node;
import org.elixir.utils.CaseLawStatementExtractor;

import java.util.*;
import java.lang.*;
import java.util.regex.*;

public class NodeRelationsDeterminer {

    private static ArrayList<String> regexMatches = new ArrayList<>();

    public static ArrayList<Node> nodesRelations;
    public static boolean regexResult = false;
    public static boolean citationResult = false;
    public static boolean previousWasCitation = false;
    private static int checkWordCount = 4;


    static final String[] testcases = new String[]{
            "A 'reasonable probability' of a different result\" is one in which the suppressed evidence \"" +
                    " 'undermines confidence in the outcome of the trial.' \""
    };

    private static final ArrayList<String> transitionWords = new ArrayList<>(
            Arrays.asList("To make that", "in the first place", "again", "to", "moreover", "more", "not only",
                    "but also", "as well as", "as a matter of fact", "and", "together with", "in like manner",
                    "also", "of course", "in addition", "then", "likewise","coupled with","equally","comparatively",
                    "in the same fashion", "in the same way", "identically","correspondingly","first","uniquely",
                    "similarly", "like", "furthermore", "not to mention", "as", "additionally", "to say nothing of",
                    "too", "equally important", "by the same token", "although" , "but", "in contrast", "still", "instead",
                    "different from","unlike","whereas", "despite", "on the other hand", "yet", "conversely", "on the contrary",
                    "otherwise", "at the same time", "albeit", "however", "in spite of", "besides", "rather", "even so ", "even though","as much as",
                    "nevertheless", "be that as it may","nonetheless", "regardless","above", "notwithstanding",
                    "after all", "with this intention","so that","even if","with this in mind","because of this",
                    "due to this","that", "those", "such", "as mentioned here","in other words",
                    "in fact","in general", "including this","in this case","for example","for this reason","for instance",
                    "to put it", "to demonstrate this", "that is to say", "indeed","especially","on the positive side",
                    "on the negative side", "with this in mind", "as a result", "consequently", "thus", "therefore",
                    "in that case","thereupon","forthwith", "in effect","hence", "accordingly", "henceforth"

            ));

    static String previousSentence = null;
            //"Such \"evidence is 'material' . . . when there is a reasonable probability  had the evidence been disclosed, the result of the proceeding would have been different.\"";

    static String previousArgumentId=null;


    public static void initialize(){
        nodesRelations=ArgumentTreeGenerator.nodes;
    }



    public static void checkRelationships(){
        for (int i=0;i<nodesRelations.size();i++) {
            // System.out.print("Input: "+arg+" -> Matches: ");
            Node node = nodesRelations.get(i);
            String arg = node.getArgument();
            if(!previousWasCitation) {
                if (node.getId().length() > 2) {
                    previousSentence = nodesRelations.get(i - 1).getArgument();
                    previousArgumentId = nodesRelations.get(i - 1).getId();

                    citationResult = checkCitationRelation(arg, node);

                    regexResult = checkElaborationRegex(arg, node);
                    if (!regexResult) {
                        checkTransitionRelation(arg, node);
                    }


                    System.out.println(regexMatches.toString());
                    System.out.println(regexResult);
                    System.out.println(node.getId().length());
                }

                regexMatches.clear();
            }else{
                checkCitation(arg,node);
            }

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

        //Splits words & assign to the arr[]
        String [] arr = argument.split("\\s+");
        String selectedWords="";

        for(int i=0; i<checkWordCount ; i++){
            selectedWords = selectedWords + " " + arr[i] ;
        }


        for(int i=0;i<transitionWords.size();i++){
            Pattern p = Pattern.compile("("+transitionWords.get(i)+")", Pattern.MULTILINE);
            Matcher m = p.matcher(selectedWords);
            if(m.find()){
                node.setParent(previousArgumentId);
                System.out.println("found transition: "+argument);
                break;
            }

        }

    }

    public static boolean checkCitationRelation(String argument, Node node){

        if(CaseLawStatementExtractor.extractUsingDirectCitation(argument)!=null ||
                CaseLawStatementExtractor.extractUsingIndirectReference(argument) !=null){

            node.setParent(previousArgumentId);
            node.setType("red");
            System.out.println("found citation :" + argument);
            previousWasCitation= true;
            return true;
        }
        return false;

    }

    public static void checkCitation(String argument, Node node){
        if(CaseLawStatementExtractor.extractUsingDirectCitation(argument)!=null ||
                CaseLawStatementExtractor.extractUsingIndirectReference(argument) !=null){
            node.setType("red");
            previousWasCitation= true;

        }else {
            previousWasCitation = false;
        }
    }


}
