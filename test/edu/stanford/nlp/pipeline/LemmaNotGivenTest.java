package edu.stanford.nlp.pipeline;

import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class LemmaNotGivenTest extends AbstractJMWEAnnotatorTst {

    @Rule
    public ExpectedException exception = ExpectedException.none();    
    
    @Test
    public void testLemmaNotGiven_ThrowsIllegalArgumentException() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, jmwe");
        props.setProperty("customAnnotatorClass.jmwe", "edu.stanford.nlp.pipeline.JMWEAnnotator");
        props.setProperty("customAnnotatorClass.jmwe.verbose", "true");
        props.setProperty("customAnnotatorClass.jmwe.underscoreReplacement", "-");
        props.setProperty("customAnnotatorClass.jmwe.indexData", index);
        props.setProperty("customAnnotatorClass.jmwe.detector", "Consecutive");
        exception.expect(IllegalArgumentException.class);
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    }    
    
}
