package org.elixir.controllers;

import org.elixir.db.DBCon;
import org.elixir.db.Databases;
import org.elixir.models.SentencePair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SentencePairsController {
    private static Connection conn = DBCon.getConnection(Databases.DISCOURSE_ANNOTATOR);

    public static ArrayList<SentencePair> getAllSentencePairs() {
        ArrayList<SentencePair> sentencePairs = new ArrayList<>();
        ResultSet resultSet;
        String query = "SELECT * FROM " + SentencePair.TABLE_NAME;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int source_sntc_id = resultSet.getInt("source_sntc_id");
                int target_sntc_id = resultSet.getInt("target_sntc_id");
                int relation = resultSet.getInt("relation");

                SentencePair pairUserAnnotation = new SentencePair(id, source_sntc_id, target_sntc_id, relation);

                sentencePairs.add(pairUserAnnotation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sentencePairs;
    }

    public static SentencePair getSentencerPairById(int pairId) {
        ResultSet resultSet;
        String query = "SELECT * from " + SentencePair.TABLE_NAME + " WHERE ID=" + pairId;
        SentencePair sentencePair = null;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int sourceSntcId = resultSet.getInt("source_sntc_id");
                int targetSntcId = resultSet.getInt("target_sntc_id");
                int relation = resultSet.getInt("relation");
                sentencePair = new SentencePair(id, sourceSntcId, targetSntcId, relation);
                sentencePair.setSourceSntc(SentencesController.getSentenceById(sourceSntcId));
                sentencePair.setTargetSntc(SentencesController.getSentenceById(targetSntcId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sentencePair;
    }
}
