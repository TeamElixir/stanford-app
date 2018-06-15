package org.elixir.controllers;

import org.elixir.db.DBCon;
import org.elixir.models.CorefChainOfPara;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CorefChainOfParasController {
    private static Connection conn = DBCon.getConnection();

    public static boolean insertCorefChainOfPara(CorefChainOfPara chainOfPara) {
        String query = "INSERT INTO " + CorefChainOfPara.TABLE_NAME + "  (paragraph_id, coref_chain)" +
                "values (?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, chainOfPara.getParagraphId());
            ps.setString(2, chainOfPara.getCorefChain());

            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<CorefChainOfPara> getCorefChainsOfPara(int paragraphId) {
        ArrayList<CorefChainOfPara> chainOfParas = new ArrayList<>();
        PreparedStatement ps;
        ResultSet rs;
        String query = "SELECT * FROM " + CorefChainOfPara.TABLE_NAME + " WHERE paragraph_id = '" + paragraphId + "'";

        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String corefChain = rs.getString("coref_chain");
                chainOfParas.add(new CorefChainOfPara(id, paragraphId, corefChain));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chainOfParas;
    }
}
