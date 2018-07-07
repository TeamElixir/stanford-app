package org.elixir.gate;

/*
 * WordNetApp.java
 *
 * Copyright (c) 1998-2003, The University of Sheffield.
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 *
 * Marin Dimitrov, 21/Jan/2003
 *
 * $Id: WordNetApp.java,v 1.5 2004/12/14 14:36:24 niraj Exp $
 */

import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.util.Err;
import gate.util.GateException;
import gate.util.Out;
import gate.wordnet.SemanticRelation;
import gate.wordnet.Synset;
import gate.wordnet.WordNet;
import gate.wordnet.WordSense;
import org.elixir.Constants;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class WordNetApp {

    public static void main(String[] args) {
        // init GATE this is the first thing to be done
        try {
            Gate.setGateHome(new File(Constants.GATE_HOME));
            Gate.init();
            Out.prln("GATE initialised...");
        } catch (GateException gex) {
            Err.prln("cannot initialise GATE...");
            gex.printStackTrace();
            return;
        }

        try {
            // Load the WordNer plugin
            System.out.println("Gate home: " + Gate.getGateHome());
            Gate.getCreoleRegister().registerDirectories(
                    new File(Gate.getGateHome().getAbsolutePath()
                            + "/plugins/WordNet").toURI().toURL());

            // Create an instance of the plugin. See the userguide for the
            // format of the WordNet property file
            FeatureMap fm = Factory.newFeatureMap();
            fm.put("propertyUrl", new URL("file:///z:/wordnet.xml"));
            WordNet wnMain =
                    (WordNet) gate.Factory.createResource(
                            "gate.wordnet.JWNLWordNetImpl", fm);
            Out.prln("WordNet initialised...");

            // 1. synset access - read all senses for a word and compare them
            // with the entries from the WN16 index files get all word senses
            // for "cup" as NOUN
            List<WordSense> senseList = wnMain.lookupWord("cup", WordNet.POS_NOUN);
            assert senseList.size() == 8; // there are only 8 synsets defined

            // the resulting List contains WordSenses for each sense (out of 8)
            // of "cup" a WordSense is a mapping between Word and Synset, i.e. it
            // represent a particular sense of a word
            for (int i = 0; i < senseList.size(); i++) {
                WordSense currSense = senseList.get(i);

                // get the Synset of this paricular sense
                Synset currSynset = currSense.getSynset();
                assert currSynset != null;

                // get various details about the synset
                Out.prln("lemma: " + currSense.getWord().getLemma() + "\n"
                        + "synset = [" + currSynset.getGloss() + "] \n"
                        + "synonyms in synset: " + currSynset.getWordSenses().size()
                        + "\n" + "synset offset = " + currSynset.getOffset() + "\n"
                        + "synset is UniqueBeginner? = ["
                        + currSynset.isUniqueBeginner() + "] \n" + "synset = ["
                        + _getSynsetMembers(currSynset) + "] \n"
                        + "-----------------------------------");
            }

            // 2. hypernymy - traverse upwards the hierarchy starting from some
            // word compare the result with the WN16 index files get all synsets for
            // "cup" start from the beginning
            senseList = wnMain.lookupWord("bank", WordNet.POS_NOUN);
            Iterator<WordSense> itSenses = senseList.iterator();
            while (itSenses.hasNext()) {
                WordSense currSense = itSenses.next();
                Synset currSynset = currSense.getSynset();
                _processSynset(currSynset, "");
                Out.prln("======================================== \n");
            }
        } catch (Exception ex) {
            ex.printStackTrace(Err.getPrintWriter());
        }
    }

    /**
     * represnet the synset as String
     *
     * @param s - Synset to print
     * @return - the Synset members as a comma delimited string
     */
    private static String _getSynsetMembers(Synset s) {
        assert s != null;
        StringBuffer result = new StringBuffer();
        result.append("#");
        // get all synset members
        // i.e. the WordSenses in this Synset
        List<WordSense> synonyms = s.getWordSenses();
        Iterator<WordSense> itSynonyms = synonyms.iterator();
        while (itSynonyms.hasNext()) {
            WordSense currSynonym = itSynonyms.next();
            // get the Word of this sense, and its lemma
            result.append(currSynonym.getWord().getLemma());
            result.append(", ");
        }
        result.delete(result.length() - 2, result.length());
        result.append("#");
        return result.toString();
    }

    private static void _processSynset(Synset s, String prefix)
            throws gate.wordnet.WordNetException {
        assert s != null;
        Out.prln(prefix + _getSynsetMembers(s));
        // get the hypernym relations (semantic) of the current synset
        // if none then the list is empty
        List<SemanticRelation> semRelations = s.getSemanticRelations(SemanticRelation.REL_HYPERNYM);
        Iterator<SemanticRelation> it = semRelations.iterator();
        while (it.hasNext()) {
            SemanticRelation currHypernymRel = it.next();
            // each SemRelation has a Source and Target, the Target of a Hypernymy
            // relation is the hypernym of the current synset
            Synset currHypernym = currHypernymRel.getTarget();
            // continue upwards recursively, shift hypernym members right (prefix)
            _processSynset(currHypernym, prefix + "----");
        }
    }
}
