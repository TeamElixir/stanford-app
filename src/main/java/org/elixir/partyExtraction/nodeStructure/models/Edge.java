package org.elixir.partyExtraction.nodeStructure.models;

public class Edge implements Comparable<Edge> {
    private int count;
    private Node nodeOne;
    private Node nodeTwo;

    public Edge(int count, Node nodeOne, Node nodeTwo) {
        this.count = count;
        this.nodeOne = nodeOne;
        this.nodeTwo = nodeTwo;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "count=" + count +
                ", nodeOne=" + nodeOne +
                ", nodeTwo=" + nodeTwo +
                '}';
    }

    public void incrementCount() {
        this.count++;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Node getNodeOne() {
        return nodeOne;
    }

    public void setNodeOne(Node nodeOne) {
        this.nodeOne = nodeOne;
    }

    public Node getNodeTwo() {
        return nodeTwo;
    }

    public void setNodeTwo(Node nodeTwo) {
        this.nodeTwo = nodeTwo;
    }

    @Override
    public int compareTo(Edge o) {
        if (this.nodeOne.compareTo(o.getNodeOne()) == 0 && this.nodeTwo.compareTo(o.getNodeTwo()) == 0) {
            return 0;
        } else {
            return -1;
        }
    }
}
