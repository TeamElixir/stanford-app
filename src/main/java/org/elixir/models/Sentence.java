package org.elixir.models;

import java.util.ArrayList;

public class Sentence {

	public static final String TABLE_NAME = "sentences";

	private int id;

	private ArrayList<Triple> triples;

	private String sentence;

	private String file;

	public Sentence() {
		triples = new ArrayList<>();
	}

	public Sentence(int id, String file, String sentence) {
		this.id = id;
		this.sentence = sentence;
		this.file = file;
		triples = new ArrayList<>();
	}

	public Sentence(String file, String sentence) {
		this.sentence = sentence;
		this.file = file;
		triples = new ArrayList<>();
	}

	@Override
	public String toString() {
		return "Sentence{" +
				"id=" + id +
				", sentence='" + sentence + '\'' +
				", file='" + file + '\'' +
				'}';
	}

	public String getFile() {
		return file;
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
