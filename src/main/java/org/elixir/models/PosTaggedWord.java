package org.elixir.models;

public class PosTaggedWord {

	public static final String TABLE_NAME = "pos_tagged_words";

	private int id;

	private String word;

	private String posTag;

	private String nerTag;

	private int sentenceId;

	public PosTaggedWord() {
	}

	public PosTaggedWord(String word, String posTag, String nerTag, int sentenceId) {
		this.word = word;
		this.posTag = posTag;
		this.nerTag = nerTag;
		this.sentenceId = sentenceId;
	}

	@Override
	public String toString() {
		return "PosTaggedWord{" +
				"word='" + word + '\'' +
				", posTag='" + posTag + '\'' +
				", nerTag='" + nerTag + '\'' +
				", sentenceId=" + sentenceId +
				'}';
	}

	public int getId() {
		return id;
	}

	public String getWord() {
		return word;
	}

	public String getNerTag() {
		return nerTag;
	}

	public String getPosTag() {
		return posTag;
	}

	public int getSentenceId() {
		return sentenceId;
	}
}
