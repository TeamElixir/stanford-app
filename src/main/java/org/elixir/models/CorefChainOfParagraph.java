package org.elixir.models;

public class CorefChainOfParagraph {
    public static final String TABLE_NAME = "coref_chains";
    private int id;
    private int paragraphId;
    private String corefChain;

    public CorefChainOfParagraph(int id, int paragraphId, String corefChain) {
        this.id = id;
        this.paragraphId = paragraphId;
        this.corefChain = corefChain;
    }

    public CorefChainOfParagraph(int paragraphId, String corefChain) {
        this.paragraphId = paragraphId;
        this.corefChain = corefChain;
    }

    @Override
    public String toString() {
        return "CorefChainOfParagraph{" +
                "paragraphId=" + paragraphId +
                ", corefChain='" + corefChain + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public int getParagraphId() {
        return paragraphId;
    }

    public String getCorefChain() {
        return corefChain;
    }
}
