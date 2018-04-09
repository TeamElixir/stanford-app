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
import org.elixir.db.Controller;
import org.elixir.db.DBCon;
import org.elixir.models.Sentence;
import org.elixir.models.Triple;
import org.elixir.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class CoreNLPTest {

	private static Connection conn = DBCon.getConnection();

	public static void main(String[] args) throws IOException {

		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog, openie, ner");
		//        props.setProperty("ner.model","edu/stanford/nlp/models/ner/english.muc.7class.caseless.distsim.crf.ser.gz");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		String globalFilePath = new File("").getAbsolutePath();
		globalFilePath += "/src/main/resources/sentiment_analysis/legal_cases/";

		for (int i = 11; i <= 22; i++) {
			System.out.println(i);
			String filePath = globalFilePath + "criminal/case_" + String.valueOf(i) + ".txt";
			String fileName = "criminal_triples/case_" + String.valueOf(i) + ".txt";
			String writePath = globalFilePath + fileName;
			String textRaw = Utils.readFile(filePath);

			String[] splittedParagraphs = textRaw.split("\n");

			for (String text : splittedParagraphs) {
				// create an empty Annotation just with the given text
				Annotation document = new Annotation(text);

				// run all Annotators on this text
				pipeline.annotate(document);

				// these are all the sentences in this document
				// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
				List<CoreMap> sentences = document.get(SentencesAnnotation.class);

				for (CoreMap sentence : sentences) {
					//System.out.println(sentence.get(TextAnnotation.class));
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

					Sentence sentence1  = new Sentence(fileName, sentence.toString());
					boolean sentenceInserted = Controller.insertSentence(sentence1);
					int sentenceId = Controller.getLastestSentenceId();
					System.out.println("sentenceId: " + sentenceId);
					if(sentenceId == -1) {
						System.out.println("Sentence ID not found. Exiting ...");
						System.exit(1);
					}

					if(sentenceInserted) {
						// Print the triples
						for (RelationTriple triple : triples) {
							Triple triple1 = new Triple(triple.subjectGloss(),
									triple.relationGloss(), triple.objectGloss(), sentenceId);
							// insert triple to database
							Controller.insertTriple(triple1);
						}   // for each triple
					}

				}   // for each sentence
			}
		}   // for each case

		try {
			conn.close();
		}
		catch (SQLException e) {
			System.out.println("Error in closing connection: " + e);
		}
	}   // main()

}   // class CoreNLPTest
