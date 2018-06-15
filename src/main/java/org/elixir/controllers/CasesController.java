package org.elixir.controllers;

import org.elixir.models.Case;
import org.elixir.models.Paragraph;

import java.util.ArrayList;

public class CasesController {
    public static Case getCaseByNumber(int n) {
        Case aCase = new Case(n);
        aCase.setParagraphs(ParagraphsController.getParagraphsOfCase(n));

        return aCase;
    }

    // fetch all paragraphs, corefChains, and sentences of paragraphs
    private static ArrayList<Case> fetchAllData() {
        ArrayList<Case> cases = new ArrayList<>();
        for (int i = 11; i <= 20; i++) {
            ArrayList<Paragraph> paragraphsOfCase = ParagraphsController.getParagraphsOfCase(i);
            for (Paragraph p : paragraphsOfCase) {
                p.setCorefChains(CorefChainOfParagraphsController.getCorefChainsOfParagraph(p.getId()));
                p.setSentences(SentencesOfParagraphsController.getSentencesOfParagraph(p.getId()));
            }
            Case aCase = new Case(i);
            aCase.setParagraphs(paragraphsOfCase);
            cases.add(aCase);
        }

        return cases;
    }
}
