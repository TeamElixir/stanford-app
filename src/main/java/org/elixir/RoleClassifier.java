package org.elixir;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.elixir.controllers.SentencesController;
import org.elixir.models.Case;
import org.elixir.models.PosTaggedWord;
import org.elixir.models.Sentence;
import org.elixir.models.Triple;
import org.elixir.models.Word;

import java.util.ArrayList;
import java.util.Properties;

public class RoleClassifier {

	public static void main(String[] args) {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,natlog,sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		//update the two arraylists in SentimentGradientAndCost class

        /*
        todo
        needs to get pos tag of each word in the sentence
        for each triple in sentence
            1. identify the sentences with negative sentiment in relation part only
            2. identify the sentences with negative sentiment in complete sentence
        */


		// for each case from case_11 to case_20
		// fetch sentences of each case
        int[] caseNumbers = {11,12,13,14,15,16,17,18,19,20};
        ArrayList<Case> cases = new ArrayList<>();
		for (int n : caseNumbers) {
			Case aCase = new Case();
			aCase.setSentences(SentencesController.getSentencesOfCase(n));
			cases.add(aCase);
		}

		// sample usage
		for (Case _case : cases) {
			for(Sentence sentence: _case.getSentences()) {
				for(Triple triple: sentence.getTriples()) {
					System.out.println(triple);
				}

				for(PosTaggedWord posTaggedWord: sentence.getPosTaggedWords()) {
					System.out.println(posTaggedWord.getPosTag());
					System.out.println(posTaggedWord.getNerTag());
				}
			}
		}
	}
}
