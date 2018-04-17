package org.elixir;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.elixir.controllers.TripleController;
import org.elixir.controllers.WordController;
import org.elixir.models.Triple;
import org.elixir.models.Word;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeSet;

public class WordSentimentTest {

	private static TreeSet<String> linux_dictionary;

	private static TreeSet<String> legalTerms_dictionary = new TreeSet<>();

	public static int count = 0;

	public static void main(String[] args) {

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,natlog,sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		linux_dictionary = createDictionary(pipeline);
		ArrayList<Triple> triples = TripleController.getAllTriples();

		for (Triple triple : triples) {
			// extract subject, relation, and object
			String[] parts = new String[] { triple.getSubject(), triple.getRelationship(), triple.getObject() };

			for (String part : parts) {
				Annotation document = new Annotation(part);
				pipeline.annotate(document);
				List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
				for (CoreMap sentence : sentences) {
					// System.out.println(sentence.get(CoreAnnotations.TextAnnotation.class));
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

						if ((!legalTerms_dictionary.contains(lemma)) && linux_dictionary.contains(lemma)
								&& ++count < 40000) {
							linux_dictionary.remove(lemma);
							legalTerms_dictionary.add(lemma);

							int sentimentValue = -2;

							switch (sentiment.toLowerCase()) {
								case "neutral":
									sentimentValue = 0;
									break;
								case "positive":
									sentimentValue = 1;
									break;
								case "negative":
									sentimentValue = -1;
									break;
							}

							Word word1 = new Word(sentimentValue, lemma, triple.getId());
							WordController.insertWord(word1);
						}
					}   // for each token
				}   // for each CoreMap sentence
			}   // for each part
		}
	}   // main()

	// create dictionary file
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
	}   // createDictionary()

}   // class WordSentimentAnalysis
