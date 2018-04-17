package org.elixir.models;

import java.util.ArrayList;

public class Case {

	private ArrayList<Sentence> sentences;

	private String title;

	private String fileName;

	public Case() {
		sentences = new ArrayList<>();
	}

	public Case(ArrayList<Sentence> sentences, String title, String fileName) {
		this.sentences = sentences;
		this.title = title;
		this.fileName = fileName;
		sentences = new ArrayList<>();
	}

	public void setSentences(ArrayList<Sentence> sentences) {
		this.sentences = sentences;
	}

	public ArrayList<Sentence> getSentences() {
		return sentences;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
