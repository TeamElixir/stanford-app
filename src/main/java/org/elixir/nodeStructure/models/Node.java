package org.elixir.nodeStructure.models;

import java.util.ArrayList;

public class Node {
    private String name;
    private ArrayList<Edge> edges;

    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                ", edges=" + edges +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
