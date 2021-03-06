package org.elixir.controllers;

import org.elixir.db.DBCon;
import org.elixir.db.Databases;
import org.elixir.models.Word;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WordsController {

    public static boolean insertWord(Word word) {
        Connection conn = DBCon.getConnection(Databases.TEN_CASE_ANALYSIS);
        String query = "INSERT INTO " + Word.TABLE_NAME + "(word, sentiment, triple_id) VALUES(?, ?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, word.getWord());
            ps.setInt(2, word.getSentiment());
            ps.setInt(3, word.getTripleId());
            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // words related to sentences
    public static ArrayList<Word> getAllWords() {
        Connection conn = DBCon.getConnection(Databases.TEN_CASE_ANALYSIS);
        ArrayList<Word> words = new ArrayList<>();
        PreparedStatement ps;
        ResultSet rs;

        String query = "SELECT * FROM " + Word.TABLE_NAME;

        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String word = rs.getString("word");
                int sentiment = rs.getInt("sentiment");
                int tripleId = rs.getInt("triple_id");

                Word w = new Word(id, word, sentiment, tripleId);
                words.add(w);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return words;
    }
}
