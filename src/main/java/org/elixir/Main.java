package org.elixir;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Main {

	public static void main(String[] args) {
		testTriple();
	} // main

	// TRIPLES EXTRACT TEST
	public static void testTriple(){
		// Create a CoreNLP document
//		Document doc = new Document("Obama was married in Hawaii. He is our president.");
//		Document doc = new Document("A 'reasonable probability' of a different result\" is one in which the suppressed evidence \" 'undermines confidence in the outcome of the trial.");
		Document doc = new Document("The Government does not contest petitioners' claim that the withheld evidence was \"favorable to\n"
				+ "the defense.\" Petitioners and the Government, however, do contest the materiality of the undisclosed\n"
				+ "Brady information. Such \"evidence is 'material' . . . when there is a reasonable probability that,\n"
				+ "had the evidence been disclosed, the result of the proceeding would have been different.\"\n"
				+ "Cone v. Bell, 556 U. S. 449, 469-470. \"A 'reasonable probability' of a different result\"\n"
				+ "is one in which the suppressed evidence \" 'undermines confidence in the outcome of the trial.'\n"
				+ "\"Kyles v. Whitley, 514 U. S. 419, 434. To make that determination, this Court \"evaluate[s]\" the \n"
				+ "withheld evidence \"in the context of the entire record.\" \n"
				+ "United States v. Agurs, 427 U. S. 97, 112. Pp. 9-11.");
//		Document doc = new Document("The quick brown fox jumps over the lazy dog");
//		Document doc = new Document("Such \"evidence is 'material' . . . when there is a reasonable probability that, had the evidence been disclosed, the result of the proceeding would have been different.");

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
