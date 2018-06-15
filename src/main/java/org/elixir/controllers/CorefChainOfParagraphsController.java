package org.elixir.controllers;

import org.elixir.db.DBCon;
import org.elixir.models.CorefChainOfParagraph;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CorefChainOfParagraphsController {
    private static Connection conn = DBCon.getConnection();

    public static boolean insertCorefChainOfParagraphs(CorefChainOfParagraph chainOfParagraph) {
        String query = "INSERT INTO " + CorefChainOfParagraph.TABLE_NAME + "  (paragraph_id, coref_chain)" +
                "values (?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, chainOfParagraph.getParagraphId());
            ps.setString(2, chainOfParagraph.getCorefChain());

            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<CorefChainOfParagraph> getCorefChainsOfPara(int paragraphId) {
        ArrayList<CorefChainOfParagraph> chainOfParagraphs = new ArrayList<>();
        PreparedStatement ps;
        ResultSet rs;
        String query = "SELECT * FROM " + CorefChainOfParagraph.TABLE_NAME + " WHERE paragraph_id = '" + paragraphId + "'";

        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String corefChain = rs.getString("coref_chain");
                chainOfParagraphs.add(new CorefChainOfParagraph(id, paragraphId, corefChain));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chainOfParagraphs;
    }
}
