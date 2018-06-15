package org.elixir.models;

import java.util.ArrayList;

public class Paragraph {
    public static final String TABLE_NAME = "paragraphs";
    private int id;
    private int caseNumber;
    private String text;
    private ArrayList<CorefChainOfParagraph> corefChains;
    private ArrayList<SentenceOfParagraph> sentences;

    public Paragraph(int id, int caseNumber, String text) {
        this.id = id;
        this.caseNumber = caseNumber;
        this.text = text;
        this.corefChains = new ArrayList<>();
        this.sentences = new ArrayList<>();
    }

    public Paragraph(int caseNumber, String text) {
        this.caseNumber = caseNumber;
        this.text = text;
        this.corefChains = new ArrayList<>();
        this.sentences = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Paragraph{" +
                "caseNumber=" + caseNumber +
                ", text='" + text + '\'' +
                '}';
    }

    public ArrayList<SentenceOfParagraph> getSentences() {
        return sentences;
    }

    public void setSentences(ArrayList<SentenceOfParagraph> sentences) {
        this.sentences = sentences;
    }

    public void setCorefChains(ArrayList<CorefChainOfParagraph> corefChains) {
        this.corefChains = corefChains;
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

    public String getText() {
        return text;
    }
}
