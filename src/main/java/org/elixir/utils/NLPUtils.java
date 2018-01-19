package org.elixir.utils;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Quadruple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class NLPUtils {
	public String getSubject(String input){
		// TODO: remove 'that' first
		Sentence sent = new Sentence(input);
		Collection<Quadruple<String, String, String, Double>> openie = sent.openie();
		String subject = "";
		for (Quadruple<String, String, String, Double> quadruple : openie) {
			subject = quadruple.first;
		}

		return subject;
	}


	public ArrayList getPersonList(CoreMap sentence){

        ArrayList<String> personArray = new ArrayList<>();

        for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
            String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            if("PERSON".equals(ne)){
                personArray.add(token.get(CoreAnnotations.TextAnnotation.class));
            }
        }

        return personArray;
    }

}
