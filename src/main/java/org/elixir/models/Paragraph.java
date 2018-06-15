package org.elixir.models;

import java.util.ArrayList;

public class Paragraph {
    public static final String TABLE_NAME = "paragraphs";
    private int id;
    private int caseNumber;
    private String paragraph;
    private ArrayList<CorefChainOfParagraph> corefChains;

    public Paragraph(int id, int caseNumber, String paragraph) {
        this.id = id;
        this.caseNumber = caseNumber;
        this.paragraph = paragraph;
        this.corefChains = new ArrayList<>();
    }

    public Paragraph(int caseNumber, String paragraph) {
        this.caseNumber = caseNumber;
        this.paragraph = paragraph;
        this.corefChains = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Paragraph{" +
                "caseNumber=" + caseNumber +
                ", paragraph='" + paragraph + '\'' +
                '}';
    }

    public void addCorefChain(CorefChainOfParagraph corefChain) {
        this.corefChains.add(corefChain);
    }

    public ArrayList<CorefChainOfParagraph> getCorefChains() {
        return corefChains;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCaseNumber() {
        return caseNumber;
    }

    public String getParagraph() {
        return paragraph;
    }
}
