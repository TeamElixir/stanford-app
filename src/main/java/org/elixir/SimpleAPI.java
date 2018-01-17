package org.elixir;

import edu.stanford.nlp.simple.*;

public class SimpleAPI {
	public static void execute(){
		// Create a document. No computation is done yet.
		Document doc = new Document("add your text here! It can contain multiple sentences.");
		for (Sentence sent : doc.sentences()) {  // Will iterate over two sentences
			System.out.println("Sentence: " + sent);
			// We're only asking for words -- no need to load any models yet
			System.out.println("The second word: " + sent.word(1));
			// When we ask for the lemma, it will load and run the part of speech tagger
			System.out.println("The third lemma: " + sent.lemma(2));
			// When we ask for the parse, it will load and run the parser
			System.out.println("The parse: "+ sent.parse());
			System.out.println("------------------------------------------------\n");
		}
	}
}
