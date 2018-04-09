package org.elixir.models;

public class Triple {

	public static final String TABLE_NAME = "triples";

	private int id;

	private int sentenceId;

	private String subject;

	private String relationship;

	private String object;

	private Triple() {

	}

	public Triple(String subject, String relationship, String object, int sentenceId) {
		this.subject = subject;
		this.relationship = relationship;
		this.object = object;
		this.sentenceId = sentenceId;
	}

	@Override
	public String toString() {
		return "(" + subject + ", " + relationship + ", " + object + ")";
	}

	public String getSubject() {
		return subject;
	}

	public String getRelationship() {
		return relationship;
	}

	public String getObject() {
		return object;
	}

	public int getId() {
		return id;
	}

	public int getSentenceId() {
		return sentenceId;
	}
}
