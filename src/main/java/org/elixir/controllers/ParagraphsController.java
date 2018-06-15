package org.elixir.controllers;

import org.elixir.db.DBCon;
import org.elixir.models.Paragraph;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ParagraphsController {
    private static Connection conn = DBCon.getConnection();

    public static boolean insertParagraph(Paragraph paragraph) {
        String query = "INSERT INTO " + Paragraph.TABLE_NAME + "  (case_number, paragraph)" +
                "values (?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, paragraph.getCaseNumber());
            ps.setString(2, paragraph.getText());

            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Paragraph> getParagraphsOfCase(int n) {
        ArrayList<Paragraph> paragraphs = new ArrayList<>();
        PreparedStatement ps;
        ResultSet rs;
        String query = "SELECT * FROM " + Paragraph.TABLE_NAME + " WHERE case_number = '" + n + "'";

        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int caseNumber = rs.getInt("case_number");
                String paragraph = rs.getString("paragraph");
                Paragraph p = new Paragraph(id, caseNumber, paragraph);
                p.setCorefChains(CorefChainOfParagraphsController.getCorefChainsOfParagraph(p.getId()));
                p.setSentences(SentencesOfParagraphsController.getSentencesOfParagraph(p.getId()));
                paragraphs.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return paragraphs;
    }
}
