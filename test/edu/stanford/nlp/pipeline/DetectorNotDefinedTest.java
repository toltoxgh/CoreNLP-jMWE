package edu.stanford.nlp.pipeline;

import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DetectorNotDefinedTest extends AbstractJMWEAnnotatorTst {

    @Rule
    public ExpectedException exception = ExpectedException.none();    
    
    @Test
    public void testDetectorNotDefined_ThrowsIllegalArgumentException() {
        final String text = "She looked up the world record.";  
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, jmwe");
        props.setProperty("customAnnotatorClass.jmwe", "edu.stanford.nlp.pipeline.JMWEAnnotator");
        props.setProperty("customAnnotatorClass.jmwe.verbose", "true");
        props.setProperty("customAnnotatorClass.jmwe.underscoreReplacement", "-");
        props.setProperty("customAnnotatorClass.jmwe.indexData", index);
        props.setProperty("customAnnotatorClass.jmwe.detector", "SomeOtherText");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(text);
        exception.expect(IllegalArgumentException.class);
        pipeline.annotate(document);
    }    
    
}
