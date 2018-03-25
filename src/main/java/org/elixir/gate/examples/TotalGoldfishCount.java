/**
 * A standalone application that makes use of GATE PR's as well as a user defined one.
 * The program displays the features of each document as created by the PR "Goldfish".
 *
 * @author Andrew Golightly (acg4@cs.waikato.ac.nz)
 * -- last updated 16/05/2003
 */

package org.elixir.gate.examples;

import gate.Gate;
import gate.Document;
import gate.util.GateException;
import gate.Factory;
import gate.creole.SerialAnalyserController;

import java.util.Iterator;
import java.io.File;

public class TotalGoldfishCount {

	private gate.Corpus corpus;

	public TotalGoldfishCount(String[] files) throws Exception {
		Gate.init();
		Gate.getCreoleRegister().registerDirectories(
				new File(System.getProperty("user.dir")).toURL());

		// add files to a corpus
		System.out.println("\n== OBTAINING DOCUMENTS ==");
		createCorpus(files);

		System.out.println("\n== USING GATE TO PROCESS THE DOCUMENTS ==");
		String[] processingResources = { "gate.creole.tokeniser.DefaultTokeniser",
				"gate.creole.splitter.SentenceSplitter",
				"andrewgolightly.nlp.gate.prs.Goldfish" };
		runProcessingResources(processingResources);

		System.out.println("\n== DOCUMENT FEATURES ==");
		displayDocumentFeatures();

		System.out.println("\nDemo done... :)");
	}

	private void createCorpus(String[] files) throws GateException {
		corpus = Factory.newCorpus("Transient Gate Corpus");

		for (int file = 0; file < files.length; file++) {
			System.out.print("\t " + (file + 1) + ") " + files[file]);
			try {
				corpus.add(Factory.newDocument(new File(files[file]).toURL()));
				System.out.println(" -- success");
			}
			catch (gate.creole.ResourceInstantiationException e) {
				System.out.println(" -- failed (" + e.getMessage() + ")");
			}
			catch (Exception e) {
				System.out.println(" -- " + e.getMessage());
			}
		}
	}

	private void runProcessingResources(String[] processingResource)
			throws GateException {
		SerialAnalyserController pipeline = (SerialAnalyserController) Factory
				.createResource("gate.creole.SerialAnalyserController");

		for (int pr = 0; pr < processingResource.length; pr++) {
			System.out.print("\t* Loading " + processingResource[pr] + " ... ");
			pipeline.add((gate.LanguageAnalyser) Factory
					.createResource(processingResource[pr]));
			System.out.println("done");
		}

		System.out.print("Creating corpus from documents obtained...");
		pipeline.setCorpus(corpus);
		System.out.println("done");

		System.out.print("Running processing resources over corpus...");
		pipeline.execute();
		System.out.println("done");
	}

	private void displayDocumentFeatures() {
		Iterator documentIterator = corpus.iterator();

		while (documentIterator.hasNext()) {
			Document currDoc = (Document) documentIterator.next();
			System.out.println("The features of document \""
					+ currDoc.getSourceUrl().getFile() + "\" are:");
			gate.FeatureMap documentFeatures = currDoc.getFeatures();

			Iterator featureIterator = documentFeatures.keySet().iterator();
			while (featureIterator.hasNext()) {
				String key = (String) featureIterator.next();
				System.out.println("\t*) " + key + " --> " + documentFeatures.get(key));
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		if (args.length == 0)
			System.err.println("USAGE: java TotalGoldfishCount <file1> <file2> ...");
		else
			try {
				new TotalGoldfishCount(args);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
	}
}
