package edu.stanford.nlp.pipeline.demo;

import java.util.List;
import java.util.Properties;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.JMWEAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * Demo to illustrate the jMWE Annotator.
 * @author Tomasz Oliwa
 *
 */
public class JMWEAnnotatorDemo {

    public static void main(String[] args) {
        String text = null;
        if (args.length == 0) {
            System.out.println("Usage: JMWEAnnotatorDemo path-to-mweindex_wordnet3.0_semcor1.6.data \"input\"");
            System.out.println("Enclose the input in \" \" characters. If no input text is given, a pre-defined text is run.");
            System.exit(0);
        }
        String index = args[0];
        if (args.length == 1) {
        // demo text from the AP Blei corpus
            text = "Andy Warhol's plastic wristwatches of Fred Flintstone, Judy Jetson and Gumby reached $2,640 in a ``wild'' bidding war _ $2,590 more than the timepieces cost the late artist in a department store, Sotheby's said. Kitschy collectibles were only some of the objects on sale Wednesday during the fifth day of the 10-day auction of Warhol's possessions. There was also serious jewelry on the block. American Indian art, including ``no end of turquoise jewelry'' was to be sold at today's auction, said Diana Brooks, president of Sotheby's North America. So far, Warhol's collection has fetched $9,072,000, more than twice Sotheby's high estimate of $3,951,000. Only 37 out of some 1,400 lots have not sold, she said. The highest bid Wednesday was $55,000 for a pair of surrealist ear clips by artist Salvador Dali, according to Ms. Brooks. The buyer, a private collector, was not identified. The ear clips, asymmetrical ruby-studded hearts with a honeycomb center set with round diamonds, had a pre-sale estimated value of $10,000 to $15,000. An 18-karat gold, garnet and diamond circular pendant, also by Dali, brought $47,300, Ms. Brooks said. The pendant, estimated at $7,500 to $10,000, depicted the profiles of Tristan and Isolde separated by a chalice. The brightly-colored cartoon watches, made by Lewco, were decorated with raised, full-length figures of Fred and Dino, his pet dinosaur; Judy Jetson standing with hands on hips; and a smiling Gumby. Warhol bought them in 1985 and 1986 at Bloomingdale's, Ms. Brooks said. In their original plastic packaging, the watches bore the original price tags of $20 and $10, she said. ``People were so wild to have the three plastic watches in the sale. They sort of symbolize the sale of the whole collection _ something people will remember,'' she said. The pre-sale estimate was $60 to $80 for all three. But in ``wild bidding'' that came down to a telephone contest between four people, the price climbed to $2,640, Ms. Brooks said. A private collector, who was not identified, won the bidding. Bloomingdale's still sells the watches in some stores, said Miraed Smith, a store spokeswoman. Another wristwatch that did well at the auction was a stainless steel piece, dated 1948, with a photo image of Gene Autry and signed ``always your pal Gene Autry.'' Sotheby's estimated it would sell for $50 to $100 but it went for $1,870, Ms. Brooks said. Proceeds from the auction will benefit the Andy Warhol Foundation for the Visual Arts, which will award grants to cultural institutions in the United States and abroad. Warhol died at age 58 last year.";
        } else {
        // text from args[1]
            text = args[1];
        }
        jmweDemo(index, text);
    }
    
    /**
     * jMWE Demo, prints out discovered MWEs from the text
     * @param index the index
     * @param text the text
     */
    public static void jmweDemo(String index, String text) {
        // creates the properties for Stanford CoreNLP: tokenize, ssplit, pos, lemma, jmwe
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, jmwe");
        props.setProperty("customAnnotatorClass.jmwe", "edu.stanford.nlp.pipeline.JMWEAnnotator");
        props.setProperty("customAnnotatorClass.jmwe.verbose", "false");
        props.setProperty("customAnnotatorClass.jmwe.underscoreReplacement", "-");
        props.setProperty("customAnnotatorClass.jmwe.indexData", index);
        props.setProperty("customAnnotatorClass.jmwe.detector", "CompositeConsecutiveProperNouns");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        
        // put the text in the document annotation 
        Annotation doc = new Annotation(text);
        
        // run the CoreNLP pipeline on the document
        pipeline.annotate(doc);
        
        // loop over the sentences
        List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
        System.out.println();
        for(CoreMap sentence: sentences) {
            System.out.println("Sentence: "+sentence);
            // loop over all discovered jMWE token and perform some action
          for (IMWE<IToken> token: sentence.get(JMWEAnnotation.class)) {
              System.out.println("IMWE<IToken>: "+token+", token.isInflected(): "+token.isInflected()+", token.getForm(): "+token.getForm());
            }
          System.out.println();
        }        
    }
}
