package edu.stanford.nlp.pipeline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.JMWEAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.Token;
import edu.mit.jmwe.detect.CompositeDetector;
import edu.mit.jmwe.detect.Consecutive;
import edu.mit.jmwe.detect.Exhaustive;
import edu.mit.jmwe.detect.IMWEDetector;
import edu.mit.jmwe.detect.InflectionPattern;
import edu.mit.jmwe.detect.MoreFrequentAsMWE;
import edu.mit.jmwe.detect.ProperNouns;
import edu.mit.jmwe.index.IMWEIndex;
import edu.mit.jmwe.index.MWEIndex;

/**
 * Annotator to capture Multi-Word Expressions (MWE) via "jMWE", see
 * http://projects.csail.mit.edu/jmwe/ for author, copyright, license,
 * dependencies and other information on "jMWE". 
 * 
 * As noted on http://projects.csail.mit.edu/jmwe/ jMWE 1.0.2 is distributed 
 * by its authors under the Creative Commons Attribution 4.0 International License: 
 * http://creativecommons.org/licenses/by/4.0/
 * Hereby proper copyright acknowledgement is made as required.
 * 
 * Name, copyright and other acknowledgements for jMWE: Finlayson, M.A. and Kulkarni,
 * N. (2011) Detecting Multi-Word Expressions Improves Word Sense
 * Disambiguation, Proceedings of the 8th Workshop on Multiword Expressions,
 * Portland, OR. pp. 20-24. 
 * Kulkarni, N. and Finlayson, M.A. jMWE: A Java
 * Toolkit for Detecting Multi-Word Expressions, Proceedings of the 8th Workshop
 * on Multiword Expressions, Portland, OR. pp. 122-124.
 * 
 * @author Tomasz Oliwa
 *
 */
public class JMWEAnnotator implements Annotator {

    // print verbose output
    private final boolean verbose;
    // the class name of the detector
    private final String detectorName;
    // the index data for jMWE, loaded from for instance the file
    // mweindex_wordnet3.0_Semcor1.6.data
    private final IMWEIndex index;
    // the String that will replace an underscore in the signal, necessary since
    // jMWE throws an Exception if an underscore is part of the signal
    private final String underscoreReplacement;

    /**
     * Annotator to capture Multi-Word Expressions (MWE).
     * @param name
     *            annotator name
     * @param props
     *            the properties
     */
    public JMWEAnnotator(String name, Properties props) {
        // set verbosity
        this.verbose = PropertiesUtils.getBool(props, "customAnnotatorClass.jmwe.verbose", false);
        // set underscoreReplacement
        if (!PropertiesUtils.hasProperty(props, "customAnnotatorClass.jmwe.underscoreReplacement")) {
            throw new RuntimeException("No customAnnotatorClass.jmwe.underscoreReplacement key in properties found");
        }
        underscoreReplacement = (String) props.get("customAnnotatorClass.jmwe.underscoreReplacement");
        if (underscoreReplacement.contains("_")) {
            throw new RuntimeException("The underscoreReplacement contains an underscore character");
        }
        // set index
        if (!PropertiesUtils.hasProperty(props, "customAnnotatorClass.jmwe.indexData")) {
            throw new RuntimeException("No customAnnotatorClass.jmwe.indexData key in properties found");
        }
        File indexFile = new File((String) props.get("customAnnotatorClass.jmwe.indexData"));
        if (!indexFile.exists()) {
            throw new RuntimeException("index file " + indexFile.getAbsoluteFile() + " does not exist");
        }

        this.index = new MWEIndex(indexFile);
        // set detector
        if (!PropertiesUtils.hasProperty(props, "customAnnotatorClass.jmwe.detector")) {
            throw new RuntimeException("No customAnnotatorClass.jmwe.detector key in properties found");
        }
        this.detectorName = (String) props.get("customAnnotatorClass.jmwe.detector");

        if (this.verbose) {
            System.out.println("verbose: " + this.verbose);
            System.out.println("underscoreReplacement: " + this.underscoreReplacement);
            System.out.println("indexData: " + this.index);
            System.out.println("detectorName: " + this.detectorName);
        }
    }

    @Override
    public void annotate(Annotation annotation) {
        if (annotation.has(CoreAnnotations.SentencesAnnotation.class)) {
            // open index
            try {
                index.open();
            } catch (IOException e) {
                throw new RuntimeException("unable to open IMWEIndex index");
            }
            // create the detector
            IMWEDetector detector = getDetector(index, detectorName);
            // capture jMWE per sentence
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                List<IMWE<IToken>> mwes = getjMWEInSentence(sentence, index, detector, verbose);
                sentence.set(JMWEAnnotation.class, mwes);
            }
            // close index
            index.close();
        } else {
            throw new RuntimeException("unable to find words/tokens in: " + annotation);
        }
    }

    @Override
    public Set<Requirement> requirementsSatisfied() {
        return Collections.singleton(NER_REQUIREMENT);
    }

    @Override
    public Set<Requirement> requires() {
        return Annotator.REQUIREMENTS.get(STANFORD_NER);
    }

    /**
     * Get the MWE of the sentence.
     * 
     * @param sentence
     *            the sentence
     * @param index
     *            the index
     * @param detector
     *            the detector
     * @param verbose
     *            the verbosity
     * @return the MWE of the sentence
     */
    public List<IMWE<IToken>> getjMWEInSentence(CoreMap sentence, IMWEIndex index, IMWEDetector detector,
            boolean verbose) {
        List<IToken> tokens = getITokens(sentence.get(CoreAnnotations.TokensAnnotation.class));
        List<IMWE<IToken>> mwes = detector.detect(tokens);
        if (verbose) {
            for (IMWE<IToken> token : mwes) {
                System.out.println("IMWE<IToken>: " + token);
            }
        }
        return mwes;
    }

    /**
     * Get the detector.
     * 
     * @param index
     *            the index
     * @param detector 
     *            the detector, \"Consecutive\", \"Exhaustive\", \"ProperNouns\", \"Complex\" or \"CompositeConsecutiveProperNouns\" are supported
     * @return the detector
     */
    public IMWEDetector getDetector(IMWEIndex index, String detector) {
        IMWEDetector iMWEdetector = null;
        switch (detector) {
        case "Consecutive":
            iMWEdetector = new Consecutive(index);
            break;
        case "Exhaustive":
            iMWEdetector = new Exhaustive(index);
            break;
        case "ProperNouns":
            iMWEdetector = ProperNouns.getInstance();
            break;
        case "Complex":
            iMWEdetector = new CompositeDetector(ProperNouns.getInstance(),
                    new MoreFrequentAsMWE(new InflectionPattern(new Consecutive(index))));
            break;
        case "CompositeConsecutiveProperNouns":
            iMWEdetector = new CompositeDetector(new Consecutive(index), ProperNouns.getInstance());            
            break;
        default:
            throw new IllegalArgumentException("Invalid detector argument " + detector
                    + ", only \"Consecutive\", \"Exhaustive\", \"ProperNouns\", \"Complex\" or \"CompositeConsecutiveProperNouns\" are supported.");
        }
        return iMWEdetector;
    }

    /**
     * Create a list of IToken from the list of CoreLabel tokens.
     * 
     * Each IToken is created by passing the original text, the POS, and the
     * lemma of the CoreLabel token. A _ symbol is replaced with the underscoreReplacement String, 
     * as JMWE 1.0.2 throws an IllegalArgumentException when given a _ symbol
     * 
     * @param tokens
     *            list of CoreLabel tokens
     * @return list of IToken
     */
    public List<IToken> getITokens(List<CoreLabel> tokens) {
        return getITokens(tokens, underscoreReplacement);
    }
    
    /**
     * Create a list of IToken from the list of CoreLabel tokens.
     * 
     * Each IToken is created by passing the original text, the POS, and the
     * lemma of the CoreLabel token. A _ symbol is replaced with the underscoreReplacement String, 
     * as JMWE 1.0.2 throws an IllegalArgumentException when given a _ symbol
     * 
     * @param tokens
     *            list of CoreLabel tokens
     * @param underscoreReplacement the replacement String for each underscore character
     *                              in the signal
     * @return list of IToken
     */
    public List<IToken> getITokens(List<CoreLabel> tokens, String underscoreReplacement) {
        List<IToken> sentence = new ArrayList<IToken>();
        for (CoreLabel token : tokens) {
            sentence.add(new Token(token.originalText().replaceAll("_", underscoreReplacement), token.get(PartOfSpeechAnnotation.class), token.lemma().replaceAll("_", underscoreReplacement)));
        }
        return sentence;
    }
}
