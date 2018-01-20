package org.elixir.models;

public class Node {
	private String id;
	private String parent;
	private String argument;

	public Node() {

	}

	@Override
	public String toString() {
		return "Node{" +
				"id='" + id + '\'' +
				", parent='" + parent + '\'' +
				", argument='" + argument + '\'' +
				'}';
	}

	public Node(String id, String parent, String argument) {
		this.id = id;
		this.parent = parent;
		this.argument = argument;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getArgument() {
		return argument;
	}

	public void setArgument(String argument) {
		this.argument = argument;
	}
}
