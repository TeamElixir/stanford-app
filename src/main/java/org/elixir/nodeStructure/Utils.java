package org.elixir.nodeStructure;

import org.elixir.nodeStructure.models.Edge;
import org.elixir.nodeStructure.models.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
                String wholeString = line.substring(0, line.indexOf("("));
                String[] splits = wholeString.split(":");
                String one = splits[0].trim();
                String two = splits[1].trim();

                Edge e = new Edge(0, new Node(one), new Node(two));
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
