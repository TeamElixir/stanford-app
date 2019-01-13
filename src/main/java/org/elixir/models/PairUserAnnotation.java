package org.elixir.models;

public class PairUserAnnotation {
    public static final String TABLE_NAME = "pair_user_annotations";
    private int id;
    private int pair_id;
    private int user_id;
    private int relation;

    public PairUserAnnotation() {
    }

    public PairUserAnnotation(int id, int pair_id, int user_id, int relation) {
        this.id = id;
        this.pair_id = pair_id;
        this.user_id = user_id;
        this.relation = relation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPair_id() {
        return pair_id;
    }

    public void setPair_id(int pair_id) {
        this.pair_id = pair_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    @Override
    public String toString() {
        return "PairUserAnnotation{" +
                "id=" + id +
                ", pair_id=" + pair_id +
                ", user_id=" + user_id +
                ", relation=" + relation +
                '}';
    }
}
