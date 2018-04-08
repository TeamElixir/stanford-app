package org.elixir.db;

import org.elixir.model.Sentence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Controller {

	private static Connection conn = DBCon.getConnection();
	private static ResultSet resultSet;
	private static ArrayList<Sentence> sentences = new ArrayList<>();

	public static void main(String[] args) {
		getAllSentences();
	}

	public static void getAllSentences() {
		String query = "SELECT * from " + Sentence.TABLE_NAME;
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();

			while(resultSet.next()) {
				int id = resultSet.getInt("id");
				String subject = resultSet.getString("subject");
				String relation = resultSet.getString("relation");
				String object = resultSet.getString("object");
				String file = resultSet.getString("file");
				String sentence = resultSet.getString("sentence");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
