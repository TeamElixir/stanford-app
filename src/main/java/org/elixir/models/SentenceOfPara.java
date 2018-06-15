package org.elixir.models;

public class SentenceOfPara {
    public static final String TABLE_NAME = "sentences_of_paras";
    private int id;
    private int paragraphId;
    private String sentence;

    public SentenceOfPara(int id, int paragraphId, String sentence) {
        this.id = id;
        this.paragraphId = paragraphId;
        this.sentence = sentence;
    }

    public SentenceOfPara(int paragraphId, String sentence) {
        this.paragraphId = paragraphId;
        this.sentence = sentence;
    }

    @Override
    public String toString() {
        return "SentenceOfPara{" +
                "paragraphId=" + paragraphId +
                ", sentence='" + sentence + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public int getParagraphId() {
        return paragraphId;
    }

    public String getSentence() {
        return sentence;
    }
}
