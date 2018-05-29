package org.elixir.models;

public class Triple {

	public static final String TABLE_NAME = "triples";

	private int id;

	private int sentenceId;

	private String subject;

	private String relationship;

	private String object;

	public Triple() {

	}

	public Triple(int id, String subject, String relationship, String object, int sentenceId) {
		this.id = id;
		this.sentenceId = sentenceId;
		this.subject = subject;
		this.relationship = relationship;
		this.object = object;
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

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getSentenceId() {
		return sentenceId;
	}

	public void setSentenceId(int sentenceId) {
		this.sentenceId = sentenceId;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public void setObject(String object) {
		this.object = object;
	}
}
