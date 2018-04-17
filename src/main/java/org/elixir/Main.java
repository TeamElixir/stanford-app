package org.elixir;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.Quadruple;
import org.elixir.controllers.SentenceController;
import org.elixir.data.Texts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class Main {

	public static void main(String[] args) {

	} // main

	private static void writeSentencesToFile() {
		ArrayList<org.elixir.models.Sentence> allSentences = SentenceController.getAllSentences();
		String fileName = "allSentences.txt";
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileName)));
			for (org.elixir.models.Sentence sentence : allSentences) {
				bw.write(sentence.getSentence());
				bw.newLine();
			}

			bw.flush();
			bw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	// TRIPLES EXTRACT TEST
	public static void testTriple() {
		// Create a CoreNLP document
		System.out.println("test 1");

		Sentence sentence = new Sentence("He who never failed, never tried anything new");
		System.out.println(sentence.nerTags());
		Sentence sentence1 = new Sentence("Make hay while the sun shines");
		Collection<RelationTriple> relationTriples = sentence.openieTriples();
		System.out.println("RelationTriples: " + relationTriples);
		Collection<Quadruple<String, String, String, Double>> openie = sentence.openie();
		System.out.println("OpenIE: " + openie);
		System.exit(0);
		for (RelationTriple relationTriple : relationTriples) {
			System.out.println("Subject: " + relationTriple.subject);
			System.out.println("SubjectGloss:" + relationTriple.subjectGloss());
			System.out.println("Confidence: " + relationTriple.confidence);
			System.out.println("ConfidenceGloss: " + relationTriple.confidence);
			System.out.println("Relation: " + relationTriple.relation);
			System.out.println("RelationGloss: " + relationTriple.relationGloss());
		}

		System.exit(0);

		Document doc = new Document(Texts.CASE_1_TEXT_1);
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
