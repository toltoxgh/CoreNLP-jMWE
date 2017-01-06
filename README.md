## Table of Contents
**[CoreNLP-jMWE](#corenlp-jmwe)**  
**[Installation and use](#installation-and-use)**  
**[Demo](#demo)**  
**[Annotator properties](#annotator-properties)**  
**[MWE detectors](#mwe-detectors)**  
**[License](#license)**  
**[Author information](#author-information)**  
**[Original Stanford CoreNLP README](#original-stanford-corenlp-readme)**

# CoreNLP-jMWE

This [Stanford CoreNLP](https://github.com/stanfordnlp/CoreNLP) annotator is able to capture Multi-Word Expressions (MWE) / collocations from plain text. Under the hood, it integrates [jMWE](http://projects.csail.mit.edu/jmwe/), which itself employs a database generated through processing [WordNet](https://wordnet.princeton.edu/).

For example, given the input sentence 

    She traveled to Las Vegas and looked up the world record.
    
the detected MWEs information could include:

    1. travel_to_V={traveled_VBD,to_TO},    token.isInflected(): true,  token.getForm(): traveled_to
    2. las_vegas_N={Las_NNP,Vegas_NNP},     token.isInflected(): false, token.getForm(): las_vegas
    3. las_vegas_P={Las_NNP,Vegas_NNP},     token.isInflected(): false, token.getForm(): las_vegas
    4. look_up_V={looked_VBD,up_RP},        token.isInflected(): true,  token.getForm(): looked_up
    5. world_record_N={world_NN,record_NN}, token.isInflected(): false, token.getForm(): world_record

Detecting MWEs can be useful in a number of applications, such as: 

1. Extracting a subset of n-grams (collocations) from a corpus for topic modeling and information retrieval purposes.
2. Word Sense Disambiguation problems, see the publications on [jMWE](http://projects.csail.mit.edu/jmwe/) for more information.

This is a ready to use new [annotator](http://stanfordnlp.github.io/CoreNLP/new_annotator.html) for Stanford CoreNLP 3.7.0, and can therefore be easily integrated into any further downstream NLP processing the same way as any other already existing CoreNLP annotator.

## Installation and use

### Use without compilation

For convenience, jar files are provided in this repository. To use CoreNLP-jMWE in a project that has Stanford CoreNLP, simply:

1. Add ``stanford-corenlp-jmwe-annotator.jar`` to your projects CLASSPATH
2. Add either ``edu.mit.jmwe_1.0.2.jar`` (binary) or  ``edu.mit.jmwe_1.0.2_jdk.jar`` (binary and src) from [jMWE](http://projects.csail.mit.edu/jmwe/) to your projects CLASSPATH
3. Download the standard MWE Index data file ``mweindex_wordnet3.0_semcor1.6.data`` from [jMWE](http://projects.csail.mit.edu/jmwe/)
4. Setup your CoreNLP [Properties](#annotator-properties) to use CoreNLP-jMWE, see the [Demo](#demo) section for a complete example

Done!

### Compilation preparation

1. Clone the project:

        git clone https://github.com/toliwa/CoreNLP-jMWE
    
2. Create the lib directory:

        mkdir CoreNLP-jMWE/lib

### Getting Stanford CoreNLP

CoreNLP-jMWE is compatible with CoreNLP 3.7.0. Either get the stable release (it comes with the model jar):

1. Download Stanford CoreNLP from [http://stanfordnlp.github.io/CoreNLP/index.html](http://stanfordnlp.github.io/CoreNLP/index.html) and copy all its jar files to CoreNLP-jMWE/lib

Or get the current master branch:

1. Clone Stanford CoreNLP from [https://github.com/stanfordnlp/CoreNLP](https://github.com/stanfordnlp/CoreNLP) and copy all its jar files to CoreNLP-jMWE/lib
2. Get the CoreNLP models from [http://nlp.stanford.edu/software/stanford-corenlp-models-current.jar](http://nlp.stanford.edu/software/stanford-corenlp-models-current.jar) and copy them to CoreNLP-jMWE/lib

### Getting jMWE

From [jMWE](http://projects.csail.mit.edu/jmwe/), download:

1. Either ``edu.mit.jmwe_1.0.2.jar`` (binary) or  ``edu.mit.jmwe_1.0.2_jdk.jar`` (binary and src)

2. The Standard MWE Index data file ``mweindex_wordnet3.0_semcor1.6.data``

and copy them to CoreNLP/lib.

The compilation preparation is done!

### Getting JUnit

From [JUnit](http://junit.org/), download:

1. ``hamcrest-core-1.3.jar`` and ``junit-4.12.jar`` and copy them to CoreNLP-jMWE/lib

The compilation preparation is done!

### Compiling, jar packaging and unit tests

1. To compile the project and create jar files, go inside the CoreNLP-jMWE folder and run

        ant jar
    
2. To run the unit tests, you need to edit the ``AbstractJMWEAnnotatorTst.java`` file and point the ``public static final String index`` variable to the location on your file system where the ``mweindex_wordnet3.0_semcor1.6.data`` file is stored. Then, run: 

        ant clean && ant junit
        
Running the unit tests should result in **all** tests passing. If this is not the case, resolve these problems before continuing. Common reasons why tests fail would be missing dependencies or not editing the ``AbstractJMWEAnnotatorTst.java`` file to point to the ``mweindex_wordnet3.0_semcor1.6.data`` file on your file-system.    
    
## Demo

The class ``JMWEAnnotatorDemo`` in the package ``edu.stanford.nlp.pipeline.demo`` shows a basic use case of the new annotator, given a String it will detect and print out MWE information.

If you have downloaded the jar files from this repository or have created them (see above), you can also run the ``runJMWEDemo.sh`` shell script, it can call the ``JMWEAnnotatorDemo`` with a predefined text if no further input is given, or use the commandline first argument as input. To get the output as described in the first section, type:

```./runJMWEDemo.sh "She traveled to Las Vegas and looked up the world record."```

The following method from ``JMWEAnnotatorDemo`` shows how to programmatically access detected MWEs. The way to do so mirrors accessing other annotators such as TokensAnnotation as described for the [Stanford CoreNLP demo](http://nlp.stanford.edu/software/corenlp.shtml).

```java
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
```
    
### Remarks on the programmatical use
It appears that when declaring and instantiating a pipeline with a ``customAnnotatorClass.jmwe`` more than one time in the code, the Annotator and its parameters will only be instantiated the first time the ``StanfordCoreNLP pipeline`` object is created. Therefore, subsequent object creations of new pipelines in the same JVM, potentially with different ``customAnnotatorClass.jmwe`` parameters, should not be performed.

## Annotator properties

As can be seen in the example above, the JMWEAnnotator has several properties to be set:

    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, jmwe");
"jmwe" is the reference name of the annotator, it has to be called after lemmatization.
    
    props.setProperty("customAnnotatorClass.jmwe", "edu.stanford.nlp.pipeline.JMWEAnnotator")
The full path to its class needs to be set.
 
    props.setProperty("customAnnotatorClass.jmwe.verbose", "false");
By default, verbose is false. If set to true, Annotator information and MWEs will be output during the detection stage.

    props.setProperty("customAnnotatorClass.jmwe.underscoreReplacement", "-");
jMWE 1.0.2 throws an IllegalArgumentException when given a "_" symbol, so underscores have to be replaced during detection. A possible choice would be the hyphen symbol "-".

    props.setProperty("customAnnotatorClass.jmwe.indexData", index);
indexData needs a path to the ``mweindex_wordnet3.0_semcor1.6.data`` file.

    props.setProperty("customAnnotatorClass.jmwe.detector", "CompositeConsecutiveProperNouns");
The type of the MWE detector needs to be defined, currently implemented are "Consecutive", "Exhaustive", "ProperNouns", "Complex" and "CompositeConsecutiveProperNouns".


## MWE detectors

[jMWE](http://projects.csail.mit.edu/jmwe/) comes with a straight-forward way to write and combine detectors, filters and resolvers for MWE discovery. A detailed introduction about this is given in the [jMWE user manual](edu.mit.jmwe_1.0.2_manual.pdf).

The class JMWEAnnotator contains the following method, where the MWE detectors are setup. Adding more detectors is only a matter of adding another switch case and instantiating the desired setup as introduced in the [jMWE user manual](edu.mit.jmwe_1.0.2_manual.pdf).

The choice of the right MWE detector will greatly impact the retrieved MWEs, on the [jMWE](http://projects.csail.mit.edu/jmwe/) page there are jMWE publications, wich provide precision, recall and f-measures for different implemented detectors.


```java
    /**
     * Get the detector.
     * 
     * @param index
     *            the index
     * @param detector 
     *            the detector, \"Consecutive\", \"Exhaustive\", \"ProperNouns\", \"Complex\" or \"CompositeConsecutiveProperNouns\" are supported
     * @return the detector
     */
    public IMWEDetector getDetector(IMWEIndex index, String detector) {
        IMWEDetector iMWEdetector = null;
        switch (detector) {
        case "Consecutive":
            iMWEdetector = new Consecutive(index);
            break;
        case "Exhaustive":
            iMWEdetector = new Exhaustive(index);
            break;
        case "ProperNouns":
            iMWEdetector = ProperNouns.getInstance();
            break;
        case "Complex":
            iMWEdetector = new CompositeDetector(ProperNouns.getInstance(),
                    new MoreFrequentAsMWE(new InflectionPattern(new Consecutive(index))));
            break;
        case "CompositeConsecutiveProperNouns":
            iMWEdetector = new CompositeDetector(new Consecutive(index), ProperNouns.getInstance());            
            break;
        default:
            throw new IllegalArgumentException("Invalid detector argument " + detector
                    + ", only \"Consecutive\", \"Exhaustive\", \"ProperNouns\", \"Complex\" or \"CompositeConsecutiveProperNouns\" are supported.");
        }
        return iMWEdetector;
    }
```

## License

This annotator has the same license as Stanford CoreNLP, which, at the time of writing, states on https://github.com/stanfordnlp/CoreNLP :
"The Stanford CoreNLP code is written in Java and licensed under the GNU General Public License (v3 or later)."

The jMWE files (not included here, available from http://projects.csail.mit.edu/jmwe/ ) have the following note on their page:
"The software is distributed under the Creative Commons Attribution 4.0 International License that makes it free to use for any purpose, as long as proper copyright acknowledgement is made."

Name, copyright and other acknowledgements for "jMWE" under the http://creativecommons.org/licenses/by/4.0/ from the page and author of Finlayson, M.A. at http://projects.csail.mit.edu/jmwe/ : 

Finlayson, M.A. and Kulkarni,
N. (2011) Detecting Multi-Word Expressions Improves Word Sense
Disambiguation, Proceedings of the 8th Workshop on Multiword Expressions,
Portland, OR. pp. 20-24. 

Kulkarni, N. and Finlayson, M.A. jMWE: A Java
Toolkit for Detecting Multi-Word Expressions, Proceedings of the 8th Workshop
on Multiword Expressions, Portland, OR. pp. 122-124.

Hereby proper copyright acknowledgement is made as required. 

The GNU Project states that GPL and the Creative Commons Attribution 4.0 license are compatible: http://www.gnu.org/licenses/license-list.en.html#OtherLicenses

## Author information

The CoreNLP-jMWE annotator is developed by Tomasz Oliwa at the Center for Research Informatics (CRI), University of Chicago.





## Original Stanford CoreNLP README

You can find the original Stanford CoreNLP README.md at [https://github.com/stanfordnlp/CoreNLP](https://github.com/stanfordnlp/CoreNLP)


 
