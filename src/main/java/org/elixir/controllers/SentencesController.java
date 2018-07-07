package org.elixir.controllers;

import org.elixir.db.DBCon;
import org.elixir.db.Databases;
import org.elixir.models.Sentence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SentencesController {

    private static Connection conn = DBCon.getConnection(Databases.TEN_CASE_ANALYSIS);

    /*
      11 <= n <= 21 (for now)
     */
    public static ArrayList<Sentence> getSentencesOfCase(int n) {
        ResultSet rs;
        ArrayList<Sentence> sentences = new ArrayList<>();
        final String caseFileName = "criminal_triples/case_" + n + ".txt";
        String query = "SELECT * FROM " + Sentence.TABLE_NAME + " WHERE file='" + caseFileName + "'";
        System.out.println(query);
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String file = rs.getString("file");
                String rawSentence = rs.getString("sentence");

                Sentence s = new Sentence(id, file, rawSentence);
                s.setTriples(TriplesController.getTriplesOfSentence(s.getId()));
                s.setGoogleTriples(TriplesController.getGoogleTriplesOfSentence(s.getId()));
                s.setPosTaggedWords(PosTaggedWordsController.getPosTaggedWordsOfSentence(s.getId()));

                sentences.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return sentences;
    }

    public static ArrayList<Sentence> getAllSentences() {
        ArrayList<Sentence> sentences = new ArrayList<>();
        ResultSet resultSet;
        String query = "SELECT * FROM " + Sentence.TABLE_NAME;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String file = resultSet.getString("file");
                String rawSentence = resultSet.getString("sentence");

                Sentence s = new Sentence(id, file, rawSentence);
                s.setTriples(TriplesController.getTriplesOfSentence(s.getId()));
                s.setGoogleTriples(TriplesController.getGoogleTriplesOfSentence(s.getId()));
                s.setPosTaggedWords(PosTaggedWordsController.getPosTaggedWordsOfSentence(s.getId()));

                sentences.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sentences;
    }

    public static boolean insertSentence(Sentence sentence) {
        String query = "INSERT INTO " + Sentence.TABLE_NAME + "(file, sentence) VALUES(?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, sentence.getFile());
            ps.setString(2, sentence.getSentence());
            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    // get sentences of a given case (sentences are sanitized and stored in the table discourse-annotator.sentences
    public static ArrayList<Sentence> getSanitizedSentencesOfCase(int n) {
        Connection connection = DBCon.getConnection(Databases.DISCOURSE_ANNOTATOR);
        ResultSet rs;
        ArrayList<Sentence> sentences = new ArrayList<>();
        String fileName = "cases_set/case_" + n + ".txt";  // in database

        String query = "SELECT * FROM " + Sentence.TABLE_NAME + " WHERE file='" + fileName + "'";
        System.out.println(query);
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String file = rs.getString("file");
                String rawSentence = rs.getString("sentence");

                Sentence s = new Sentence(id, file, rawSentence);

                sentences.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return sentences;
    }
}
