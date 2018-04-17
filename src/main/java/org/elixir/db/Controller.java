package org.elixir.db;

import org.elixir.models.PosTaggedWord;
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
		ArrayList<Triple> allTriples = getAllTriples();
		int i = 0;
		for (Triple triple : allTriples) {
			System.out.println(++i + " " + triple);
		}
	}

	public static ArrayList<Triple> getAllTriples() {
		ArrayList<Triple> triples = new ArrayList<>();
		String query = "SELECT * FROM " + Triple.TABLE_NAME;
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String subject = resultSet.getString("subject");
				String relationship = resultSet.getString("relationship");
				String object = resultSet.getString("object");

				Triple triple = new Triple();
				triple.setId(id);
				triple.setSubject(subject);
				triple.setRelationship(relationship);
				triple.setObject(object);

				triples.add(triple);
			}

			return triples;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ArrayList<Sentence> getAllSentences() {
		ArrayList<Sentence> sentences = new ArrayList<>();
		String query = "SELECT * FROM " + Sentence.TABLE_NAME;
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String file = resultSet.getString("file");
				String sentence = resultSet.getString("sentence");

				Sentence s = new Sentence(file, sentence);
				sentences.add(s);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return sentences;
	}

	private static int getLatestId(String tableName) {
		String query = "SELECT last_insert_id() from " + tableName;
		int id = -1;
		try {
			PreparedStatement ps = conn.prepareStatement(query);
			resultSet = ps.executeQuery();
			if (resultSet.next()) {
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

	public static boolean insertSentence(Sentence sentence) {
		String query = "INSERT INTO " + Sentence.TABLE_NAME + "(file, sentence) VALUES(?, ?)";

		try {
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, sentence.getFile());
			ps.setString(2, sentence.getSentence());
			ps.execute();
			return true;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public static boolean insertTriple(Triple triple) {
		String query = "INSERT INTO " + Triple.TABLE_NAME + "(subject, relationship, object, sentence_id)"
				+ " VALUES (?, ?, ?, ?)";

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

	public static boolean insertPosTaggedWord(PosTaggedWord posTaggedWord) {
		String query = "INSERT INTO " + PosTaggedWord.TABLE_NAME + "(word, pos_tag, sentence_id)"
				+ " VALUES (?, ?, ?)";

		try {
			PreparedStatement ps = conn.prepareStatement(query);

			return true;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
