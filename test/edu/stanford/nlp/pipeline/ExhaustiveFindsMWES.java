package edu.stanford.nlp.pipeline;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

public class ExhaustiveFindsMWES extends AbstractJMWEAnnotatorTst {

    @Test
    public void test_ExhaustiveFindsMWES() {
      // Since the detector is exhaustive, the second sentence is expected to yield new york city
      final String text = "He lives in San Francisco. The word new, the word york, the word city.";  

      Properties props = new Properties();
      props.setProperty("annotators", "tokenize, ssplit, pos, lemma, jmwe");
      props.setProperty("customAnnotatorClass.jmwe", "edu.stanford.nlp.pipeline.JMWEAnnotator");
      props.setProperty("customAnnotatorClass.jmwe.verbose", "true");
      props.setProperty("customAnnotatorClass.jmwe.underscoreReplacement", "-");
      props.setProperty("customAnnotatorClass.jmwe.indexData", index);
      props.setProperty("customAnnotatorClass.jmwe.detector", "Exhaustive");
      List<String> mwe_observed = getObservedMWEs(props, text);
      
      // construct expected
      List<String> mwe_expected = new ArrayList<>();
      mwe_expected.add("lives_in");
      mwe_expected.add("san_francisco");
      mwe_expected.add("the_city");
      mwe_expected.add("new_york_city");
      mwe_expected.add("new_york");
      mwe_expected.add("the_city");
      mwe_expected.add("the_city");
      
      assertThat(mwe_observed, is(equalTo(mwe_expected)));
    }    
    
}
