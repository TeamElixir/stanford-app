package org.elixir.models;

import java.util.ArrayList;

public class Case {

	private ArrayList<String> sentences;

	private String title;

	private String fileName;

	public Case() {
		sentences = new ArrayList<>();
	}

	public Case(ArrayList<String> sentences, String title, String fileName) {
		this.sentences = sentences;
		this.title = title;
		this.fileName = fileName;
		sentences = new ArrayList<>();
	}

	public ArrayList<String> getSentences() {
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
