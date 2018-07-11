package org.elixir.controllers;

import org.elixir.db.DBCon;
import org.elixir.db.Databases;
import org.elixir.models.SentenceOfParagraph;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SentencesOfParagraphsController {
    private static Connection conn = DBCon.getConnection(Databases.TEN_CASE_ANALYSIS);

    public static boolean insertSentenceOfParagraph(SentenceOfParagraph sentenceOfParagraph) {
        String query = "INSERT INTO " + SentenceOfParagraph.TABLE_NAME + "  (paragraph_id, sentence)" +
                "values (?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, sentenceOfParagraph.getParagraphId());
            ps.setString(2, sentenceOfParagraph.getSentence());

            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<SentenceOfParagraph> getSentencesOfParagraph(int paragraphId) {
        ArrayList<SentenceOfParagraph> sentencesOfParagraph = new ArrayList<>();
        PreparedStatement ps;
        ResultSet rs;
        String query = "SELECT * FROM " + SentenceOfParagraph.TABLE_NAME + " WHERE paragraph_id = '" + paragraphId + "'";

        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String sentence = rs.getString("sentence");
                sentencesOfParagraph.add(new SentenceOfParagraph(id, paragraphId, sentence));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sentencesOfParagraph;
    }
}
