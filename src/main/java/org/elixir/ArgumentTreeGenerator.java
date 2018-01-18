package org.elixir;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.simple.*;

import java.util.*;

public class ArgumentTreeGenerator {

	private static final ArrayList<String> SUBJECT_LIST = new ArrayList<>(Arrays.asList("Petitioner", "Government","Defendant"));

	private static ArrayList<String> currentSubjects = new ArrayList<>();

	private static ArrayList<ArrayList<ArrayList<String>>> extractedArguments = new ArrayList<>();

	private static ArrayList<Integer> lastSubjects = new ArrayList<>();

	private static boolean hasSubject = false;

	private static String lastSentence = null;

	private static String finalRawSentence;

	public static void main(String[] args) {

		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog, openie, ner");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// read some text in the text variable
		String text = "When a defendant claims that his counsel's deficient performance deprived him of a trial by causing him to accept a plea, the defendant can show prejudice by demonstrating a \"reasonable probability that," +
                " but for counsel's errors, he would not have pleaded guilty and would have insisted on going to trial.\" Hill v. Lockhart," +
                " 474 U. S. 52, 59.\n" +
                "\n" +
                "     Lee contends that he can make this showing because he never would have accepted a guilty plea had he known the " +
                "result would be deportation. The Government contends that Lee cannot show prejudice from accepting a plea where his only " +
                "hope at trial was that something unexpected and unpredictable might occur that would lead to acquittal. Pp. 5-8." +
                "The Government makes two errors in urging the adoption of a per se rule that a defendant with no viable defense cannot " +
                "show prejudice from the denial of his right to trial. First, it forgets that categorical rules are ill suited to an " +
                "inquiry that demands a \"case-by-case examination\" of the \"totality of the evidence.\" Williams v. Taylor," +
                " 529 U. S. 362, 391 (internal quotation marks omitted); Strickland, 466 U. S., at 695. More fundamentally, " +
                "it overlooks that the Hill v. Lockhart inquiry focuses on a defendant's decisionmaking, which may not turn " +
                "solely on the likelihood of conviction after trial.";

		ArrayList<String> rawSentences = new ArrayList<>();
		Document doc = new Document(text);
		List<Sentence> sentences1 = doc.sentences();
		for (Sentence sentence : sentences1) {
			rawSentences.add(sentence.toString());
		}

		for (String rawSentence : rawSentences) {



			String ss = rawSentence.replaceAll("(that,|that|'s)", "");
            finalRawSentence = rawSentences.get(rawSentences.size()-1);
            boolean sentenceAdded2 = false;

			/*if(!hasSubject && lastSubjects.size()>0 && lastSentence!=null){
                for (Integer subject : lastSubjects) {

                        int lastArgumentIndex = extractedArguments.get(subject).size() - 1;
                        extractedArguments.get(subject).get(lastArgumentIndex).add(lastSentence);

                }
                lastSentence=null;
            }*/

            hasSubject = false;

			System.out.println("splitted sentence : " + ss);
			// create an empty Annotation just with the given text
			Annotation document = new Annotation(ss);

			// run all Annotators on this text
			pipeline.annotate(document);

			// these are all the sentences in this document
			// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
			List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

			for (CoreMap sentence : sentences) {

			    ArrayList<Integer> sentenceAdded = new ArrayList<>();


				String rawSentence1 = sentence.get(CoreAnnotations.TextAnnotation.class);
				// traversing the words in the current sentence
				// a CoreLabel is a CoreMap with additional token-specific methods
				for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
					// this is the text of the token
					String word = token.get(CoreAnnotations.TextAnnotation.class);
					// this is the POS tag of the token
					String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
					// this is the NER label of the token
					String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
					//System.out.println(word + " " + pos + " " + ne);
				}

				Collection<RelationTriple> triples =
						sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
				// Print the triples
				for (RelationTriple triple : triples) {
                    /*System.out.println(triple.confidence + "\t" +
                            triple.subjectLemmaGloss() + "\t" +
                            triple.relationLemmaGloss() + "\t" +
                            triple.objectLemmaGloss());*/
					String sentenceSubject = triple.subjectLemmaGloss();
//					System.out.println("subject : " + sentenceSubject);
					for (int i = 0; i < SUBJECT_LIST.size(); i++) {

						if (sentenceSubject.toLowerCase().indexOf(SUBJECT_LIST.get(i).toLowerCase()) != -1) {
							// sentenceSubject is in SUBJECT_LIST

                            if(!hasSubject){
                                lastSubjects.clear();
                            }

							System.out.println("true : + " + SUBJECT_LIST.get(i));
							String currentSubject = SUBJECT_LIST.get(i);
							int currentSubjectIndex = -1;

							if (!currentSubjects.contains(currentSubject)) {
								currentSubjects.add(currentSubject);
								ArrayList<ArrayList<String>> argumentList = new ArrayList<>();
								ArrayList<String> subjectArgumentList = new ArrayList<>();
								subjectArgumentList.add(rawSentence);
								argumentList.add(subjectArgumentList);
								extractedArguments.add(argumentList);
								currentSubjectIndex = currentSubjects.indexOf(currentSubject);
								System.out.println("Subject Index : " + currentSubjectIndex);
								sentenceAdded.add(currentSubjectIndex);
                                if(!lastSubjects.contains(currentSubjectIndex)) {
                                    lastSubjects.add(currentSubjectIndex);
                                }
                                hasSubject=true;
							}else{
								currentSubjectIndex = currentSubjects.indexOf(currentSubject);
								if(!sentenceAdded.contains(currentSubjectIndex)) {
                                    ArrayList<String> subjectArgumentList = new ArrayList<>();
                                    subjectArgumentList.add(rawSentence);
                                    extractedArguments.get(currentSubjectIndex).add(subjectArgumentList);
                                    sentenceAdded.add(currentSubjectIndex);
                                    if(!lastSubjects.contains(currentSubjectIndex)) {
                                        lastSubjects.add(currentSubjectIndex);
                                    }
                                    hasSubject=true;
                                }
							}

						}else{
						    lastSentence = "A : "+rawSentence;
                        }/*else{
						    if(!hasSubject) {
                                if (lastSubjects.size() > 0) {
                                    for (Integer subject : lastSubjects) {
                                        if (!sentenceAdded.contains(subject)) {
                                            int lastArgumentIndex = extractedArguments.get(subject).size() - 1;
                                            extractedArguments.get(subject).get(lastArgumentIndex).add(rawSentence);
                                            sentenceAdded.add(subject);
                                        }
                                    }
                                }
                            }
                        }*/
					}
				}

				System.out.println("------------------------------------------------");
				System.out.println("");

			}

            if(!hasSubject && lastSubjects.size()>0 && lastSentence!=null && !sentenceAdded2){

                for (Integer subject : lastSubjects) {

                    int lastArgumentIndex = extractedArguments.get(subject).size() - 1;
                    extractedArguments.get(subject).get(lastArgumentIndex).add(lastSentence);


                }
                sentenceAdded2=true;
                lastSentence=null;
            }

			System.out.println("currentSubjects : " + currentSubjects.toString());
			System.out.println("argumentsList : "+ extractedArguments.toString());
            System.out.println("lastSubjects : "+lastSubjects.toString());

		}

	}
}
