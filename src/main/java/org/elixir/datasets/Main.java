package org.elixir.datasets;

import org.elixir.controllers.PairUserAnnotationController;
import org.elixir.controllers.SentencePairsController;
import org.elixir.controllers.SentencesController;
import org.elixir.controllers.SimpleRelationsController;
import org.elixir.models.PairUserAnnotation;
import org.elixir.models.Sentence;
import org.elixir.models.SentencePair;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Sentence> allSentences = SentencesController.getAllSentences();

        ArrayList<PairUserAnnotation> allPairUserAnnotations = PairUserAnnotationController.getAllPairUserAnnotations();

        ArrayList<SentencePair> allSentencePairs = SentencePairsController.getAllSentencePairs();
        System.out.println(allSentencePairs.size());

        ArrayList<PairUserAnnotation> pairAnnotationsWhereBothJudgesAgree = getPairAnnotationsWhereBothJudgesAgree();

        int i = 1;
        for (PairUserAnnotation pairUserAnnotation : pairAnnotationsWhereBothJudgesAgree) {
            SentencePair sentencePair = SentencePairsController.getSentencerPairById(pairUserAnnotation.getPairId());
            String sourceSentence = SentencesController.getSentenceById(sentencePair.getSourceSntcId()).getSentence();
            String targetSentence = SentencesController.getSentenceById(sentencePair.getTargetSntcId()).getSentence();

            System.out.println();
            System.out.println("PairID: " + pairUserAnnotation.getPairId());
            System.out.println("Source: " + sourceSentence);
            System.out.println("Target: " + targetSentence);
            System.out.println("Annotation: " + SimpleRelationsController.getSimpleRelation(pairUserAnnotation.getAnnotation()));
            System.out.println(i++);
        }
    }

    public static ArrayList<PairUserAnnotation> getPairAnnotationsWhereBothJudgesAgree() {
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
