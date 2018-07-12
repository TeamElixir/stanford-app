package org.elixir.models;

public class SentimentMatrix {
    private String sentence;
    private double veryPositiveValue;
    private double positiveValue;
    private double neutralValue;
    private double negativeValue;
    private double veryNegativeValue;

    public SentimentMatrix(String sentence, double veryPositiveValue, double positiveValue, double neutralValue, double negativeValue, double veryNegativeValue) {
        this.sentence = sentence;
        this.veryPositiveValue = veryPositiveValue;
        this.positiveValue = positiveValue;
        this.neutralValue = neutralValue;
        this.negativeValue = negativeValue;
        this.veryNegativeValue = veryNegativeValue;
    }

    @Override
    public String toString() {
        return "SentimentMatrix{" +
                "sentence='" + sentence + '\'' +
                ", veryPositiveValue=" + veryPositiveValue +
                ", positiveValue=" + positiveValue +
                ", neutralValue=" + neutralValue +
                ", negativeValue=" + negativeValue +
                ", veryNegativeValue=" + veryNegativeValue +
                '}';
    }

    public String getSentence() {
        return sentence;
    }

    public double getVeryPositiveValue() {
        return veryPositiveValue;
    }

    public double getPositiveValue() {
        return positiveValue;
    }

    public double getNeutralValue() {
        return neutralValue;
    }

    public double getNegativeValue() {
        return negativeValue;
    }

    public double getVeryNegativeValue() {
        return veryNegativeValue;
    }
}
