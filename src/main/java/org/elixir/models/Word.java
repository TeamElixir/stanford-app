package org.elixir.models;

public class Word {

	public static final String TABLE_NAME = "words";

	private int id;

	private int sentiment;

	private String word;

	private int tripleId;

	public Word() {

	}

	public Word(int id, String word, int sentiment, int tripleId) {
		this.id = id;
		this.sentiment = sentiment;
		this.word = word;
		this.tripleId = tripleId;
	}

	public Word(int sentiment, String word, int tripleId) {
		this.sentiment = sentiment;
		this.word = word;
		this.tripleId = tripleId;
	}

	public int getId() {
		return id;
	}

	public int getSentiment() {
		return sentiment;
	}

	public String getWord() {
		return word;
	}

	public int getTripleId() {
		return tripleId;
	}
}
