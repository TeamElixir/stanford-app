package org.elixir.nodeStructure.models;

public class Node implements Comparable<Node> {
    private String name;

    public Node(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                '}';
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
