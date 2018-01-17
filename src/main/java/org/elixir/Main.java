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
import java.util.*;

public class Main {

	public static ArrayList<String> keywords = new ArrayList<>(Arrays.asList("Petitioner","Government","petitioner",
			"petitioners","government", "Petitioners"));

	public static void main(String[] args) {

		//completeExample();

		testTriple();
	} // main

	private static void minimalAnalysisPipeline() {
		Annotator pipeline = new StanfordCoreNLP();
		Annotation annotation = new Annotation("Can you parse my sentence?");
		pipeline.annotate(annotation);
	}

	private static void completeExample() {
		PrintWriter xmlOut = null;
		try {
			xmlOut = new PrintWriter("xmlOutput.xml");
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found");;
			e.printStackTrace();
		}
		Properties props = new Properties();
		props.setProperty("annotators",
				"tokenize, ssplit, pos, lemma, ner, parse");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		Annotation annotation = new Annotation("This is a short sentence. And this is another. Here's yet another.");
		pipeline.annotate(annotation);
		try {
			pipeline.xmlPrint(annotation, xmlOut);
		}
		catch (IOException e) {
			System.out.println("Error: pipeline.xmlPrint");
			e.printStackTrace();
		}
		// An Annotation is a Map and you can get and use the
		// various analyses individually. For instance, this
		// gets the parse tree of the 1st sentence in the text.
		List<CoreMap> sentences = annotation.get(
				CoreAnnotations.SentencesAnnotation.class);
		if (sentences != null && sentences.size() > 0) {
			for(int i=0; i<sentences.size(); i++){
				CoreMap sentence = sentences.get(i);
				Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
				PrintWriter out = new PrintWriter(System.out);
				out.println("The "+ i +" th/nd sentence parsed is:");
				tree.pennPrint(out);
			}
		}
	}

	private static void apiSample() {
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// read some text in the text variable
		String text = "The quick brown fox jumps over the lazy dog";

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

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
			}

			// this is the parse tree of the current sentence
			Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);

			// this is the Stanford dependency graph of the current sentence
			SemanticGraph dependencies = sentence
					.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
		}

		// This is the coreference link graph
		// Each chain stores a set of mentions that link to each other,
		// along with a method for getting the most representative mention
		// Both sentence and token offsets start at 1!
		Map<Integer, CorefChain> graph =
				document.get(CorefCoreAnnotations.CorefChainAnnotation.class);
	}   // apiSample


	// TRIPLES EXTRACT TEST
	public static void testTriple(){
		// Create a CoreNLP document
		Document doc = new Document("Petitioners' main argument is  had they known about the withheld evidence, they could have\n" +
				"challenged the Government's basic group attack theory by raising an alternative theory, namely, a single\n" +
				"perpetrator (or two at most) had attacked Fuller. Considering the withheld evidence \"in the context of the\n" +
				"entire record,\" Agurs, supra, at 112, that evidence is too little, too weak, or too distant from the main\n" +
				"evidentiary points to meet Brady's standards.");

		// Iterate over the sentences in the document
		for (Sentence sent : doc.sentences()) {
			// Iterate over the triples in the sentence
			for (RelationTriple triple : sent.openieTriples()) {
				// Print the triple
				/*System.out.println(triple.confidence + "\t" +
						triple.subjectLemmaGloss() + "\t" +
						triple.relationLemmaGloss() + "\t" +
						triple.objectLemmaGloss());*/
				System.out.println("Sentence  : "+sent);
				System.out.println("");
				System.out.println("subject : "+"\t"+triple.subjectLemmaGloss());
				System.out.println(" ");
			}
		}
	}
}
