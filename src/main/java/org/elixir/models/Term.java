package org.elixir.models;

public class Term {
    public static final String TABLE_NAME = "words";
    private int id;
    private String term;
    private int frequency;
    private String sentiment;

    public Term(int id, String term, int frequency, String sentiment) {
        this.id = id;
        this.term = term;
        this.frequency = frequency;
        this.sentiment = sentiment;
    }

    public Term(String term, int frequency, String sentiment) {
        this.term = term;
        this.frequency = frequency;
        this.sentiment = sentiment;
    }

    @Override
    public String toString() {
        return "Term{" +
                "id=" + id +
                ", term='" + term + '\'' +
                ", frequency=" + frequency +
                ", sentiment='" + sentiment + '\'' +
                '}';
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public int getId() {
        return id;
    }

    public String getTerm() {
        return term;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getSentiment() {
        return sentiment;
    }
}
