package org.elixir.partyExtraction.nodeStructure.models;

import java.util.ArrayList;

public class Node implements Comparable<Node> {
    private String name;
    private ArrayList<String> corefs;

    public Node(String name) {
        this.name = name;
        this.corefs = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                '}';
    }

    public void setCorefs(ArrayList<String> corefs) {
        this.corefs = corefs;
    }

    public ArrayList<String> getCorefs() {
        return corefs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Node o) {
        return this.name.compareTo(o.getName());
    }
}
