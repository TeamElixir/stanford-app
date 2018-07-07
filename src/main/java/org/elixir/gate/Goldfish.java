/**
 * Counts the number of times the word "Goldfish" appears in a sentence. That total is
 * added as a feature to every sentence annotation.
 * Also adds summary information to each document, namely:
 * - total number of characters in document
 * - total number of tokens in document
 * - total number of words in document
 * - total number of sentences
 * - total "Goldfish" count
 *
 * @author Andrew Golightly (acg4@cs.waikato.ac.nz)
 * -- last updated 18/05/2003
 */

package org.elixir.gate;

import gate.Annotation;
import gate.AnnotationSet;
import gate.creole.ANNIEConstants;
import gate.creole.ExecutionException;

import java.util.Iterator;

public class Goldfish extends gate.creole.AbstractLanguageAnalyser {

    private String inputASname, outputASname;

    public String getinputASname() {
        return inputASname;
    }

    public void setinputASname(String inputASname) {
        this.inputASname = inputASname;
    }

    public String getoutputASname() {
        return outputASname;
    }

    public void setoutputASname(String outputASname) {
        this.outputASname = outputASname;
    }

    public void execute() throws ExecutionException {
        gate.Document doc = getDocument();
        int totalGoldfishCount = 0;

        doc.getFeatures().clear();

        AnnotationSet inputAnnSet = (inputASname == null || inputASname.length() == 0)
                ? doc.getAnnotations()
                : doc.getAnnotations(inputASname);

        AnnotationSet outputAnnSet = (outputASname == null || outputASname.length() == 0)
                ? doc.getAnnotations()
                : doc.getAnnotations(outputASname);

        doc.getFeatures().put("Number of characters",
                new Integer(doc.getContent().toString().length()).toString());
        try {
            doc.getFeatures().put(
                    "Number of tokens",
                    new Integer(inputAnnSet.get(ANNIEConstants.TOKEN_ANNOTATION_TYPE)
                            .size()).toString());
        } catch (NullPointerException e) {
            throw new ExecutionException(
                    "You need to run the English Tokenizer first!");
        }
        try {
            doc.getFeatures().put(
                    "Number of sentences",
                    new Integer(inputAnnSet.get(
                            ANNIEConstants.SENTENCE_ANNOTATION_TYPE).size())
                            .toString());
        } catch (NullPointerException e) {
            throw new ExecutionException(
                    "You need to run the Sentence Splitter first!");
        }

        // iterate through the sentences
        Iterator sentenceIterator = inputAnnSet.get(
                ANNIEConstants.SENTENCE_ANNOTATION_TYPE).iterator(), tokenIterator;
        int wordCount = 0;
        while (sentenceIterator.hasNext()) {
            Annotation sentenceAnnotation = (Annotation) sentenceIterator.next();
            tokenIterator = inputAnnSet.get(ANNIEConstants.TOKEN_ANNOTATION_TYPE,
                    sentenceAnnotation.getStartNode().getOffset(),
                    sentenceAnnotation.getEndNode().getOffset()).iterator();

            // iterate through the tokens in the current sentence
            int sentenceGoldfishCount = 0;
            String word;

            while (tokenIterator.hasNext()) {
                Annotation tokenAnnotation = (Annotation) tokenIterator.next();
                if (tokenAnnotation.getFeatures().get(
                        ANNIEConstants.TOKEN_KIND_FEATURE_NAME).equals("word"))
                    wordCount++;
                word = (String) tokenAnnotation.getFeatures().get(
                        ANNIEConstants.TOKEN_STRING_FEATURE_NAME);
                if (word.equals("Goldfish")) {
                    try {
                        outputAnnSet.add(tokenAnnotation.getStartNode().getOffset(),
                                tokenAnnotation.getEndNode().getOffset(), "Goldfish",
                                gate.Factory.newFeatureMap());
                    } catch (gate.util.InvalidOffsetException ioe) {
                        throw new ExecutionException(ioe);
                    }
                    sentenceGoldfishCount++;
                }
            }

            sentenceAnnotation.getFeatures().put(new String("Goldfish Count"),
                    new Integer(sentenceGoldfishCount));

            totalGoldfishCount += sentenceGoldfishCount;
        }

        doc.getFeatures().put("Number of words", new Integer(wordCount).toString());
        doc.getFeatures().put("Total \"Goldfish\" count",
                new Integer(totalGoldfishCount).toString());
    }
}

