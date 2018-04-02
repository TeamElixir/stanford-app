package org.elixir.utils;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Quadruple;

import java.util.ArrayList;
import java.util.Collection;

public class NLPUtils {

	public static ArrayList getPersonList(CoreMap sentence) {

		ArrayList<String> personArray = new ArrayList<>();

		for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
			String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
			if ("PERSON".equals(ne)) {
				personArray.add(token.get(CoreAnnotations.TextAnnotation.class));
			}
		}

		return personArray;
	}

	/**
	 * @param sentence sentence must be annotated with parse and sentiment
	 * @return true for Positive and false for Negetive
	 */
	public static boolean getSentiment(CoreMap sentence) {

		String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

		return "Positive".equals(sentiment);
	}

	public String getSubject(String input) {
		// TODO: remove 'that' first
		Sentence sent = new Sentence(input);
		Collection<Quadruple<String, String, String, Double>> openie = sent.openie();
		String subject = "";
		for (Quadruple<String, String, String, Double> quadruple : openie) {
			subject = quadruple.first;
		}

		return subject;
	}

}
