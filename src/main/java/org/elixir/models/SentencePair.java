package org.elixir.models;

public class SentencePair {
    public static final String TABLE_NAME = "sentence_pairs_from_algorithm";
    private int id;
    private int sourceSntcId;
    private int targetSntcId;
    private int relation;

    public SentencePair(int id, int sourceSntcId, int targetSntcId, int relation) {
        this.id = id;
        this.sourceSntcId = sourceSntcId;
        this.targetSntcId = targetSntcId;
        this.relation = relation;
    }

    public SentencePair() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSourceSntcId() {
        return sourceSntcId;
    }

    public void setSourceSntcId(int sourceSntcId) {
        this.sourceSntcId = sourceSntcId;
    }

    public int getTargetSntcId() {
        return targetSntcId;
    }

    public void setTargetSntcId(int targetSntcId) {
        this.targetSntcId = targetSntcId;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    @Override
    public String toString() {
        return "SentencePair{" +
                "id=" + id +
                ", sourceSntcId=" + sourceSntcId +
                ", targetSntcId=" + targetSntcId +
                ", relation=" + relation +
                '}';
    }
}
