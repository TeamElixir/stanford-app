package org.elixir;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class OllieTripleParser {

    public static void main(String[] args) throws FileNotFoundException {

        String filePath = new File("").getAbsolutePath();
        filePath += "/src/main/resources/sentiment_analysis/legal_cases/ollie_triples/case_11_ollie_triples.txt";

        Scanner scanner = new Scanner(new File(filePath));
        while(scanner.hasNextLine()){
            scanner.nextLine();
            if(scanner.hasNextLine()){
                String line = scanner.nextLine();
                if(!line.contains(":")){
                   if(scanner.hasNextLine()){
                       scanner.nextLine();
                   }
                }else{
                    while(line.contains(":")){
                        System.out.println(line);
                        if(scanner.hasNextLine()){
                            line = scanner.nextLine();
                        }

                    }
                }

            }
        }


    }

}
