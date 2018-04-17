package org.elixir.controllers;

import org.elixir.db.DBCon;
import org.elixir.models.PosTaggedWord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PosTaggedWordController {

	private static Connection conn = DBCon.getConnection();

	private static ResultSet resultSet;

	public static boolean insertPosTaggedWord(PosTaggedWord posTaggedWord) {
		String query = "INSERT INTO " + PosTaggedWord.TABLE_NAME + "(word, pos_tag, ner_tag, sentence_id)"
				+ " VALUES (?, ?, ?, ?)";

		try {
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, posTaggedWord.getWord());
			ps.setString(2, posTaggedWord.getPosTag());
			ps.setString(3, posTaggedWord.getNerTag());
			ps.setInt(4, posTaggedWord.getSentenceId());

			ps.execute();
			return true;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
