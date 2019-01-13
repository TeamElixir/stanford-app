package org.elixir.models;

public class SentencePair {
    public static final String TABLE_NAME = "sentence_pairs_from_algorithm";
    private int id;
    private int source_sntc_id;
    private int target_sntc_id;
    private int relation;

    public SentencePair(int id, int source_sntc_id, int target_sntc_id, int relation) {
        this.id = id;
        this.source_sntc_id = source_sntc_id;
        this.target_sntc_id = target_sntc_id;
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

    public int getSource_sntc_id() {
        return source_sntc_id;
    }

    public void setSource_sntc_id(int source_sntc_id) {
        this.source_sntc_id = source_sntc_id;
    }

    public int getTarget_sntc_id() {
        return target_sntc_id;
    }

    public void setTarget_sntc_id(int target_sntc_id) {
        this.target_sntc_id = target_sntc_id;
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
                ", source_sntc_id=" + source_sntc_id +
                ", target_sntc_id=" + target_sntc_id +
                ", relation=" + relation +
                '}';
    }
}
