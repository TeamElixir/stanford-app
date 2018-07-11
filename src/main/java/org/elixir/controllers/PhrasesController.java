package org.elixir.controllers;

import org.elixir.db.DBCon;
import org.elixir.db.Databases;
import org.elixir.models.Phrase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PhrasesController {

    public static boolean insertPhraseToDB(Phrase phrase) {
        Connection conn = DBCon.getConnection(Databases.TEN_CASE_ANALYSIS);
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
