package edu.stanford.nlp.pipeline;

import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.stanford.nlp.util.MetaClass.ClassCreationException;

public class IndexNotGivenTest extends AbstractJMWEAnnotatorTst {

    @Rule
    public ExpectedException exception = ExpectedException.none();    
    
    @Test
    public void testIndexNotGiven_ClassCreationException() {        
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, jmwe");
        props.setProperty("customAnnotatorClass.jmwe", "edu.stanford.nlp.pipeline.JMWEAnnotator");
        props.setProperty("customAnnotatorClass.jmwe.verbose", "true");
        props.setProperty("customAnnotatorClass.jmwe.underscoreReplacement", "-");
        props.setProperty("customAnnotatorClass.jmwe.detector", "Consecutive");
        exception.expect(ClassCreationException.class);
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // Exception trace will have an entry Caused by: java.lang.RuntimeException: No customAnnotatorClass.jmwe.indexData key in properties found
    }    
    
}
