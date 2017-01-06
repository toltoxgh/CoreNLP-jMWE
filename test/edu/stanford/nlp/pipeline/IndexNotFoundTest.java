package edu.stanford.nlp.pipeline;

import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.stanford.nlp.util.MetaClass.ClassCreationException;

public class IndexNotFoundTest extends AbstractJMWEAnnotatorTst {

    @Rule
    public ExpectedException exception = ExpectedException.none();    
    
    @Test
    public void testIndexNotFound_ThrowsIllegalArgumentException() {
        final String text = "She looked up the world record.";  
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, jmwe");
        props.setProperty("customAnnotatorClass.jmwe", "edu.stanford.nlp.pipeline.JMWEAnnotator");
        props.setProperty("customAnnotatorClass.jmwe.verbose", "true");
        props.setProperty("customAnnotatorClass.jmwe.underscoreReplacement", "-");
        props.setProperty("customAnnotatorClass.jmwe.detector", "Consecutive");
        props.setProperty("customAnnotatorClass.jmwe.indexData", "/home/user/some/random/folder/somefile");
        exception.expect(ClassCreationException.class);
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // Exception trace will have an entry Caused by: java.lang.RuntimeException: index file /home/user/some/random/folder/somefile does not exist
    }    
    
}
