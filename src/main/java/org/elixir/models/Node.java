package org.elixir.models;

public class Node {
	private long id;
	private long parent;
	private String argument;

	public Node() {

	}

	public Node(long id, long parent, String argument) {
		this.id = id;
		this.parent = parent;
		this.argument = argument;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getParent() {
		return parent;
	}

	public void setParent(long parent) {
		this.parent = parent;
	}

	public String getArgument() {
		return argument;
	}

	public void setArgument(String argument) {
		this.argument = argument;
	}
}
