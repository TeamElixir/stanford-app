package org.elixir.models;

public class PairUserAnnotation {
    public static final String TABLE_NAME = "pair_user_annotations";
    private int id;
    private int pairId;
    private int userId;
    private int annotation;

    public PairUserAnnotation() {
    }

    public PairUserAnnotation(int id, int pairId, int userId, int annotation) {
        this.id = id;
        this.pairId = pairId;
        this.userId = userId;
        this.annotation = annotation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPairId() {
        return pairId;
    }

    public void setPairId(int pairId) {
        this.pairId = pairId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAnnotation() {
        return annotation;
    }

    public void setAnnotation(int annotation) {
        this.annotation = annotation;
    }

    @Override
    public String toString() {
        return "PairUserAnnotation{" +
                "id=" + id +
                ", pairId=" + pairId +
                ", userId=" + userId +
                ", annotation=" + annotation +
                '}';
    }
}
