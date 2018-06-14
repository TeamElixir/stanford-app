package org.elixir.models;

public class Paragraph {
    public static final String TABLE_NAME = "paragraphs";
    private int id;
    private int caseNumber;
    private String paragraph;

    public Paragraph(int id, int caseNumber, String paragraph) {
        this.id = id;
        this.caseNumber = caseNumber;
        this.paragraph = paragraph;
    }

    public Paragraph(int caseNumber, String paragraph) {
        this.caseNumber = caseNumber;
        this.paragraph = paragraph;
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
