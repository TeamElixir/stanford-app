package org.elixir.models;

public class CorefChainOfPara {
    public static final String TABLE_NAME = "coref_chains";
    private int id;
    private int paragraphId;
    private String corefChain;

    public CorefChainOfPara(int id, int paragraphId, String corefChain) {
        this.id = id;
        this.paragraphId = paragraphId;
        this.corefChain = corefChain;
    }

    public CorefChainOfPara(int paragraphId, String corefChain) {
        this.paragraphId = paragraphId;
        this.corefChain = corefChain;
    }

    @Override
    public String toString() {
        return "CorefChainOfPara{" +
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
