package org.elixir.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Node {

	@JsonProperty("key")
	private String id;

	@JsonProperty("parent")
	private String parent;

	@JsonProperty("name")
	private String argument;

	@JsonProperty("type")
	private String type;

	// default constructor needed for parsing JSON
	public Node() {

	}

	public Node(String id, String parent, String argument) {
		this.id = id;
		this.parent = parent;
		this.argument = argument;
	}

	@Override
	public String toString() {
		return "Node{" +
				"id='" + id + '\'' +
				", parent='" + parent + '\'' +
				", argument='" + argument + '\'' +
				", type='" + type + '\'' +
				'}';
	}

	public void setType(String type) {
		this.type = type;
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
