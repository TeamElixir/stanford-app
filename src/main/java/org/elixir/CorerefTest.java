package org.elixir;

import java.util.Properties;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class CorerefTest {
    public static void main(String[] args) throws Exception {
        Annotation document = new Annotation("petitioners, Timothy Catlett, Russell Overton, Levy Rouse, Kelvin Smith, Charles and Christopher Turner, and Clifton Yarborough--and several others were indicted for the kidnaping, robbery, and murder of Catherine Fuller. At trial, the Government advanced the theory that Fuller was attacked by a large group of individuals. Its evidentiary centerpiece consisted of the testimony of Calvin Alston and Harry Bennett, who confessed to participating in a group attack and cooperated with the Government in return for leniency. Several other Government witnesses corroborated aspects of Alston's and Bennett's testimony. Melvin Montgomery testified that he was in a park among a group of people, heard someone say they were \"going to get that one,\" saw petitioner Overton pointing to Fuller, and saw several persons, including some petitioners, cross the street in her direction. Maurice Thomas testified that he saw the attack, identified some petitioners as participants, and later overheard petitioner Catlett say that they \"had to kill her.\" .The Government does not contest petitioners' claim that the withheld evidence was \"favorable to the defense.\" Petitioners and the Government, however, do contest the materiality of the undisclosed Brady information. Such \"evidence is 'material' . . . when there is a reasonable probability that, had the evidence been disclosed, the result of the proceeding would have been different.");
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,mention,coref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        pipeline.annotate(document);
        System.out.println("---");
        System.out.println("coref chains");
        for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
            System.out.println("\t" + cc);
        }
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            System.out.println("---");
            System.out.println("mentions");
            for (Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
                System.out.println("\t" + m);
            }
        }
    }
}
