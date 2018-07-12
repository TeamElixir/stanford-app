package org.elixir.controllers;

import org.elixir.db.DBCon;
import org.elixir.db.Databases;
import org.elixir.models.Phrase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PhrasesController {

    public static boolean updateSentimentOfPhrase(Phrase phrase) {
        Connection conn = DBCon.getConnection(Databases.SENTIMENT_ANNOTATOR);
        String query = "UPDATE " + Phrase.TABLE_NAME +
                " SET sentiment = '" + phrase.getSentiment() +
                "' WHERE id = " + phrase.getId();

        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            System.out.println("Query: " + query);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ArrayList<Phrase> getAllPhrases() {
        Connection conn = DBCon.getConnection(Databases.SENTIMENT_ANNOTATOR);
        String query = "SELECT * FROM " + Phrase.TABLE_NAME;

        ArrayList<Phrase> phrases = new ArrayList<>();
        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String phrase = rs.getString("phrase");
                String sentiment = rs.getString("sentiment");
                String caseFileName = rs.getString("caseFileName");

                Phrase p = new Phrase();

                p.setId(id);
                p.setPhrase(phrase);
                p.setSentiment(sentiment);
                p.setCaseFileName(caseFileName);

                phrases.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return phrases;
    }

    public static boolean insertFilteredPhrasesToBD(Phrase phrase) {
        Connection conn = DBCon.getConnection(Databases.SENTIMENT_ANNOTATOR);
        String query = "";
        PreparedStatement ps = null;
        try {
            query = "INSERT INTO " + Phrase.TABLE_NAME + " (phrase) VALUES (?)";
            ps = conn.prepareStatement(query);
            ps.setString(1, phrase.getPhrase());

            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean insertPhraseToDB(Phrase phrase) {
        Connection conn = DBCon.getConnection(Databases.SENTIMENT_ANNOTATOR);
        String query = "INSERT INTO " + Phrase.TABLE_NAME + " (phrase, sentiment, caseFileName) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, phrase.getPhrase());
            ps.setString(2, phrase.getSentiment());
            ps.setString(3, phrase.getCaseFileName());

            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
