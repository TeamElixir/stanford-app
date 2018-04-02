package org.elixir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Sort_results {

	public static void main(String[] args) throws IOException {
		String filePath = new File("").getAbsolutePath();
		filePath += "/src/main/resources/sentiment_analysis/legal_cases/results/";

		String[] fileNames = { "negative.csv", "positive.csv", "neutral.csv" };

		for (String fileName : fileNames) {

			TreeMap<String, String> wordMap = new TreeMap<>();
			Scanner scanner = new Scanner(new File(filePath + fileName));

			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				String firstWord = line.split(",")[0];
				wordMap.put(firstWord, line);
			}
			scanner.close();

			BufferedWriter out = new BufferedWriter(new FileWriter(filePath + "sorted/" + fileName));
			for (Map.Entry<String, String> entry : wordMap.entrySet()) {
				String value = entry.getValue();
				out.write(value);
				out.newLine();
			}
			out.flush();
			out.close();

		}

	}

}
