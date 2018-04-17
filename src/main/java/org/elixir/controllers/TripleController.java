package org.elixir.controllers;

import org.elixir.db.DBCon;
import org.elixir.models.Triple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TripleController {

	private static Connection conn = DBCon.getConnection();

	private static ResultSet resultSet;

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
}
