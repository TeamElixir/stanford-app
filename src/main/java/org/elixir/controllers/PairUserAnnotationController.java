package org.elixir.controllers;

import org.elixir.db.DBCon;
import org.elixir.db.Databases;
import org.elixir.models.PairUserAnnotation;
import org.elixir.models.Sentence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PairUserAnnotationController {
    private static final Connection con = DBCon.getConnection(Databases.DISCOURSE_ANNOTATOR);


    public static ArrayList<PairUserAnnotation> getAllPairUserAnnotations() {
        ArrayList<PairUserAnnotation> pairUserAnnotationArrayList = new ArrayList<>();
        ResultSet resultSet;
        String query = "SELECT * FROM " + PairUserAnnotation.TABLE_NAME;
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int pair_id = resultSet.getInt("pair_id");
                int user_id = resultSet.getInt("user_id");
                int relation = resultSet.getInt("relation");

                PairUserAnnotation pairUserAnnotation = new PairUserAnnotation(id, pair_id, user_id, relation);

                pairUserAnnotationArrayList.add(pairUserAnnotation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pairUserAnnotationArrayList;
    }

}
