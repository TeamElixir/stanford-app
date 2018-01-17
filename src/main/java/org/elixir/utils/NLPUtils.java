package org.elixir.utils;

import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.Quadruple;

import java.util.Collection;

public class NLPUtils {
	public String getSubject(String input){
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
