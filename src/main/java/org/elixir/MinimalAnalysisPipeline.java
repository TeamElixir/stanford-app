package org.elixir;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class MinimalAnalysisPipeline {

	public static void execute(){
		Annotator pipeline = new StanfordCoreNLP();
		Annotation annotation = new Annotation("Can you parse my sentence?");
		pipeline.annotate(annotation);
	}
}
