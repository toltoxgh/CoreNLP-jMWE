package edu.stanford.nlp.pipeline;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

public class HasUniCodeTest extends AbstractJMWEAnnotatorTst {

    @Test
    public void test_HasUniCode_FindsMWES() {
      final String text = "According to the definition, some unicode symbols are ☃, ★, ☁ and ♦.";  

      Properties props = new Properties();
      props.setProperty("annotators", "tokenize, ssplit, pos, lemma, jmwe");
      props.setProperty("customAnnotatorClass.jmwe", "edu.stanford.nlp.pipeline.JMWEAnnotator");
      props.setProperty("customAnnotatorClass.jmwe.verbose", "true");
      props.setProperty("customAnnotatorClass.jmwe.underscoreReplacement", "-");
      props.setProperty("customAnnotatorClass.jmwe.indexData", index);
      props.setProperty("customAnnotatorClass.jmwe.detector", "Consecutive");
      List<String> mwe_observed = getObservedMWEs(props, text);
      
      // construct expected
      List<String> mwe_expected = new ArrayList<>();
      mwe_expected.add("according_to");
      mwe_expected.add("according_to");
      mwe_expected.add("according_to");
      mwe_expected.add("according_to");
      mwe_expected.add("to_the");
      
      assertThat(mwe_observed, is(equalTo(mwe_expected)));
    }    
    
}
