package org.elixir.models;

public class Phrase {
    public static final String TABLE_NAME = "phrases";
    private int id;
    private String caseFileName;
    private String phrase;
    private String sentiment;

    public Phrase(int id, String phrase, String sentiment, String caseFileName) {
        this.id = id;
        this.caseFileName = caseFileName;
        this.phrase = phrase;
        this.sentiment = sentiment;
    }

    public Phrase(String phrase, String sentiment, String caseFileName) {
        this.caseFileName = caseFileName;
        this.phrase = phrase;
        this.sentiment = sentiment;
    }

    public int getId() {
        return id;
    }

    public String getCaseFileName() {
        return caseFileName;
    }

    public String getPhrase() {
        return phrase;
    }

    public String getSentiment() {
        return sentiment;
    }

    @Override
    public String toString() {
        return "Phrase{" +
                "id=" + id +
                ", caseFileName='" + caseFileName + '\'' +
                ", phrase='" + phrase + '\'' +
                ", sentiment='" + sentiment + '\'' +
                '}';
    }
}
