package edu.stanford.nlp.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.stanford.nlp.ling.JMWEAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.util.CoreMap;

/**
 * Class to be extended by Test classes of JMWEAnnotator, contains a helper method to
 * obtain the observed MWEs from a text
 * @author toliwa
 *
 */
public class AbstractJMWEAnnotatorTst {
    
    // For the unit tests to pass, change this path to wherever the index file mweindex_wordnet3.0_semcor1.6.data is located
    public static final String index = "lib/mweindex_wordnet3.0_semcor1.6.data";
    
    /**
     * Create the observed MWEs in the text
     * @param props the properties
     * @param text the text
     * @return the observed MWEs in the text
     */
    protected List<String> getObservedMWEs(Properties props, String text) {
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
}
