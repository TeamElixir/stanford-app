package org.elixir.controllers;

import org.elixir.db.DBCon;
import org.elixir.db.Databases;
import org.elixir.models.Term;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TermsController {


    public static ArrayList<Term> getAllTerms() {
        Connection conn = DBCon.getConnection(Databases.TERM_FREQUENCIES);
        String query = "SELECT * FROM " + Term.TABLE_NAME;

        ArrayList<Term> terms = new ArrayList<>();
        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String word = rs.getString("word");
                int frequency = rs.getInt("frequency");
                String sentiment = rs.getString("sentiment");

                Term term = new Term(id, word, frequency, sentiment);
                terms.add(term);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return terms;
    }

    public static ArrayList<Term> getTermsWithASentiment() {
        Connection conn = DBCon.getConnection(Databases.TERM_FREQUENCIES);
        String query = "SELECT * FROM " + Term.TABLE_NAME + " WHERE sentiment IS NULL";

        ArrayList<Term> terms = new ArrayList<>();
        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String word = rs.getString("word");
                int frequency = rs.getInt("frequency");
                String sentiment = rs.getString("sentiment");

                Term term = new Term(id, word, frequency, sentiment);
                terms.add(term);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return terms;
    }

    public static boolean updateSentimentOfTerm(Term term) {
        Connection conn = DBCon.getConnection(Databases.TERM_FREQUENCIES);
        System.out.println("Sentiment: " + term.getSentiment());
        String query = "UPDATE " + Term.TABLE_NAME +
                " SET sentiment = '" + term.getSentiment() +
                "' WHERE id = " + term.getId();

        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
//            ps.setString(1, term.getSentiment());
//            ps.setInt(2, term.getId());
            System.out.println("Query: " + query);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
