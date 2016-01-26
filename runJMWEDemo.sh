#!/bin/bash

# Script to demo the jMWE annotator usage in Stanford CoreNLP
# author: Tomasz Oliwa

set -o nounset

if [ "$#" -eq 0 ]
then
    echo "Running edu.stanford.nlp.pipeline.demo.JMWEAnnotatorDemo with a predefined text"
    java -cp ./*:lib/* edu.stanford.nlp.pipeline.demo.JMWEAnnotatorDemo "lib/mweindex_wordnet3.0_semcor1.6.data"
else
    echo "Running edu.stanford.nlp.pipeline.demo.JMWEAnnotatorDemo with input text. The input should be enclosed in \" \" symbols, example usage:  $ ./runJMWEDemo.sh \"She looked up the world record.\""
    java -cp ./*:lib/* edu.stanford.nlp.pipeline.demo.JMWEAnnotatorDemo "lib/mweindex_wordnet3.0_semcor1.6.data" "$1"
fi
