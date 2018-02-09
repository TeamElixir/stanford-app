package org.elixir;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.simple.*;
import org.elixir.models.Node;
import org.elixir.utils.NLPUtils;
import org.elixir.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ArgumentTreeGenerator {

    private static final ArrayList<String> SUBJECT_LIST = new ArrayList<>(
            Arrays.asList("Petitioner", "Government", "Defendant"));                        //an Array to store the names of Legal Persons

    private static ArrayList<String> currentSubjects = new ArrayList<>();                   //an Array to store the parties in the current court case

    private static ArrayList<ArrayList<ArrayList<String>>> extractedArguments = new ArrayList<>();    //an Array list which keeps the extracted arguments

    private static ArrayList<Integer> lastSubjects = new ArrayList<>();                    //an Array to store the recently appeared subjects

    private static boolean hasSubject = false;                                              //to find out whether the current sentence has a subject

    private static String lastSentence = null;                                              //to keep track on previous sentence

    private static String finalRawSentence;

    private static CoreMap lastAnnotatedSentence = null;                                   //to keep track on the most recent annotated sentence

    private static ArrayList<String> persons = new ArrayList<>();                          //to keep track of persons presented in names

    private static ArrayList<Node> nodes = new ArrayList<>();                              //nodes of the argument tree

    public static ArrayList<ArrayList<ArrayList<String>>> getExtractedArguments() {
        return extractedArguments;                                                         //extracted arguments
    }

    public static void main(String[] args) {

        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog, openie, ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        String filePath = new File("").getAbsolutePath();
        filePath+="/src/main/resources/case1.txt";                                                  //read case
        String textRaw = Utils.readFile(filePath);

        String[] splitted = textRaw.split("\n");

        StringBuilder inputBuilder = new StringBuilder();
        for (int i = 2; i <splitted.length ; i++) {
            inputBuilder.append(splitted[i]);
        }

        // this the text after the Held: paragraph
        String text = inputBuilder.toString();

        // Held: paragraph
        String held = splitted[0].split("Held: ")[1];
        System.out.println(held);

         //read some text in the text variable
       /* String text =
                "When a defendant claims that his counsel's deficient performance deprived him of a trial by causing him to accept a plea, the defendant can show prejudice by demonstrating a \"reasonable probability that,"
                        +
                        " but for counsel's errors, he would not have pleaded guilty and would have insisted on going to trial.\" Hill v. Lockhart,"
                        +
                        " 474 U. S. 52, 59.\n" +
                        "\n" +
                        "     Lee contends that he can make this showing because he never would have accepted a guilty plea had he known the "
                        +
                        "result would be deportation. The Government contends that Lee cannot show prejudice from accepting a plea where his only "
                        +
                        "hope at trial was that something unexpected and unpredictable might occujava -Xmx1024m com.xyz.TheClassNamer that would lead to acquittal. Pp. 5-8."
                        +
                        "The Government makes two errors in urging the adoption of a per se rule that a defendant with no viable defense cannot "
                        +
                        "show prejudice from the denial of his right to trial. First, it forgets that categorical rules are ill suited to an "
                        +
                        "inquiry that demands a \"case-by-case examination\" of the \"totality of the evidence.\" Williams v. Taylor,"
                        +
                        " 529 U. S. 362, 391 (internal quotation marks omitted); Strickland, 466 U. S., at 695. More fundamentally, "
                        +
                        "it overlooks that the Hill v. Lockhart inquiry focuses on a defendant's decisionmaking, which may not turn "
                        +
                        "solely on the likelihood of conviction after trial.";*/

        ArrayList<String> rawSentences = new ArrayList<>();
        Document doc = new Document(text);
        List<Sentence> sentences1 = doc.sentences();
        for (Sentence sentence : sentences1) {
            rawSentences.add(sentence.toString());                          //extract the sentences in the given case
        }

        for (String rawSentence : rawSentences) {

            String ss = rawSentence.replaceAll("(that,|that|'s)", "");            //replace words that misleads triple extraction
            finalRawSentence = rawSentences.get(rawSentences.size() - 1);
            boolean sentenceAdded2 = false;

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


                                //creates a list for the subject (which is new) an arraylist to store argument lists which belongs to the subject
                                ArrayList<ArrayList<String>> argumentList = new ArrayList<>();

                                //creates a list to store an argument which belongs to this subject, this list may also contains sentences which supports this argument
                                ArrayList<String> subjectArgumentList = new ArrayList<>();

                                //add the sentence as it is in raw sentence
                                subjectArgumentList.add(rawSentence);

                                argumentList.add(subjectArgumentList);

                                //add the list of argumentlist which belongs to this subject for the list which contains all the extracted arguments
                                extractedArguments.add(argumentList);

                                //get the index of the current subject in the list which contains the subjects of this case
                                currentSubjectIndex = currentSubjects.indexOf(currentSubject);

                                //to keep track that this particular sentence was already added as a sentence which is belonged to this subject
                                sentenceAdded.add(currentSubjectIndex);

                                //add the subject to last subjects, will be helpful when appending sentences
                                if (!lastSubjects.contains(currentSubjectIndex)) {
                                    lastSubjects.add(currentSubjectIndex);
                                }

                                //keep track that this sentence already has subject
                                hasSubject = true;
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
                                    // add a new argument list, in which supporting sentences can be appended
                                    ArrayList<String> subjectArgumentList = new ArrayList<>();
                                    subjectArgumentList.add(rawSentence);

                                    //add this argument list to the list of argument lists which is
                                    // belong to this subject
                                    extractedArguments.get(currentSubjectIndex).add(subjectArgumentList);

                                    //to keep track that this particular sentence was already added as a sentence which
                                    // is belonged to this subject
                                    sentenceAdded.add(currentSubjectIndex);

                                    //add the subject to last subjects, will be helpful when appending sentences
                                    if (!lastSubjects.contains(currentSubjectIndex)) {
                                        lastSubjects.add(currentSubjectIndex);
                                    }

                                    //keep track that this sentence already has subject
                                    hasSubject = true;
                                }
                            }

                        } else {

                            //the subject of this sentence is not a legal person, so add this as the mostrecentsentence

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

            if (!hasSubject && lastSentence != null && !sentenceAdded2) {

                //if there is a previous sentence and that previous sentence has a subject ot that previous sentence is
                //a descendant of a sentence which has a subject, this sentence will also be appended as a descendant of
                // that sentence. (if there are more than one subject, this will cover all these subjects)

                if (lastSubjects.size() > 0) {


                    for (Integer subject : lastSubjects) {

                        int lastArgumentIndex = extractedArguments.get(subject).size() - 1;
                        extractedArguments.get(subject).get(lastArgumentIndex).add(lastSentence);

                    }
                    sentenceAdded2 = true;

                    //clear the sentence
                    lastSentence = null;
                } else {


                    if (lastAnnotatedSentence != null) {

                        //check whether the subject of this sentence is a name of a person
                        ArrayList<String> localPersons = NLPUtils.getPersonList(lastAnnotatedSentence);

                        if(localPersons.size()>0) {

                            //first person is taken as the subject
                            String localPerson = localPersons.get(0);

                            if(!persons.contains(localPerson) && !currentSubjects.contains(localPerson)) {
                                persons.add(localPerson);

                                //add the person as a subject in this case
                                currentSubjects.add(localPerson);

                                if (currentSubjects.indexOf(localPerson)!=-1){
                                    ArrayList<ArrayList<String>> argumentList = new ArrayList<>();
                                    ArrayList<String> subjectArgumentList = new ArrayList<>();
                                    subjectArgumentList.add(rawSentence);
                                    argumentList.add(subjectArgumentList);
                                    extractedArguments.add(argumentList);
                                }

                            }
                        }


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
                    Node conditionNode = new Node(conditionId, argumentId, set1.get(i).substring(3));
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

        try {
            Utils.writeToJson(nodes);
        } catch (IOException e) {
            System.out.println("Error writing extracted arguments to JSON file");
            e.printStackTrace();
        }
    }   // main

}
