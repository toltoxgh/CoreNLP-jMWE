package edu.stanford.nlp.pipeline;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import edu.mit.jmwe.data.IToken;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;

public class UnderscoresAreReplacedCorrectlyTest extends AbstractJMWEAnnotatorTst {

    @Test
    public void test_UnderscoresAreReplacedCorrectly() {
      final String text = "Some underscore _ text with many_underscores_present_here.";
      final String underscoreReplacement = "-";
      final List<String> expected = Arrays.asList("Some", "underscore", "-", 
              "text", "with", "many-underscores-present-here", ".");
      
      Properties props = new Properties();
      props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
      Annotation doc = new Annotation(text);
      StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
      pipeline.annotate(doc);
      List<CoreLabel> coreLabels = doc.get(TokensAnnotation.class);
      
      props = new Properties();
      props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
      props.setProperty("customAnnotatorClass.jmwe", "edu.stanford.nlp.pipeline.JMWEAnnotator");
      props.setProperty("customAnnotatorClass.jmwe.verbose", "true");
      props.setProperty("customAnnotatorClass.jmwe.underscoreReplacement", underscoreReplacement);
      props.setProperty("customAnnotatorClass.jmwe.indexData", index);
      props.setProperty("customAnnotatorClass.jmwe.detector", "Consecutive");
      JMWEAnnotator jMWEAnnotator = new JMWEAnnotator("JMWEAnnotator", props);
      List<IToken> iToken = jMWEAnnotator.getITokens(coreLabels, underscoreReplacement);
      
      for (int i = 0; i < expected.size(); i++) {
          assertThat(iToken.get(i).getForm(), is(equalTo(expected.get(i))));
      }
    }    
    
}
