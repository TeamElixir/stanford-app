package org.elixir;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeSet;

public class word_sentiment_test {

	public static TreeSet<String> linux_dictionary;

	public static TreeSet<String> legalTerms_dictionary = new TreeSet<>();

	public static int count = 0;

	public static void main(String[] args) throws IOException {

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,natlog,sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		linux_dictionary = createDictionary(pipeline);

		String globalFilePath = new File("").getAbsolutePath();
		globalFilePath += "/src/main/resources/sentiment_analysis/legal_cases/";

		BufferedWriter out_basic = new BufferedWriter(new FileWriter(globalFilePath + "results/word_sentiment.csv", true));
		//BufferedWriter out_final = new BufferedWriter(new FileWriter(globalFilePath+"results/final.csv"));
		BufferedWriter out_neutral = new BufferedWriter(new FileWriter(globalFilePath + "results/neutral.csv", true));
		BufferedWriter out_negative = new BufferedWriter(new FileWriter(globalFilePath + "results/negative.csv", true));
		BufferedWriter out_positive = new BufferedWriter(new FileWriter(globalFilePath + "results/positive.csv", true));

		for (int i = 11; i <= 22; i++) {
			String casename = "criminal_triples/case_" + String.valueOf(i) + ".txt";
			String filePath = globalFilePath + casename;
			Scanner scanner = new Scanner(new File(filePath));

			while (scanner.hasNext()) {
				String line = scanner.nextLine();

				// (subject, relation, object) [subject is related to the object]
				// extract the triple
				String triple = line.substring(1, line.indexOf(")"));

				// extract the original sentence
				String originalSentence = line.substring(line.indexOf("[") + 1, line.length() - 2);

				// extract subject, relation, and object
				String[] words = triple.split(",");

				for (String word : words) {
					Annotation document = new Annotation(word);
					pipeline.annotate(document);
					List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
					for (CoreMap sentence : sentences) {
						//System.out.println(sentence.get(CoreAnnotations.TextAnnotation.class));
						// traversing the words in the current sentence
						// a CoreLabel is a CoreMap with additional token-specific methods
						for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
							// this is the text of the token
							String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
							//                        // this is the POS tag of the token
							//                        String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
							//                        // this is the NER label of the token
							//                        String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
							String sentiment = token.get(SentimentCoreAnnotations.SentimentClass.class);

							Boolean condition = true;

							if (condition) {
								if ((!legalTerms_dictionary.contains(lemma)) && linux_dictionary.contains(lemma)
										&& count < 40000) {
									count += 1;
									linux_dictionary.remove(lemma);
									legalTerms_dictionary.add(lemma);

									switch (sentiment.toLowerCase()) {
										case "neutral":
											out_neutral.write(lemma + ", " + casename + ", " + triple + " , "
													+ originalSentence);
											out_neutral.newLine();
											break;
										case "positive":
											out_positive.write(lemma + ", " + casename + ", " + triple + ", "
													+ originalSentence);
											out_positive.newLine();

											break;
										case "negative":
											out_negative.write(lemma + ", " + casename + ", " + triple + ", "
													+ originalSentence);
											out_negative.newLine();
											break;
									}
									out_basic.write(lemma + ", " + sentiment);
									//out_detail.write(lemma + " , "+sentiment + " , "+casename+" , "+ sentence );
									out_basic.newLine();

									//out_detail.newLine();
								}

							}

						}
					}
				}

			}
			//            String textRaw = Utils.readFile(filePath);
			//
			//            String[] splittedParagraphs = textRaw.split("\n");
			//
			//            for(String splittedParagraph:splittedParagraphs){
			//                String text = splittedParagraph;
			//
			//
			//
			//            }
			out_basic.flush();
			out_neutral.flush();
			out_negative.flush();
			out_positive.flush();
			scanner.close();

			//.flush();
		}


		/*
		for (int i = 1; i <= 10; i++) {
			String casename = "environment/case_" + String.valueOf(i) + ".txt";
			String filePath = globalFilePath + casename;

			String textRaw = Utils.readFile(filePath);

			String[] splittedParagraphs = textRaw.split("\n");

			for (String splittedParagraph : splittedParagraphs) {
				String text = splittedParagraph;
				Annotation document = new Annotation(text);
				pipeline.annotate(document);
				List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
				for (CoreMap sentence : sentences) {
					//System.out.println(sentence.get(CoreAnnotations.TextAnnotation.class));
					// traversing the words in the current sentence
					// a CoreLabel is a CoreMap with additional token-specific methods
					for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
						// this is the text of the token
						String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
						// this is the POS tag of the token
						String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
						// this is the NER label of the token
						String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
						String sentiment = token.get(SentimentCoreAnnotations.SentimentClass.class);

						if (true) {
							if (!legalTerms_dictionary.contains(lemma) && linux_dictionary.contains(lemma) && count < 8000) {
								linux_dictionary.remove(lemma);
								legalTerms_dictionary.add(lemma);
								count += 1;
								if (sentiment.toLowerCase().equals("neutral")) {
									out_neutral.write(lemma + " , " + casename);
									out_neutral.newLine();
								} else if (sentiment.toLowerCase().equals("positive")) {
									out_positive.write(lemma + " , " + casename);
									out_positive.newLine();

								} else if (sentiment.toLowerCase().equals("negative")) {
									out_negative.write(lemma + " , " + casename);
									out_negative.newLine();
								}
								out_basic.write(lemma + " , " + sentiment);
								//out_detail.write(lemma + " , "+sentiment + " , "+casename+" , "+ sentence );
								out_basic.newLine();
								//out_detail.newLine();

							}

						}

					}
				}

			}
			out_basic.flush();
			out_neutral.flush();
			out_negative.flush();
			out_positive.flush();
			//out_detail.flush();
		}   // end of for() environmental

		for (int i = 1; i <= 15; i++) {
			String casename = "family/case_" + String.valueOf(i) + ".txt";
			String filePath = globalFilePath + casename;

			String textRaw = Utils.readFile(filePath);

			String[] splittedParagraphs = textRaw.split("\n");

			for (String splittedParagraph : splittedParagraphs) {
				String text = splittedParagraph;
				Annotation document = new Annotation(text);
				pipeline.annotate(document);
				List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
				for (CoreMap sentence : sentences) {
					//System.out.println(sentence.get(CoreAnnotations.TextAnnotation.class));
					// traversing the words in the current sentence
					// a CoreLabel is a CoreMap with additional token-specific methods
					for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
						// this is the text of the token
						String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
						// this is the POS tag of the token
						String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
						// this is the NER label of the token
						String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
						String sentiment = token.get(SentimentCoreAnnotations.SentimentClass.class);

						if (true) {
							if (!legalTerms_dictionary.contains(lemma) && linux_dictionary.contains(lemma)
									&& count < 15000) {
								linux_dictionary.remove(lemma);
								legalTerms_dictionary.add(lemma);
								count += 1;
								if (sentiment.toLowerCase().equals("neutral")) {
									out_neutral.write(lemma + " , " + casename);
									out_neutral.newLine();
								} else if (sentiment.toLowerCase().equals("positive")) {
									out_positive.write(lemma + " , " + casename);
									out_positive.newLine();

								} else if (sentiment.toLowerCase().equals("negative")) {
									out_negative.write(lemma + " , " + casename);
									out_negative.newLine();
								}
								out_basic.write(lemma + " , " + sentiment);
								//out_detail.write(lemma + " , "+sentiment + " , "+casename+" , "+ sentence );
								out_basic.newLine();
								//out_detail.newLine();
							}

						}

					}
				}

			}
			out_basic.flush();
			out_neutral.flush();
			out_negative.flush();
			out_positive.flush();
			//out_detail.flush();
		}   // end of for() family
		*/

		out_basic.close();
		//out_detail.close();
		out_negative.close();
		out_positive.close();
		out_neutral.close();

	}   // end of main()

	//create dictionary file
	public static TreeSet<String> createDictionary(StanfordCoreNLP pipeline) {

		String filePath = new File("").getAbsolutePath();
		filePath += "/src/main/resources/sentiment_analysis/dict/linux_dict.txt";
		File file = new File(filePath);
		TreeSet<String> words = new TreeSet<>();

		try {
			System.out.println("started");
			Scanner scanner = new Scanner(file);
			while (scanner.hasNext()) {
				String word = scanner.next();
				if (word.charAt(0) == word.toLowerCase().charAt(0)) {
					Annotation annotation = new Annotation(word);
					pipeline.annotate(annotation);
					List<CoreMap> list = annotation.get(CoreAnnotations.SentencesAnnotation.class);
					String tokenLemma = list
							.get(0).get(CoreAnnotations.TokensAnnotation.class)
							.get(0).get(CoreAnnotations.LemmaAnnotation.class);
					words.add(tokenLemma);
				}
			}
			System.out.println("finished");
			return words;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("interrupted");
			return null;
		}
	}   // end of createDictionary()

}
