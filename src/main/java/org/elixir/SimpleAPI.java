package org.elixir;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ie.machinereading.structure.Span;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.naturalli.Polarity;
import edu.stanford.nlp.simple.*;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.Quadruple;
import org.elixir.data.Texts;
import org.elixir.utils.NLPUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SimpleAPI {
	public static void main(String[] args){
		NLPUtils nlpUtils = new NLPUtils();
		String sent1 = "The quick brown fox jumps over the crazy dog";
		String sent2 = "He who never failed, never tried";
		String sent3 = "Make hay while the sun shines";

//		String sub1 = nlpUtils.getSubject(sent1);
		String sub2 = nlpUtils.getSubject(sent2);
		String sub3 = nlpUtils.getSubject(sent3);

//		System.out.println(sub1);
		System.out.println(sub2);
		System.out.println(sub3);

		System.exit(0);

		// Create a document. No computation is done yet.
		Document doc = new Document(Texts.text3);
		for (Sentence sent : doc.sentences()) {  // Will iterate over two sentences
			List<String> words = sent.words();
			List<String> posTags = sent.posTags();
			List<String> lemmas = sent.lemmas();
			List<String> nerTags = sent.nerTags();
			Tree parse = sent.parse();
			Map<Integer, CorefChain> coref = sent.coref();
			List<Polarity> polarities = sent.natlogPolarities();
			Collection<Quadruple<String, String, String, Double>> openie = sent.openie();

			System.out.println("Parse: " + parse);
			for (Tree tree : parse) {
				Tree[] children = tree.children();
				for (Tree tree1 : tree) {
					System.out.println(tree1);
				}
			}

//			System.out.println("Sentence: " + sent);
//			System.out.println("Words: " + words);
//			System.out.println("NERTags: " + nerTags);
			System.out.println("--------------------------------------------------\n");
		}
	}

	private static void sentenceAlgo(){
		String text = "It was already decided by the Counsellor.";
		Sentence sent = new Sentence(text);
		int i = sent.algorithms().headOfSpan(new Span(0, sent.length()));
		System.out.println(i);
		System.out.println(sent.tokens().get(i).originalText());
	}

	private static void openIE(){
		String text = "The petitioners argued the withheld evidence could've been ruled in their favor";
		Sentence sent = new Sentence(text);
		Collection<Quadruple<String, String, String, Double>> openie = sent.openie();
		for (Quadruple<String, String, String, Double> triplet : openie) {
			System.out.println("Triplet: " + triplet);
//			System.out.println(triplet.first);
//			System.out.println(triplet.second);
//			System.out.println(triplet.third);
			System.out.println("-------------------------");
		}

	}

	private static void singleSentence() {
		String text = "It was already decided by the Counsellor.";
		Sentence sent = new Sentence(text);
		Tree parse = sent.parse();
//		System.out.println(parse);
		Iterator<Tree> iterator = parse.iterator();
		while(iterator.hasNext()) {
			System.out.println(iterator.next());
		}
	}
}
