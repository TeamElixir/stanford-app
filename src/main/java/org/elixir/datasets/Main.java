package org.elixir.datasets;

import org.elixir.controllers.PairUserAnnotationController;
import org.elixir.controllers.SentencePairsController;
import org.elixir.controllers.SentencesController;
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
    }
}
