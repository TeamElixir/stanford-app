package org.elixir;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCostAndGradient;
import edu.stanford.nlp.util.CoreMap;
import org.elixir.controllers.SentencesController;
import org.elixir.models.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class RoleClassifier {

	public static void main(String[] args) throws IOException {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,natlog,sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		//update the two arraylists in SentimentGradientAndCost class
		String filePath = new File("").getAbsolutePath();
		filePath += "/src/main/resources/sentiment_analysis/legal_cases/results/reviewed/";

		String nonPositiveFilePath = filePath + "non_positive_mini.csv";
		String nonNeutralFilePath = filePath + "non_neutral_mini.csv";
		String nonNegativeFilePath = filePath + "non_negative_mini.csv";

		Scanner nonPositiveScanner = new Scanner(nonPositiveFilePath);
		while(nonPositiveScanner.hasNextLine()){
			SentimentCostAndGradient.nonNeutralList.add(nonPositiveScanner.nextLine());
		}

		Scanner nonNeutralScanner = new Scanner(nonNeutralFilePath);
		while(nonNeutralScanner.hasNextLine()){
			SentimentCostAndGradient.nonNeutralList.add(nonNeutralScanner.nextLine());
		}

		Scanner nonNegativeScanner = new Scanner(nonNegativeFilePath);
		while(nonNegativeScanner.hasNextLine()){
			SentimentCostAndGradient.nonNegativeList.add(nonNegativeScanner.nextLine());
		}
        /*
        todo
        needs to get pos tag of each word in the sentence
        for each triple in sentence
            1. identify the sentences with negative sentiment in relation part only
            2. identify the sentences with negative sentiment in complete sentence
        */


		// for each case from case_11 to case_20
		// fetch sentences of each case
        int[] caseNumbers = {11};
        ArrayList<Case> cases = new ArrayList<>();
		for (int n : caseNumbers) {
			Case aCase = new Case();
			aCase.setSentences(SentencesController.getSentencesOfCase(n));
			cases.add(aCase);
		}
		BufferedWriter bf = new BufferedWriter(new FileWriter(new File("/home/viraj/stanford-core-nlp/FYP/ollie/case_11.txt")));

		// sample usage
		for (Case _case : cases) {
			for(Sentence sentence: _case.getSentences()) {

				try {
					bf.write(sentence.getSentence());
					bf.write("\n");
					bf.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
/*
				//to create empty map for the word-postag combinations inside SentimentCostAndGradient class
				SentimentCostAndGradient.createPosTagMap();

				//to add the word-postag combinations to the map inside SentimentCostAndGradient class
				for(PosTaggedWord posTaggedWord: sentence.getPosTaggedWords()) {
					SentimentCostAndGradient.addPosTagsOfWords(posTaggedWord.getWord(),posTaggedWord.getPosTag());
				}

				for(GoogleTriple triple: sentence.getGoogleTriples()) {
					Annotation annotation = new Annotation(triple.getRelation()+ " "+triple.getObject()+".");
					//Annotation annotation = new Annotation(triple.getRelation());
					pipeline.annotate(annotation);

					List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
					for(CoreMap coreMapSentence:sentences){
						String sentiment = coreMapSentence.get(SentimentCoreAnnotations.SentimentClass.class);
						if(sentiment.equals("Negative")){
							System.out.println("( " + triple.getSubject() + " , " + triple.getRelation() + " , " +triple.getObject()+" )");
						}

					}

				}*/

			}
		}
		bf.close();
	}
}
