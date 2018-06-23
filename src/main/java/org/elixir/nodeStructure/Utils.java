package org.elixir.nodeStructure;

import org.elixir.nodeStructure.models.Edge;
import org.elixir.nodeStructure.models.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Utils {
    public static void main(String[] args) {

    }

    public static ArrayList<Edge> getAllEdges() {
        ArrayList<Edge> edges = new ArrayList<>();

        BufferedReader br = null;
        FileReader fr = null;
        String absoluteFilePath = new File("").getAbsolutePath();
        String path = absoluteFilePath + "/src/main/resources/SentimentAnalysis/InnerOuterSentenceSentiment/case_11/one_negative.txt";
        try {
            fr = new FileReader(new File(path));
            br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                String twoParties = line.substring(0, line.indexOf("("));
                String allCoref = line.substring(line.indexOf("("), line.indexOf(")"));

                String[] partySplits = twoParties.split(":");
                String one = partySplits[0].trim();
                String two = partySplits[1].trim();
                Node n1 = new Node(one);
                Node n2 = new Node(two);

                String[] corefSplits = allCoref.split(":");
                ArrayList<String> oneCorefs = new ArrayList<>(Arrays.asList(corefSplits[0].trim().split(";")));
                ArrayList<String> twoCorefs = new ArrayList<>(Arrays.asList(corefSplits[1].trim().split(";")));
                n1.setCorefs(oneCorefs);
                n2.setCorefs(twoCorefs);

                Edge e = new Edge(0, n1, n2);
                edges.add(e);

                line = br.readLine();

//                System.out.println(one + ", " + two);
//                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return edges;
    }   // getAllEdges()
}
