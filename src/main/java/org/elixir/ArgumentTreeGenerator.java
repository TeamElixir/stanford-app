package org.elixir;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;
import org.elixir.models.Node;
import org.elixir.utils.NLPUtils;
import org.elixir.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class ArgumentTreeGenerator {

	//an Array to store the names of Legal Persons
	private static final ArrayList<String> SUBJECT_LIST = new ArrayList<>(
			Arrays.asList("Petitioner", "Government", "Defendant"));

	//nodes of the argument tree
	public static ArrayList<Node> nodes = new ArrayList<>();

	public static boolean alreadyAdded = false;

	public static ArrayList<String> sentencesInCase = new ArrayList<>();

	//an Array to store the parties in the current court case
	private static ArrayList<String> currentSubjects = new ArrayList<>();

	//an Array list which keeps the extracted arguments
	private static ArrayList<ArrayList<ArrayList<String>>> extractedArguments = new ArrayList<>();

	//an Array to store the recently appeared subjects
	private static ArrayList<Integer> lastSubjects = new ArrayList<>();

	//to find out whether the current sentence has a subject
	private static boolean hasSubject = false;

	//to keep track on previous sentence
	private static String lastSentence = null;

	private static String finalRawSentence;

	//to keep track on the most recent annotated sentence
	private static CoreMap lastAnnotatedSentence = null;

	//to keep track of persons presented in names
	private static ArrayList<String> persons = new ArrayList<>();

	public static void main(String[] args) {

		// creates a StanfordCoreNLP object, with annotators
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie,ner");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		//read case
		String filePath = new File("").getAbsolutePath();
		filePath += "/src/main/resources/case2.txt";
		String textRaw = Utils.readFile(filePath);

		String[] splitted = textRaw.split("\n");

		StringBuilder inputBuilder = new StringBuilder();
		for (int i = 2; i < splitted.length; i++) {
			inputBuilder.append(splitted[i]);
		}

		// this the text after the Held: paragraph
		String text = inputBuilder.toString();

		// Held: paragraph
		String held = splitted[0].split("Held: ")[1];
		System.out.println(held);

		ArrayList<String> rawSentences = new ArrayList<>();
		Document doc = new Document(text);
		List<Sentence> sentences1 = doc.sentences();
		for (Sentence sentence : sentences1) {
			//extract the sentences in the given case
			rawSentences.add(sentence.toString());
		}

		for (String rawSentence : rawSentences) {
			//replace words that misleads triple extraction
			String ss = rawSentence.replaceAll("(that,|that|'s)", "");

			finalRawSentence = rawSentences.get(rawSentences.size() - 1);
			boolean sentenceAdded2 = false;
			boolean alreadyAdded = false;
			int firstSubjectLocation = -1;
			int differenceBetweenSubjects = 0;


			/*if(!hasSubject && lastSubjects.size()>0 && lastSentence!=null){
                for (Integer subject : lastSubjects) {

                        int lastArgumentIndex = extractedArguments.get(subject).size() - 1;
                        extractedArguments.get(subject).get(lastArgumentIndex).add(lastSentence);

                }
                lastSentence=null;
            }*/

			//still no idea whether the sentence has a subject
			hasSubject = false;

			// create an empty Annotation just with the given text
			Annotation document = new Annotation(ss);

			// run all Annotators on this text
			pipeline.annotate(document);

			// these are all the sentences in this document
			// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
			List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

			for (CoreMap sentence : sentences) {

				lastAnnotatedSentence = sentence;

				//keep track for which subjects the sentence is belonged to
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

				//extracting triples from the sentence
				Collection<RelationTriple> triples =
						sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);

				for (RelationTriple triple : triples) {

					String sentenceSubject = triple.subjectLemmaGloss();

					for (int i = 0; i < SUBJECT_LIST.size(); i++) {

						// check whether the sentenceSubject is in SUBJECT_LIST

						if (sentenceSubject.toLowerCase().indexOf(SUBJECT_LIST.get(i).toLowerCase()) != -1) {

							//this sentence has a subject, therefore no need to keep track of last subjects, the sentence is already connected to this subject

							if (!hasSubject) {
								lastSubjects.clear();
							}

							//keeping track of the subject of this sentence
							String currentSubject = SUBJECT_LIST.get(i);

							int currentSubjectIndex = -1;

							//check whether this subject is already not in this case
							if (!currentSubjects.contains(currentSubject)) {

								//add the subject to current subjects list of this case
								currentSubjects.add(currentSubject);

								//get the index of the current subject in the list which contains the subjects of this case
								currentSubjectIndex = currentSubjects.indexOf(currentSubject);

								if (!lastSubjects.contains(currentSubjectIndex)) {
									lastSubjects.add(currentSubjectIndex);
								}

								if (lastSubjects.size() == 1) {
									firstSubjectLocation = rawSentence.toLowerCase()
											.indexOf(currentSubjects.get(lastSubjects.get(0)).toLowerCase());
								}

								if (lastSubjects.size() > 1) {
									int currentSubjectLocation = rawSentence.toLowerCase()
											.indexOf(currentSubjects.get(currentSubjectIndex).toLowerCase());
									if (currentSubjectLocation > firstSubjectLocation) {
										differenceBetweenSubjects = currentSubjectLocation - firstSubjectLocation;
									} else {
										differenceBetweenSubjects = firstSubjectLocation - currentSubjectLocation;
									}
								}

								if (differenceBetweenSubjects < 4) {

									//creates a list for the subject (which is new) an arraylist to store argument lists which belongs to the subject
									ArrayList<ArrayList<String>> argumentList = new ArrayList<>();

									//creates a list to store an argument which belongs to this subject, this list may also contains sentences which supports this argument
									ArrayList<String> subjectArgumentList = new ArrayList<>();

									//add the sentence as it is in raw sentence
									subjectArgumentList.add(rawSentence);

									argumentList.add(subjectArgumentList);

									//add the list of argumentlist which belongs to this subject for the list which contains all the extracted arguments
									extractedArguments.add(argumentList);

									//to keep track that this particular sentence was already added as a sentence which is belonged to this subject
									sentenceAdded.add(currentSubjectIndex);

									//add the subject to last subjects, will be helpful when appending sentences

									//keep track that this sentence already has subject
									hasSubject = true;
									alreadyAdded = true;
								}

							}
							//when the subject is already in the current subject list of this court case what is needed to be done
							else {

								//find out the index of the subject in the current subject list of this court case
								currentSubjectIndex = currentSubjects.indexOf(currentSubject);

								//if this sentence is not added as a sentence which belongs to this subject, following
								// tasks have to be performed. This is because one sentence can generate multiple
								// triples. As a result there will be more than one triple with the same subject for a
								// particular sentence. So the if condition is to remove the redundancy.
								if (!sentenceAdded.contains(currentSubjectIndex)) {

									//add the subject to last subjects, will be helpful when appending sentences
									if (!lastSubjects.contains(currentSubjectIndex)) {
										lastSubjects.add(currentSubjectIndex);
									}
									if (lastSubjects.size() == 1) {
										firstSubjectLocation = rawSentence.toLowerCase()
												.indexOf(currentSubjects.get(lastSubjects.get(0)).toLowerCase());
									}

									if (lastSubjects.size() > 1) {
										int currentSubjectLocation = rawSentence.toLowerCase()
												.indexOf(currentSubjects.get(currentSubjectIndex).toLowerCase());
										if (currentSubjectLocation > firstSubjectLocation) {
											differenceBetweenSubjects = currentSubjectLocation - firstSubjectLocation;
										} else {
											differenceBetweenSubjects = firstSubjectLocation - currentSubjectLocation;
										}
									}

									if (differenceBetweenSubjects < 4) {

										// add a new argument list, in which supporting sentences can be appended
										ArrayList<String> subjectArgumentList = new ArrayList<>();
										subjectArgumentList.add(rawSentence);

										//add this argument list to the list of argument lists which is
										// belong to this subject
										extractedArguments.get(currentSubjectIndex).add(subjectArgumentList);

										//to keep track that this particular sentence was already added as a sentence which
										// is belonged to this subject
										sentenceAdded.add(currentSubjectIndex);

										//keep track that this sentence already has subject
										hasSubject = true;
										alreadyAdded = true;
									}
								}
							}

						} else {

							//the subject of this sentence is not a legal person, so add this as the most recent sentence

							lastSentence = "A : " + rawSentence;

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

			//check whether the sentence has a subject, has a previous sentence and whether this sentence is already added.
			//if a sentence doesn't have a subject which is a legal person, has not added already,
			// that means this is a new sentence. As subject is not a legal person, the sentence may or may have another
			// subject(name of a person).

			sentencesInCase.add(rawSentence);

			if (!hasSubject && lastSentence != null && !sentenceAdded2) {

				//if there is a previous sentence and that previous sentence has a subject ot that previous sentence is
				//a descendant of a sentence which has a subject, this sentence will also be appended as a descendant of
				// that sentence. (if there are more than one subject, this will cover all these subjects)

				if (lastSubjects.size() > 0) {

					for (Integer subject : lastSubjects) {

						int lastArgumentIndex = extractedArguments.get(subject).size() - 1;
						extractedArguments.get(subject).get(lastArgumentIndex).add(rawSentence);

					}
					sentenceAdded2 = true;

					//clear the sentence
					lastSentence = null;
				} else {

					if (lastAnnotatedSentence != null) {

						//check whether the subject of this sentence is a name of a person
						ArrayList<String> localPersons = NLPUtils.getPersonList(lastAnnotatedSentence);

						if (localPersons.size() > 0) {

							//first person is taken as the subject
							String localPerson = localPersons.get(0);

							if (!persons.contains(localPerson) && !currentSubjects.contains(localPerson)) {
								persons.add(localPerson);

								//add the person as a subject in this case
								currentSubjects.add(localPerson);

								if (currentSubjects.indexOf(localPerson) != -1) {
									ArrayList<ArrayList<String>> argumentList = new ArrayList<>();
									ArrayList<String> subjectArgumentList = new ArrayList<>();
									subjectArgumentList.add(rawSentence);
									argumentList.add(subjectArgumentList);
									extractedArguments.add(argumentList);
								}

							}
						}

						lastSentence = null;

					}
				}
			}

			System.out.println("currentSubjects : " + currentSubjects.toString());
			System.out.println("argumentsList : " + extractedArguments.toString());
			System.out.println("lastSubjects : " + lastSubjects.toString());
		}

		System.out.println("\n This is the terminal output");

		Node rootNode = new Node("-1", "", held);
		// TODO: Replace 'title' with the 'held' part or the like
		nodes.add(rootNode);
		for (int j = 0; j < currentSubjects.size(); j++) {
			String subject = currentSubjects.get(j);
			String subjectId = Integer.toString(j);

			Node subjectNode = new Node(subjectId, "-1", subject);
			nodes.add(subjectNode);

			System.out.println(subject + " arguments : ");
			for (int k = 0; k < extractedArguments.get(currentSubjects.indexOf(subject)).size(); k++) {
				ArrayList<String> set1 = extractedArguments.get(currentSubjects.indexOf(subject)).get(k);
				System.out.println("\t" + set1.get(0));

				String argumentId = Integer.toString(j) + Integer.toString(k);
				Node argumentNode = new Node(argumentId, subjectId, set1.get(0));
				nodes.add(argumentNode);

				for (int i = 1; i < set1.size(); i++) {

					String conditionId = Integer.toString(j) + Integer.toString(k) + Integer.toString(i);
					Node conditionNode = new Node(conditionId, argumentId, set1.get(i));
					nodes.add(conditionNode);

					System.out.println("\t" + "\t" + set1.get(i).substring(3));
				}
			}
		}

		System.out.println("\n This is person list");
		System.out.println(persons.toString());
		System.out.println("\n This is nodes list");
		System.out.println("\n ");
		System.out.println("Nodes: ");
		for (Node node : nodes) {
			System.out.println(node);
		}

		System.out.println("This is raw sentences list");
		for (String rawSentence : rawSentences) {
			System.out.println(rawSentence);
		}

		NodeRelationsDeterminer.initialize();
		NodeRelationsDeterminer.checkRelationships();
		nodes = NodeRelationsDeterminer.nodesRelations;

        /*System.out.println("sentences in cases");
        System.out.println(sentencesInCase.toString());*/

		try {
			Utils.writeToJson(nodes);
		}
		catch (IOException e) {
			System.out.println("Error writing extracted arguments to JSON file");
			e.printStackTrace();
		}

	}   // main

	public static ArrayList<ArrayList<ArrayList<String>>> getExtractedArguments() {
		//extracted arguments
		return extractedArguments;
	}

}
