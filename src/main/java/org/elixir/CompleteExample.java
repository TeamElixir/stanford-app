package org.elixir;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

public class CompleteExample {
	public static void execute(){
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
}
