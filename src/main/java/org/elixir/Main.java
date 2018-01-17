package org.elixir;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import org.elixir.data.Texts;

public class Main {

	public static void main(String[] args) {
//		ApiSample.coreAPI();
		SimpleAPI.execute();
	} // main

	// TRIPLES EXTRACT TEST
	public static void testTriple() {
		// Create a CoreNLP document
		Document doc = new Document(Texts.text1);

		// Iterate over the sentences in the document
		for (Sentence sent : doc.sentences()) {
			// Iterate over the triples in the sentence
			for (RelationTriple triple : sent.openieTriples()) {
				// Print the triple
				System.out.println(triple.confidence + "\t" +
						triple.subjectLemmaGloss() + "\t" +
						triple.relationLemmaGloss() + "\t" +
						triple.objectLemmaGloss());
			}
		}
	}
}
