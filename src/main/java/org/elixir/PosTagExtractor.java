package org.elixir;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.elixir.controllers.PosTaggedWordController;
import org.elixir.controllers.SentenceController;
import org.elixir.models.PosTaggedWord;
import org.elixir.models.Sentence;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PosTagExtractor {

	private static StanfordCoreNLP pipeline;

	public static void main(String[] args) {
		ArrayList<Sentence> allSentences = SentenceController.getAllSentences();
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		pipeline = new StanfordCoreNLP(props);

		System.out.println("# sentences: " + allSentences.size());
		for (Sentence sentence : allSentences) {
			extractPosTags(sentence);
		}

	}

	public static void extractPosTags(Sentence inputSentence) {
		System.out.println("Sentence: " + inputSentence.getId());

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(inputSentence.getSentence());

		// run all Annotators on this text
		pipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
				// this is the text of the token
				String word = token.get(CoreAnnotations.TextAnnotation.class);
				// this is the POS tag of the token
				String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
				// this is the NER label of the token
				String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);

				if (!(".".equals(word) || ",".equals(word) || "'".equals(word) || "''".equals(word) || "`".equals(word)
						|| "\"".equals(word) || ":".equals(word) || ";".equals(word) || "``".equals(word))) {
					PosTaggedWord posTaggedWord = new PosTaggedWord(word, pos, ne, inputSentence.getId());
					//				System.out.println(posTaggedWord);

					boolean inserted = PosTaggedWordController.insertPosTaggedWord(posTaggedWord);
					if (!inserted) {
						System.out.println("Insertion failed");
					}
				}
			}
		}
	}
}
