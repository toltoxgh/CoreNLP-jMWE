package edu.stanford.nlp.pipeline;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

public class UnderscoreIsInternallyChangedTest extends AbstractJMWEAnnotatorTst {

    @Test
    public void test_UnderscoreIsInternallyChanged_NoException_FindsMWES() {
      final String text = "She looked up the _ , underscore is in_side so_me wo_______-rds world record.";  

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
      mwe_expected.add("looked_up");
      mwe_expected.add("world_record");
      
      assertThat(mwe_observed, is(equalTo(mwe_expected)));
    }
    
}
