package org.elixir.models;

public class GoogleTriple {

	private int id;

	private String subject;

	private String relation;

	private String object;

	private int sentenceId;

	public GoogleTriple() {
	}

	public GoogleTriple(int id, String subject, String relation, String object, int sentenceId) {
		this.id = id;
		this.subject = subject;
		this.relation = relation;
		this.object = object;
		this.sentenceId = sentenceId;
	}

	public GoogleTriple(String subject, String relation, String object, int sentenceId) {
		this.subject = subject;
		this.relation = relation;
		this.object = object;
		this.sentenceId = sentenceId;
	}

	@Override
	public String toString() {
		return "GoogleTriple{" +
				"id=" + id +
				", subject='" + subject + '\'' +
				", relation='" + relation + '\'' +
				", object='" + object + '\'' +
				", sentenceId=" + sentenceId +
				'}';
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public int getSentenceId() {
		return sentenceId;
	}

	public void setSentenceId(int sentenceId) {
		this.sentenceId = sentenceId;
	}
}
