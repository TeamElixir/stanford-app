package org.elixir.datasets;

import org.elixir.controllers.PairUserAnnotationController;
import org.elixir.controllers.SentencePairsController;
import org.elixir.controllers.SentencesController;
import org.elixir.controllers.SimpleRelationsController;
import org.elixir.models.PairUserAnnotation;
import org.elixir.models.SentencePair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new File("sentence_pairs.tsv"));
        StringBuilder sb = new StringBuilder();
        final String SEPARATOR = "\t";
        sb.append("source_sentence");
        sb.append(SEPARATOR);
        sb.append("target_sentence");
        sb.append(SEPARATOR);
        sb.append("annotation");
        sb.append('\n');
        ArrayList<PairUserAnnotation> pairAnnotationsWhereBothJudgesAgree = getPairAnnotationsWhereBothJudgesAgree();
        int i = 1;
        for (PairUserAnnotation pairUserAnnotation : pairAnnotationsWhereBothJudgesAgree) {
            SentencePair sentencePair = SentencePairsController.getSentencerPairById(pairUserAnnotation.getPairId());
            String sourceSentence = SentencesController.getSentenceById(sentencePair.getSourceSntcId()).getSentence();
            String targetSentence = SentencesController.getSentenceById(sentencePair.getTargetSntcId()).getSentence();
            String simpleRelation = SimpleRelationsController.getSimpleRelation(pairUserAnnotation.getAnnotation());

            System.out.println();
            System.out.println("Source: " + sourceSentence);
            System.out.println("Target: " + targetSentence);
            System.out.println("Annotation: " + simpleRelation);
            System.out.println(i++);

            sb.append(sourceSentence);
            sb.append(SEPARATOR);
            sb.append(targetSentence);
            sb.append(SEPARATOR);
            sb.append(simpleRelation);
            sb.append("\n");

        }
        pw.write(sb.toString());
        pw.close();
        System.out.println("done!");
    }

    private static ArrayList<PairUserAnnotation> getPairAnnotationsWhereBothJudgesAgree() {
        ArrayList<PairUserAnnotation> allPairUserAnnotations = PairUserAnnotationController.getAllPairUserAnnotations();
        ArrayList<PairUserAnnotation> pairUserAnnotationsWhereBothJudgesAgree = new ArrayList<>();
        for (int i = 0; i < allPairUserAnnotations.size() - 1; i += 2) {
            PairUserAnnotation pairUserAnnotation = allPairUserAnnotations.get(i);
            PairUserAnnotation nextPairUserAnnotation = allPairUserAnnotations.get(i + 1);
            if (pairUserAnnotation.getPairId() == nextPairUserAnnotation.getPairId()) {
                if (pairUserAnnotation.getAnnotation() == nextPairUserAnnotation.getAnnotation()) {
                    pairUserAnnotationsWhereBothJudgesAgree.add(pairUserAnnotation);
                } else {
                    System.out.println("Skipping: The two judges do not agree.");
                }
            } else {
                System.err.println("Error: Pair ID mismatch in two consecutive rows.");
            }
        }

        return pairUserAnnotationsWhereBothJudgesAgree;
    }
}
