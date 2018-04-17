package org.elixir.controllers;

import org.elixir.db.DBCon;
import org.elixir.models.Word;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WordController {

	private static Connection conn = DBCon.getConnection();

	public static boolean insertWord(Word word) {
		String query = "INSERT INTO " + Word.TABLE_NAME + "(word, sentiment, triple_id) VALUES(?, ?, ?)";

		try {
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, word.getWord());
			ps.setInt(2, word.getSentiment());
			ps.setInt(3, word.getTripleId());
			ps.execute();
			return true;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
