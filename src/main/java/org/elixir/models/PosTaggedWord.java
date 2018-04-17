package org.elixir.models;

public class PosTaggedWord {

	public static final String TABLE_NAME = "pos_tagged_words";

	private int id;

	private String word;

	private String posTag;

	private int sentenceId;

	public PosTaggedWord() {
	}

	public PosTaggedWord(String word, String posTag, int sentenceId) {
		this.word = word;
		this.posTag = posTag;
		this.sentenceId = sentenceId;
	}

	public int getId() {
		return id;
	}

	public String getWord() {
		return word;
	}

	public String getPosTag() {
		return posTag;
	}

	public int getSentenceId() {
		return sentenceId;
	}
}
