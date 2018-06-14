package org.elixir.controllers;

import org.elixir.db.DBCon;
import org.elixir.models.PosTaggedWord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PosTaggedWordsController {

    private static Connection conn = DBCon.getConnection();

    public static boolean insertPosTaggedWord(PosTaggedWord posTaggedWord) {
        String query = "INSERT INTO " + PosTaggedWord.TABLE_NAME + " (word, pos_tag, ner_tag, sentence_id)"
                + " VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, posTaggedWord.getWord());
            ps.setString(2, posTaggedWord.getPosTag());
            ps.setString(3, posTaggedWord.getNerTag());
            ps.setInt(4, posTaggedWord.getSentenceId());

            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<PosTaggedWord> getPosTaggedWordsOfSentence(int sentenceId) {
        ArrayList<PosTaggedWord> posTaggedWords = new ArrayList<>();
        PreparedStatement ps;
        ResultSet rs;
        String query = "SELECT * FROM " + PosTaggedWord.TABLE_NAME + " WHERE sentence_id='" + sentenceId + "'";

        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String word = rs.getString("word");
                String posTag = rs.getString("pos_tag");
                String nerTag = rs.getString("ner_tag");
                int sId = rs.getInt("sentence_id");

                PosTaggedWord posTaggedWord = new PosTaggedWord(id, word, posTag, nerTag, sId);
                posTaggedWords.add(posTaggedWord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return posTaggedWords;
    }
}
