package org.elixir.nodeStructure;

import org.elixir.nodeStructure.models.Edge;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ArrayList<Edge> allEdges = Utils.getAllEdges();

        System.out.println("Initial size: " + allEdges.size());

        for (int i = 0; i < allEdges.size(); i++) {
            if (allEdges.get(i) == null) {
                continue;
            }

            Edge currentEdge = allEdges.get(i);
            for (int j = i + 1; j < allEdges.size(); ) {
                if (allEdges.get(j) == null) {
                    j++;
                    continue;
                }
                if (currentEdge.compareTo(allEdges.get(j)) == 0) {
                    // the two edges are equal
                    currentEdge.incrementCount();
                    allEdges.set(j, null);
                } else {
                    j++;
                }
            }
        }

        int count = 0;
        for (int i = 0; i < allEdges.size(); i++) {
            if (allEdges.get(i) != null) {
                count++;
            }
        }

        System.out.println("Size after removing duplicates: " + count);
        System.out.println();

        for (Edge e : allEdges) {
            if (e != null) {
                System.out.println(e);
                ArrayList<String> corefs = e.getNodeOne().getCorefs();
                for (String coref : corefs) {
                    System.out.println(coref);
                }
            }
        }

    }

    private static void printEdgesForPythonGraph(ArrayList<Edge> edges) {
        for (Edge e : edges) {
            if (e != null) {
                System.out.println("G.add_edge(\"" + e.getNodeOne().getName() + "\", \""
                        + e.getNodeTwo().getName() + "\", weight=" + e.getCount() + ")");
            }
        }
    }
}
