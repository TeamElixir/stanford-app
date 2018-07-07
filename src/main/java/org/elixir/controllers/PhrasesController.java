package org.elixir.controllers;

import org.elixir.db.DBCon;
import org.elixir.db.Databases;
import org.elixir.models.Phrase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PhrasesController {
    private static Connection conn = DBCon.getConnection(Databases.DISCOURSE_ANNOTATOR);

    public static boolean insertPhraseToDB(Phrase phrase) {
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
