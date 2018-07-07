package org.elixir.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elixir.models.Node;

import java.io.*;
import java.util.ArrayList;

public class Utils {
    public static String readFile(String fileName) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();

            return everything;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {

                }
            }
        }
    }

    public static void writeToJson(ArrayList<Node> nodes) throws IOException {
        final String outputRootDirName = ".oblie";
        final String outputFileName = "arguments.json";
        String userHome = System.getProperty("user.home");
        String outputRootDirPath = userHome + File.separator + outputRootDirName;
        boolean mkdir = new File(outputRootDirPath).mkdir();
        String filePath = outputRootDirPath + File.separator + outputFileName;

        ObjectMapper mapper = new ObjectMapper();

        //Object to JSON in String
        String jsonInString = mapper.writeValueAsString(nodes);
        System.out.println("JSON as String: " + jsonInString);

        //Object to JSON in file
        mapper.writeValue(new File(filePath), nodes);

    }
}
