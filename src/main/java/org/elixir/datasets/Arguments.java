package org.elixir.datasets;

import org.elixir.controllers.SentencesController;
import org.elixir.db.DBCon;
import org.elixir.db.Databases;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Arguments {
    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<Argument> allArguments = ArgumentsController.getAllArguments();

        PrintWriter pw = new PrintWriter(new File(Argument.TABLE_NAME + ".csv"));
        StringBuilder sb = new StringBuilder();
        final String SEPARATOR = ",";
        sb.append("sentence");
        sb.append(SEPARATOR);
        sb.append("argument");
        sb.append("\n");

        for (Argument argument : allArguments) {
            System.out.println(argument);
            String sentence = "\"" + argument.getSentence() + "\"";
            sb.append(sentence);
            sb.append(SEPARATOR);
            sb.append(argument.isArgument());
            sb.append("\n");
        }

        pw.write(sb.toString());
        pw.close();
        System.out.println("done!");
    }

    private static class Argument {
        public static final String TABLE_NAME = "arguments";
        private int sentenceId;
        private int argument;

        Argument(int sentenceId, int argument) {
            this.sentenceId = sentenceId;
            this.argument = argument;
        }

        @Override
        public String toString() {
            return "Argument{" +
                    "sentence=" + getSentence() +
                    ", argument=" + argument +
                    '}';
        }

        int getSentenceId() {
            return sentenceId;
        }

        String getSentence() {
            return SentencesController.getSentenceById(sentenceId).getSentence();
        }

        int isArgument() {
            return argument;
        }

    }

    private static class ArgumentsController {
        private static Connection conn = DBCon.getConnection(Databases.DISCOURSE_ANNOTATOR);

        static ArrayList<Argument> getAllArguments() {
            ResultSet rs;
            ArrayList<Argument> arguments = new ArrayList<>();
            String query = "SELECT * FROM " + Argument.TABLE_NAME;
            System.out.println(query);
            try {
                PreparedStatement ps = conn.prepareStatement(query);
                rs = ps.executeQuery();

                while (rs.next()) {
                    int sentenceId = rs.getInt("sentence_id");
                    int argument = rs.getInt("argument");

                    Argument a = new Argument(sentenceId, argument);
                    arguments.add(a);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
            return arguments;
        }
    }
}
