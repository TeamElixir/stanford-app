package org.elixir.db;

import org.elixir.models.Sentence;
import org.elixir.models.Triple;
import org.elixir.models.Word;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Controller {

    private static Connection conn = DBCon.getConnection();

    private static ResultSet resultSet;

    private static int getLatestId(String tableName) {
        String query = "SELECT last_insert_id() from " + tableName;
        int id = -1;
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt("last_insert_id()");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;

    }

    public static int getLatestWordId() {
        return getLatestId(Word.TABLE_NAME);
    }

    public static int getLatestTripleId() {
        return getLatestId(Triple.TABLE_NAME);
    }

    public static int getLastestSentenceId() {
        return getLatestId(Sentence.TABLE_NAME);
    }

}
