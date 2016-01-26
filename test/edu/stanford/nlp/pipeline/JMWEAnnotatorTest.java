package edu.stanford.nlp.pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.JMWEAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.MetaClass.ClassCreationException;

/**
 * Unit tests for the JMWEAnnotator
 * @author Tomasz Oliwa
 *
 */
public class JMWEAnnotatorTest {
    
    // For the unit tests to pass, change this path to wherever the index file mweindex_wordnet3.0_semcor1.6.data is located
    public static final String index = "/home/toliwa/workspace/JMWdE_Annotator/lib/mweindex_wordnet3.0_semcor1.6.data";
    
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testDemoFromJMWEManual_FindsMWES() {
      final String text = "She looked up the world record.";  

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
    
    @Test
    public void test_ProperNounds_FindsMWES() {
      final String text = "He lives in San Francisco. The word new, the word york, the word city.";  

      Properties props = new Properties();
      props.setProperty("annotators", "tokenize, ssplit, pos, lemma, jmwe");
      props.setProperty("customAnnotatorClass.jmwe", "edu.stanford.nlp.pipeline.JMWEAnnotator");
      props.setProperty("customAnnotatorClass.jmwe.verbose", "true");
      props.setProperty("customAnnotatorClass.jmwe.underscoreReplacement", "-");
      props.setProperty("customAnnotatorClass.jmwe.indexData", index);
      props.setProperty("customAnnotatorClass.jmwe.detector", "ProperNouns");
      List<String> mwe_observed = getObservedMWEs(props, text);
      
      // construct expected
      List<String> mwe_expected = new ArrayList<>();
      mwe_expected.add("san_francisco");
      
      assertThat(mwe_observed, is(equalTo(mwe_expected)));
    }   
    
    /**
     * Create the observed MWEs in the text
     * @param props the properties
     * @param text the text
     * @return the observed MWEs in the text
     */
    private List<String> getObservedMWEs(Properties props, String text) {
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        
        // create an Annotation with the text
        Annotation document = new Annotation(text);
        
        // run the Annotators on the text
        pipeline.annotate(document);
        
        // construct observed
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        List<String> mwe_observed = new ArrayList<>();
        for(CoreMap sentence: sentences) {
          for (IMWE<IToken> mwe: sentence.get(JMWEAnnotation.class)) {
              mwe_observed.add(mwe.getForm());
            }
        }
        return mwe_observed;
    }
    
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
    
    @Test
    public void testNoUnderscoreReplacementGiven_ThrowsRunTimeException() {
      final String text = "She looked up the world record.";  
      Properties props = new Properties();
      props.setProperty("annotators", "tokenize, ssplit, pos, lemma, jmwe");
      props.setProperty("customAnnotatorClass.jmwe", "edu.stanford.nlp.pipeline.JMWEAnnotator");
      props.setProperty("customAnnotatorClass.jmwe.verbose", "true");
      props.setProperty("customAnnotatorClass.jmwe.indexData", index);
      props.setProperty("customAnnotatorClass.jmwe.detector", "Consecutive");
      exception.expect(RuntimeException.class);
      StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    }
    
    @Test
    public void testUnderscoreReplacementHasUnderscore_ThrowsRunTimeException() {
      final String text = "She looked up the world record.";  
      Properties props = new Properties();
      props.setProperty("annotators", "tokenize, ssplit, pos, lemma, jmwe");
      props.setProperty("customAnnotatorClass.jmwe", "edu.stanford.nlp.pipeline.JMWEAnnotator");
      props.setProperty("customAnnotatorClass.jmwe.verbose", "true");
      props.setProperty("customAnnotatorClass.jmwe.underscoreReplacement", "-_");
      props.setProperty("customAnnotatorClass.jmwe.indexData", index);
      props.setProperty("customAnnotatorClass.jmwe.detector", "Consecutive");
      exception.expect(RuntimeException.class);
      StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    }    
    
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
