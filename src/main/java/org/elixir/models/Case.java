package org.elixir.models;

import java.util.ArrayList;

public class Case {
    private int number;

    private ArrayList<Sentence> sentences;

    private ArrayList<Paragraph> paragraphs;


    private String fileName;

    public Case() {
        sentences = new ArrayList<>();
    }

    public Case(int number) {
        this.number = number;
        this.sentences = new ArrayList<>();
        this.paragraphs = new ArrayList<>();
    }

    public int getNumber() {
        return number;
    }

    public ArrayList<Paragraph> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(ArrayList<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public void setSentences(ArrayList<Sentence> sentences) {
        this.sentences = sentences;
    }

    public ArrayList<Sentence> getSentences() {
        return sentences;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
