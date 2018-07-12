package org.elixir;

//party identification and extraction happens in this class.

/*task 1
 * provide manually extracted phrases and check the sentiment
 */

/*task 2
 * solve coreferences in sentences and save in the database, along with that it may require to store mentions
 */

/*task 3
 *design algorithm;
 */

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.ejml.simple.SimpleMatrix;
import org.elixir.models.SentimentMatrix;
import org.elixir.utils.CoreNLPDepParser;
import org.elixir.utils.CorefChainMapping;
import org.elixir.utils.CustomizeSentimentAnnotator;

import java.io.*;
import java.util.*;

import static org.elixir.utils.NLPUtils.getParsedSentimentMatrix;


public class PartyExtractionMain {

    public static void main(String[] args) throws IOException {

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,natlog,depparse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        String absoluteFilePath = new File("").getAbsolutePath();
        String sourceFilePath = absoluteFilePath + "/src/main/resources/SentimentAnalysis/thatSent/";
        String targetFilePath = absoluteFilePath + "/src/main/resources/SentimentAnalysis/InnerOuterSentenceSentiment/";

        Properties propsSentiment = new Properties();
        propsSentiment.setProperty("annotators", "tokenize,ssplit,tokenize,pos,lemma,parse,natlog,sentiment");
        StanfordCoreNLP sentimentPipeline = new StanfordCoreNLP(propsSentiment);


        Scanner sc = new Scanner(new File(sourceFilePath + "case_11.txt"));

        CustomizeSentimentAnnotator.addSentimentLayerToCoreNLPSentiment("sentimentAnnotator/DeviatedSentimentWords/non_positive_mini.csv",
                "sentimentAnnotator/DeviatedSentimentWords/non_negative_mini.csv",
                "sentimentAnnotator/DeviatedSentimentWords/non_neutral_mini.csv");


        File fullInfoFile = new File(targetFilePath + "case_11/fullinfo.txt");
        fullInfoFile.getParentFile().mkdirs();

        File intermediateSentimentFile = new File(targetFilePath + "case_11/InnerOuterSentiment.txt");
        intermediateSentimentFile.getParentFile().mkdirs();

        File party_file = new File(targetFilePath + "case_11/party-combinations-first-iteration.txt");
        party_file.getParentFile().mkdirs();

        BufferedWriter br = new BufferedWriter(new FileWriter(fullInfoFile));
        BufferedWriter br2 = new BufferedWriter(new FileWriter(intermediateSentimentFile));
        BufferedWriter br3 = new BufferedWriter(new FileWriter(party_file));

        ArrayList<SubjectCombination> combinationArrayList = new ArrayList<>();

//        while (sc.hasNextLine()){
        String text = "At an evidentiary hearing, both Lee and his plea-stage counsel testified that \"deportation was" +
                " the determinative issue\" to Lee in deciding whether to accept a plea, and Lee's counsel acknowledged " +
                "that although Lee's defense to the charge was weak, if he had known Lee would be deported upon pleading" +
                " guilty, he would have advised him to go to trial.";
        Annotation ann = new Annotation(text);
        pipeline.annotate(ann);

        for (IndexedWord[] array : CoreNLPDepParser.findIndicesOfOuterVerbAndInnerVerb(ann, text)) {
            IndexedWord thatIndexWord = array[1];
            IndexedWord innerVerbIndexWord = array[0];

            String innerSentence = endExtractedInnerSentence(startPointIndexInnerSentence(thatIndexWord.beginPosition(), innerVerbIndexWord.beginPosition(), text), text);
            innerSentence = WhTagFilterInEnd(innerSentence);

            IndexedWord innersubject = CoreNLPDepParser.findSubjectForGivenVerb(ann, innerVerbIndexWord);
            String innerSubjectContext = "none-innersubject";
            if (innersubject != null) {
                innerSubjectContext = CoreNLPDepParser.findSubjectContext(ann, innersubject);

            }

            IndexedWord outerVerb = CoreNLPDepParser.findRelatedGovWordForGivenWord(ann, "ccomp", innerVerbIndexWord);
            String outerVerbContext = CoreNLPDepParser.findVerbContext(ann, outerVerb);

            IndexedWord outerSubject = CoreNLPDepParser.findSubjectForGivenVerb(ann, outerVerb);

            String outerSubjectContext = "none-outerSubject";
            if (outerSubject != null) {
                outerSubjectContext = CoreNLPDepParser.findSubjectContext(ann, outerSubject);
            }


            CustomizeSentimentAnnotator.createPosTagMapForSentence(innerSentence);
            Annotation ann2 = new Annotation(innerSentence);
            sentimentPipeline.annotate(ann2);

            List<CoreMap> sentences = ann2.get(CoreAnnotations.SentencesAnnotation.class);

            br.write(text + "\n");
            br.write("\t" + "innerSentence : " + innerSentence + "\n");

            System.out.println("innerSentence : " + innerSentence);

            SubjectCombination subjectCombination = new SubjectCombination();
            subjectCombination.party_1 = innerSubjectContext;
            subjectCombination.party_2 = outerSubjectContext;

            for (CoreMap coreMapSentence : sentences) {
                final Tree tree = coreMapSentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                final SimpleMatrix sm = RNNCoreAnnotations.getPredictions(tree);

                System.out.println(sm);
                SentimentMatrix parsedSentimentMatrix = getParsedSentimentMatrix(sm, coreMapSentence.toString());


                final String sentiment = coreMapSentence.get(SentimentCoreAnnotations.SentimentClass.class);
                System.out.println("sentence:  " + coreMapSentence);
                System.out.println("sentiment: " + sentiment);
                System.out.println("matrix:    " + sm);
                br.write("\t\t" + "coremap sentence : " + coreMapSentence + "\n");
                br.write("\t\t" + "sentiment : " + sentiment + "\n");
                br.write("\t\t" + "matrix" + sm + "\n");
                br.write("\t\t-----\n");
                br3.write(innerSentence + "\n");
                br3.write(sentiment + "\n");
                br3.write(sm.toString() + "\n");
                br3.flush();

                System.out.println();
                subjectCombination.sm1 = sm;
                if (sentiment.toLowerCase().equals("negative")) {
                    subjectCombination.innerSentiment = "negative";
                } else {
                    subjectCombination.innerSentiment = "non-negative";
                }
            }


            String outerSentence = outerSubjectContext.trim() + " " + outerVerbContext + "something.";
            CustomizeSentimentAnnotator.createPosTagMapForSentence(outerSentence);

            ann2 = new Annotation(outerSentence);
            sentimentPipeline.annotate(ann2);
            sentences = ann2.get(CoreAnnotations.SentencesAnnotation.class);

            System.out.println("outerSentence");
            br.write("\t" + "outerSentence : " + outerSentence + "\n");
            subjectCombination.innerSentence = innerSentence;
            subjectCombination.outerSentence = outerSentence;
            for (CoreMap coreMapSentence : sentences) {

                final Tree tree = coreMapSentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                final SimpleMatrix sm = RNNCoreAnnotations.getPredictions(tree);
                final String sentiment = coreMapSentence.get(SentimentCoreAnnotations.SentimentClass.class);
                System.out.println("sentence:  " + coreMapSentence);
                System.out.println("sentiment: " + sentiment);
                System.out.println("matrix:    " + sm);
                br.write("\t\t" + "coremap sentence : " + coreMapSentence + "\n");
                br.write("\t\t" + "sentiment : " + sentiment + "\n");
                br.write("\t\t" + "matrix" + sm + "\n");
                br.write("\t\t-----\n");

                br3.write(outerSentence + "\n");
                br3.write(sentiment + "\n");
                br3.write(sm.toString() + "\n");
                br3.flush();

                System.out.println("\n");
                subjectCombination.sm2 = sm;

                if (sentiment.toLowerCase().equals("negative")) {
                    subjectCombination.outerSentiment = "negative";
                } else {
                    subjectCombination.outerSentiment = "non-negative";
                }
            }

            br.write("\t outer Subject : " + outerSubjectContext);
            br.write(", \t inner Subject : " + innerSubjectContext + "\n");
            br3.write(outerSubjectContext);
            br3.write("::" + innerSubjectContext + "\n");
            br3.newLine();
            br3.flush();
            combinationArrayList.add(subjectCombination);
        }
        br.newLine();

        br.close();
        br3.close();

        br2.write("one negative and the other is non-negative\n\n");
        for (SubjectCombination combination : combinationArrayList) {
            if ((combination.innerSentiment.equals("negative") && combination.outerSentiment.equals("non-negative"))
                    || combination.innerSentiment.equals("non-negative") && combination.outerSentiment.equals("negative")) {
                br2.write(combination.party_1 + " - " + combination.party_2 + "\n");
            }
        }
        br2.write("-----------------\n\n");

        br2.write("both non-negative\n\n");
        for (SubjectCombination combination : combinationArrayList) {
            if (combination.innerSentiment.equals("negative") && combination.outerSentiment.equals("negative")) {
                br2.write(combination.party_1 + " - " + combination.party_2 + "\n");
            }
        }
        br2.write("-----------------\n\n");
        br2.flush();
        br2.close();

        for (SubjectCombination combination : combinationArrayList) {
            if (combination.innerSentiment.equals("non-negative") && combination.outerSentiment.equals("non-negative")) {
                System.out.println(combination.innerSentence);
                System.out.println(combination.sm1);
                System.out.println(combination.outerSentence);
                System.out.println(combination.sm2);
                System.out.println(combination.party_1 + " - " + combination.party_2);
                System.out.println("------------------");
                System.out.println();

            }
        }
    }



    public static void outputPartyExtraction(StanfordCoreNLP depparsePipeline, StanfordCoreNLP sentimentPipeline,
                                             int sentenceId, String currentSentence, String caseName,
                                             ArrayList<CorefChainMapping> ccmList, BufferedWriter br,
                                             BufferedWriter br2, BufferedWriter br3) throws IOException {


        ArrayList<SubjectCombination> combinationArrayList = new ArrayList<>();

        String text = currentSentence;
        Annotation ann = new Annotation(text);
        depparsePipeline.annotate(ann);

        for (IndexedWord[] array : CoreNLPDepParser.findIndicesOfOuterVerbAndInnerVerb(ann, text)) {
            IndexedWord thatIndexWord = array[1];
            IndexedWord innerVerbIndexWord = array[0];

            String innerSentence = endExtractedInnerSentence(startPointIndexInnerSentence(thatIndexWord.beginPosition(),
                    innerVerbIndexWord.beginPosition(), text), text);
            innerSentence = WhTagFilterInEnd(innerSentence);

            IndexedWord innersubject = CoreNLPDepParser.findSubjectForGivenVerb(ann, innerVerbIndexWord);
            String innerSubjectContext = "none-innersubject";
            if (innersubject != null) {
                innerSubjectContext = CoreNLPDepParser.findSubjectContext(ann, innersubject);

            }

            IndexedWord outerVerb = CoreNLPDepParser.findRelatedGovWordForGivenWord(ann, "ccomp", innerVerbIndexWord);
            String outerVerbContext = CoreNLPDepParser.findVerbContext(ann, outerVerb);

            IndexedWord outerSubject = CoreNLPDepParser.findSubjectForGivenVerb(ann, outerVerb);

            String outerSubjectContext = "none-outerSubject";
            if (outerSubject != null) {
                outerSubjectContext = CoreNLPDepParser.findSubjectContext(ann, outerSubject);
            }


            CustomizeSentimentAnnotator.createPosTagMapForSentence(innerSentence);
            Annotation ann2 = new Annotation(innerSentence);
            sentimentPipeline.annotate(ann2);

            List<CoreMap> sentences = ann2.get(CoreAnnotations.SentencesAnnotation.class);

            br.write(text + "\n");
            br.write("\t" + "innerSentence : " + innerSentence + "\n");

            System.out.println("innerSentence : " + innerSentence);

            SubjectCombination subjectCombination = new SubjectCombination();
            subjectCombination.party_1 = innerSubjectContext;
            subjectCombination.party_2 = outerSubjectContext;

            for (CoreMap coreMapSentence : sentences) {
                final Tree tree = coreMapSentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                final SimpleMatrix sm = RNNCoreAnnotations.getPredictions(tree);
                final String sentiment = coreMapSentence.get(SentimentCoreAnnotations.SentimentClass.class);
                System.out.println("sentence:  " + coreMapSentence);
                System.out.println("sentiment: " + sentiment);
                System.out.println("matrix:    " + sm);
                br.write("\t\t" + "coremap sentence : " + coreMapSentence + "\n");
                br.write("\t\t" + "sentiment : " + sentiment + "\n");
                br.write("\t\t" + "matrix" + sm + "\n");
                br.write("\t\t-----\n");
                br3.write(innerSentence + "\n");
                br3.write(sentiment + "\n");
                br3.write(sm.toString() + "\n");
                br3.flush();

                System.out.println();
                subjectCombination.sm1 = sm;
                if (sentiment.toLowerCase().equals("negative")) {
                    subjectCombination.innerSentiment = "negative";
                } else {
                    subjectCombination.innerSentiment = "non-negative";
                }
            }


            String outerSentence = outerSubjectContext.trim() + " " + outerVerbContext + "something.";
            CustomizeSentimentAnnotator.createPosTagMapForSentence(outerSentence);

            ann2 = new Annotation(outerSentence);
            sentimentPipeline.annotate(ann2);
            sentences = ann2.get(CoreAnnotations.SentencesAnnotation.class);

            System.out.println("outerSentence");
            br.write("\t" + "outerSentence : " + outerSentence + "\n");
            subjectCombination.innerSentence = innerSentence;
            subjectCombination.outerSentence = outerSentence;
            for (CoreMap coreMapSentence : sentences) {

                final Tree tree = coreMapSentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                final SimpleMatrix sm = RNNCoreAnnotations.getPredictions(tree);
                final String sentiment = coreMapSentence.get(SentimentCoreAnnotations.SentimentClass.class);
                System.out.println("sentence:  " + coreMapSentence);
                System.out.println("sentiment: " + sentiment);
                System.out.println("matrix:    " + sm);
                br.write("\t\t" + "coremap sentence : " + coreMapSentence + "\n");
                br.write("\t\t" + "sentiment : " + sentiment + "\n");
                br.write("\t\t" + "matrix" + sm + "\n");
                br.write("\t\t-----\n");

                br3.write(outerSentence + "\n");
                br3.write(sentiment + "\n");
                br3.write(sm.toString() + "\n");
                br3.flush();

                System.out.println("\n");
                subjectCombination.sm2 = sm;

                if (sentiment.toLowerCase().equals("negative")) {
                    subjectCombination.outerSentiment = "negative";
                } else {
                    subjectCombination.outerSentiment = "non-negative";
                }
            }

            //correference resolved
            String outerSubjectResolution = "--";
            for (CorefChainMapping ccm : ccmList) {
                String resolvedTerm = ccm.resolveTerm(outerSubjectContext, sentenceId);
                if (resolvedTerm != null) {
                    outerSubjectResolution = resolvedTerm;
                }
            }

            //correference resolved
            String innerSubjectResoultion = "--";
            for (CorefChainMapping ccm : ccmList) {
                String resolvedTerm = ccm.resolveTerm(innerSubjectContext, sentenceId);

                if (resolvedTerm != null) {
                    innerSubjectResoultion = resolvedTerm;
                }
            }
            br.write("\t outer Subject : " + outerSubjectContext);
            br.write("\t,\tinner Subject : " + innerSubjectContext + "\n");
            br.write("resolved as : (" + outerSubjectResolution + ", " + innerSubjectResoultion + ")\n");
            br3.write(outerSubjectContext + "," + outerSubjectResolution);
            br3.write("::" + innerSubjectContext + "," + innerSubjectResoultion + "\n");
            br3.newLine();
            br.flush();
            br3.flush();
            combinationArrayList.add(subjectCombination);
        }
        br.newLine();
//        br2.write("one negative and the other is non-negative\n\n");
//        for(SubjectCombination combination : combinationArrayList){
//            if((combination.innerSentiment.equals("negative") && combination.outerSentiment.equals("non-negative"))
//                    || combination.innerSentiment.equals("non-negative") && combination.outerSentiment.equals("negative")){
//                br2.write(combination.party_1 + " - " + combination.party_2 + "\n");
//            }
//        }
//        br2.write("-----------------\n\n");
//
//        br2.write("both non-negative\n\n");
//        for(SubjectCombination combination : combinationArrayList){
//            if(combination.innerSentiment.equals("negative") && combination.outerSentiment.equals("negative")){
//                br2.write(combination.party_1 + " - " + combination.party_2 + "\n");
//            }
//        }
//        br2.write("-----------------\n\n");
//        br2.flush();
//        br.flush();
//        br3.flush();
//
//        for(SubjectCombination combination : combinationArrayList){
//            if(combination.innerSentiment.equals("non-negative") && combination.outerSentiment.equals("non-negative")){
//                System.out.println(combination.innerSentence);
//                System.out.println(combination.sm1);
//                System.out.println(combination.outerSentence);
//                System.out.println(combination.sm2);
//                System.out.println(combination.party_1 + " - " +combination.party_2);
//                System.out.println("------------------");
//                System.out.println();
//
//            }
//        }

    }


    //should provide the part of sentence begins after the term "that" : fine
    public static String endExtractedInnerSentence(int startIndex, String text) {
        //todo : if that followed by when, where or although kind a word dismiss for now
        char[] endPointChars = {',', ':', ';', '?'};

        int firstEndPointIndex = text.length();

        for (int count = startIndex; count < text.length(); count++) {
            if (endPointChars[0] == text.charAt(count) || endPointChars[1] == text.charAt(count)) {
                if (firstEndPointIndex > count) {
                    firstEndPointIndex = count;
                    break;
                }
            }
        }
        String returnString = null;
        if (firstEndPointIndex > -1) {
            returnString = text.substring(startIndex, firstEndPointIndex);
        }
        return returnString;
    }

    //returns the start of inner sentence, should provide the index of that: verb related with the verb prior to that, and whole sentence
    public static int startPointIndexInnerSentence(int indexOfThat, int verbIndex, String sentence) {

        char[] startChars = {',', ':', ';'};
        int startIndex1 = indexOfThat + 4;

        for (int i = verbIndex; i > 0; i--) {
            if (startChars[0] == sentence.charAt(i) && startIndex1 < i + 1) {
                startIndex1 = i + 1;
                break;
            }
        }
        return startIndex1;
    }

    //when the sentence is like " ---- when <some sentence>" needs to remove that when part (any wh word)
    //todo : use the previously used annotation
    public static String WhTagFilterInEnd(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation ann = new Annotation(text);
        pipeline.annotate(ann);

        String[] whPosTags = {"WDT", "WP", "WRB"};

        for (CoreMap coreMapSentence : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : coreMapSentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String postag = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

                if (Arrays.asList(whPosTags).contains(postag.toUpperCase())) {
                    return text.substring(0, text.indexOf(word) - 1);
                }
            }
        }
        return text;
    }

    static class SubjectCombination {
        String party_1;
        String party_2;
        String innerSentiment;
        String outerSentiment;
        SimpleMatrix sm1;
        SimpleMatrix sm2;
        String innerSentence;
        String outerSentence;

    }

    public static void processIntermediateFile(String caseName) throws IOException {
        String absoluteFilePath = new File("").getAbsolutePath();
        String sourceFilePath = absoluteFilePath + "/src/main/resources/SentimentAnalysis/InnerOuterSentenceSentiment/" + caseName + "/intermediate.txt";

        Scanner sc = null;
        try {
            sc = new Scanner(new File(sourceFilePath));
        } catch (FileNotFoundException e) {
            System.out.println("intermediate file  is not found. ");
        }

        BufferedWriter br1 = new BufferedWriter(new FileWriter(new File(absoluteFilePath + "/src/main/resources/SentimentAnalysis/InnerOuterSentenceSentiment/"
                + caseName + "/one_negative.txt")));
        BufferedWriter br2 = new BufferedWriter(new FileWriter(new File(absoluteFilePath + "/src/main/resources/SentimentAnalysis/InnerOuterSentenceSentiment/"
                + caseName + "/negative_negative.txt")));
        /* this is the string file segment to be added during one iteration

        the drugs were his
        Neutral
        Type = dense , numRows = 5 , numCols = 1
         0.000
         0.003
         0.994
         0.003
         0.000

        Lee admitted something.
        Neutral
        Type = dense , numRows = 5 , numCols = 1
         0.003
         0.019
         0.734
         0.234
         0.010

        Lee ,Petitioner Jae Lee::drugs ,--

        */
        while (sc.hasNextLine()) {
            skipScannerLines(sc, 1);
            String innerSentimentOutput = sc.nextLine().toLowerCase();
            skipScannerLines(sc, 2);
            Double innerSentimentMatrixValue = Double.parseDouble(sc.nextLine());
            skipScannerLines(sc, 5);

            String outerSentimentOutput = sc.nextLine().toLowerCase();
            skipScannerLines(sc, 2);
            Double outerSentimentMatrixValue = Double.parseDouble(sc.nextLine());
            skipScannerLines(sc, 4);

            String subjectInfoLine = sc.nextLine();
            String[] outerInnerSubjects = subjectInfoLine.split("::");
            String[] innerSubjectParts = outerInnerSubjects[0].split(",");
            String[] outerSubjectParts = outerInnerSubjects[1].split(",");
            String output = outerSubjectParts[0] + " : " + innerSubjectParts[0] + "\t (" + outerSubjectParts[1] + " : " + innerSubjectParts[1] + ")";

            if ((innerSentimentOutput.equals("negative") || innerSentimentMatrixValue >= 0.4)
                    && (outerSentimentOutput.equals("negative") || outerSentimentMatrixValue >= 0.4)) {
                br2.write(output + "\n");
                br2.flush();
            } else if ((innerSentimentOutput.equals("negative") || innerSentimentMatrixValue >= 0.4)
                    || (outerSentimentOutput.equals("negative") || outerSentimentMatrixValue >= 0.4)) {
                br1.write(output + "\n");
                br1.flush();
            }
            skipScannerLines(sc, 1);
        }
        br1.close();
        br2.close();
    }

    private static void skipScannerLines(Scanner sc, int numberOfLines) {
        int count = 0;
        while (sc.hasNextLine() && count < numberOfLines) {
            sc.nextLine();
            count += 1;
        }
    }
}
