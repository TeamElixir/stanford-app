package org.elixir.controllers;

import org.elixir.db.DBCon;
import org.elixir.models.GoogleTriple;
import org.elixir.models.Triple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TriplesController {

    private static Connection conn = DBCon.getConnection();

    public static ArrayList<Triple> getAllTriples() {
        ResultSet resultSet;
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
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Triple> getTriplesOfSentence(int sentenceId) {
        ArrayList<Triple> triples = new ArrayList<>();
        ResultSet rs;
        PreparedStatement ps;
        String query = "SELECT * FROM " + Triple.TABLE_NAME + " WHERE sentence_id='" + sentenceId + "'";

        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String subject = rs.getString("subject");
                String relation = rs.getString("relation");
                String object = rs.getString("object");
                int sId = rs.getInt("sentence_id");

                Triple triple = new Triple(id, subject, relation, object, sId);

                triples.add(triple);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return triples;

    }


    public static ArrayList<GoogleTriple> getGoogleTriplesOfSentence(int sentenceId) {
        ArrayList<GoogleTriple> triples = new ArrayList<>();
        ResultSet rs;
        PreparedStatement ps;
        String query = "SELECT * FROM " + Triple.TABLE_NAME + " WHERE sentence_id='" + sentenceId + "'";

        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String subject = rs.getString("subject");
                String relation = rs.getString("relation");
                String object = rs.getString("object");
                int sId = rs.getInt("sentence_id");

                GoogleTriple triple = new GoogleTriple(id, subject, relation, object, sId);

                triples.add(triple);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return triples;

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
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
