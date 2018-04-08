package org.elixir.model;

import java.util.ArrayList;

public class Sentence {

	public static final String TABLE_NAME = "sentences";

	private int id;

	private ArrayList<Triple> triples;

	private String sentence;

	public Sentence() {
		triples = new ArrayList<>();
	}

	public Sentence(int id, String sentence) {
		this.sentence = sentence;
		triples = new ArrayList<>();
	}

	public void addTriple(Triple triple) {
		triples.add(triple);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Triple> getTriples() {
		return triples;
	}

	public String getSentence() {
		return sentence;
	}
}
