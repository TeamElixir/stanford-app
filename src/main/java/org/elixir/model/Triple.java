package org.elixir.model;

public class Triple {
	private String subject;
	private String relation;
	private String object;

	private Triple() {

	}

	public Triple(String subject, String relation, String object) {
		this.subject = subject;
		this.relation = relation;
		this.object = object;
	}

	@Override
	public String toString() {
		return "("+ subject + ", " + relation + ", " + object +")";
	}

	public String getSubject() {
		return subject;
	}

	public String getRelation() {
		return relation;
	}

	public String getObject() {
		return object;
	}
}
