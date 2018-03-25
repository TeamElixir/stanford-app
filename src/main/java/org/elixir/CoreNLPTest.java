package org.elixir;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class CoreNLPTest {

	public static void main(String[] args) {

		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse, depparse,natlog, openie, ner, sentiment");
//		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,depparse,ner,mention, coref, natlog, openie");
		props.setProperty("parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz");
		//        props.setProperty("ner.model","edu/stanford/nlp/models/ner/english.muc.7class.caseless.distsim.crf.ser.gz");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// read some text in the text variable
		String text = "John's counsel's deficient performance deprived him of a trial by causing him to accept a plea, "
				+ "the defendant can show prejudice by demonstrating a \"reasonable probability that, but for counsel's"
				+ " errors, he would not have pleaded guilty and would have insisted on going to trial";

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			System.out.println(sentence.get(TextAnnotation.class));
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				String word = token.get(TextAnnotation.class);
				// this is the POS tag of the token
				String pos = token.get(PartOfSpeechAnnotation.class);
				// this is the NER label of the token
				String ne = token.get(NamedEntityTagAnnotation.class);
				//                System.out.println(word + " " + pos + " " + ne);
			}

			Collection<RelationTriple> triples =
					sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
			// Print the triples
			if (triples != null) {
				for (RelationTriple triple : triples) {
					System.out.println(triple.confidence + " (" + triple.subjectGloss()
							+ ", " + triple.relationGloss()
							+ ", " + triple.objectGloss() +
							")");
				}
			} else {
				System.out.println("Triples: Null");
				System.exit(1);
			}
		}
	}
}
