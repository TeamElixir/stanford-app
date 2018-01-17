package org.elixir.utils;

import java.io.*;

public class Utils {
	public static String readFile(String fileName){
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
		}catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}catch (IOException  e) {
			e.printStackTrace();
			return null;
		} finally {
			if (br != null){
				try{
					br.close();
				}catch (IOException e) {

				}
			}
		}
	}
}
