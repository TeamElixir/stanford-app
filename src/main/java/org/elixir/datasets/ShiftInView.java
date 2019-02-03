package org.elixir.datasets;

import org.elixir.controllers.SentencePairsController;
import org.elixir.db.DBCon;
import org.elixir.db.Databases;
import org.elixir.models.SentencePair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ShiftInView {
    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<LinResult> allLinResults = LinResultsController.getAllLinResults();
        assert allLinResults != null;
        PrintWriter pw = new PrintWriter(new File("shift_in_view_pairs.tsv"));
        StringBuilder sb = new StringBuilder();
        final String SEPARATOR = "\t";
        final String LITERAL_SHIFT_IN_VIEW = "Shift-in-View";
        sb.append("source_sentence");
        sb.append(SEPARATOR);
        sb.append("target_sentence");
        sb.append(SEPARATOR);
        sb.append("annotation");
        sb.append('\n');
        for (LinResult linResult : allLinResults) {
            SentencePair sentencePair = SentencePairsController.getSentencerPairById(linResult.getPairId());
            int shiftInView = linResult.getShiftInView();
            if (shiftInView == 1) {
                sb.append(sentencePair.getSourceSntc().getSentence());
                sb.append(SEPARATOR);
                sb.append(sentencePair.getTargetSntc().getSentence());
                sb.append(SEPARATOR);
                sb.append(LITERAL_SHIFT_IN_VIEW);
                sb.append('\n');
            }
        }

        pw.write(sb.toString());
        pw.close();
        System.out.println("done!");
    }

    /**
     * Model for table discourse_annotator.lin_results
     */
    private static class LinResult {
        public static final String TABLE_NAME = "lin_results";
        private int pairId;
        private int shiftInView;

        public LinResult() {

        }

        public LinResult(int pairId, int shiftInView) {
            this.pairId = pairId;
            this.shiftInView = shiftInView;
        }

        public int getPairId() {
            return pairId;
        }

        public void setPairId(int pairId) {
            this.pairId = pairId;
        }

        public int getShiftInView() {
            return shiftInView;
        }

        public void setShiftInView(int shiftInView) {
            this.shiftInView = shiftInView;
        }
    }

    /**
     * Controller for table discourse_annotator.lin_results
     */
    private static class LinResultsController {
        private static Connection conn = DBCon.getConnection(Databases.DISCOURSE_ANNOTATOR);

        public static ArrayList<LinResult> getAllLinResults() {
            ResultSet rs;
            ArrayList<LinResult> linResults = new ArrayList<>();
            String query = "SELECT * FROM " + LinResult.TABLE_NAME;
            System.out.println(query);
            try {
                PreparedStatement ps = conn.prepareStatement(query);
                rs = ps.executeQuery();

                while (rs.next()) {
                    int pairId = rs.getInt("pair_id");
                    int shiftInView = rs.getInt("shift_in_view");

                    LinResult linResult = new LinResult(pairId, shiftInView);
                    linResults.add(linResult);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
            return linResults;
        }
    }
}
