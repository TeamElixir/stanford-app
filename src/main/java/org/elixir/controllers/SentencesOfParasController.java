package org.elixir.controllers;

import org.elixir.db.DBCon;
import org.elixir.models.SentenceOfPara;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SentencesOfParasController {
    private static Connection conn = DBCon.getConnection();

    public static boolean insertSentenceOfPara(SentenceOfPara sentenceOfPara) {
        String query = "INSERT INTO " + SentenceOfPara.TABLE_NAME + "  (paragraph_id, sentence)" +
                "values (?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, sentenceOfPara.getParagraphId());
            ps.setString(2, sentenceOfPara.getSentence());

            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<SentenceOfPara> getSentencesOfPara(int paragraphId) {
        ArrayList<SentenceOfPara> sentenceOfParas = new ArrayList<>();
        PreparedStatement ps;
        ResultSet rs;
        String query = "SELECT * FROM " + SentenceOfPara.TABLE_NAME + " WHERE paragraph_id = '" + paragraphId + "'";

        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String sentence = rs.getString("sentence");
                sentenceOfParas.add(new SentenceOfPara(id, paragraphId, sentence));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sentenceOfParas;
    }
}
