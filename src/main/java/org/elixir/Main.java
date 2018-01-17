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
			// When we ask for the lemma, it will load and run the part of speech tagger
//			System.out.println("The third lemma of the sentence '" + sent + "' is " + sent.lemma(2));
//			// When we ask for the parse, it will load and run the parser
//			System.out.println("The parse of the sentence '" + sent + "' is " + sent.parse());

//			System.out.println(sent.nerTags().toString());
			// Iterate over the triples in the sentence
			System.out.println(sent.lemmas().toString());
			for (RelationTriple triple : sent.openieTriples()) {
				// Print the triple
				System.out.println(triple.confidence + "\t" +
						triple.subjectGloss() + "\t" +
						triple.relationGloss() + "\t" +
						triple.objectGloss());

			}
		}
	}
}
