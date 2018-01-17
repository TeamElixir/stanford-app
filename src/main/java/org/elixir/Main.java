package org.elixir;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

public class Main {

	public static void main(String[] args) {
		testTriple();
	} // main

	// TRIPLES EXTRACT TEST
	public static void testTriple() {
		// Create a CoreNLP document
		Document doc = new Document("Considering the withheld evidence in the context of the" +
				"entire record, Agurs, supra, at 112, evidence is too little, too weak, or too distant from the main" +
				"evidentiary points to meet Brady's standards.");

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
