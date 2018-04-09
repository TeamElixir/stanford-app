package org.elixir.db;

import org.elixir.models.Sentence;
import org.elixir.models.Triple;
import org.elixir.models.Word;

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

			while (resultSet.next()) {
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

	private static int getLatestId(String tableName) {
		String query = "SELECT last_insert_id() from " + tableName;
		int id = -1;
		try {
			PreparedStatement ps = conn.prepareStatement(query);
			resultSet = ps.executeQuery();
			if(resultSet.next()) {
				id = resultSet.getInt("last_insert_id()");
			}
		}
		catch (SQLException e) {
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

	public static boolean insertWord(Word word) {
		String query = "INSERT INTO " + Word.TABLE_NAME + "(word, sentiment, triple_id) values(?, ?, ?)";

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

	public static boolean insertSentence(Sentence sentence) {
		String query = "INSERT INTO " + Sentence.TABLE_NAME + "(file, sentence) values(?, ?)";

		try {
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, sentence.getFile());
			ps.setString(2, sentence.getSentence());
			ps.execute();
			return  true;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public static boolean insertTriple(Triple triple) {
		String query = "INSERT INTO " + Triple.TABLE_NAME + "(subject, relationship, object, sentence_id)"
				+ " values (?, ?, ?, ?)";

		try {
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, triple.getSubject());
			ps.setString(2, triple.getRelationship());
			ps.setString(3, triple.getObject());
			ps.setInt(4, triple.getSentenceId());

			ps.execute();
			return true;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
