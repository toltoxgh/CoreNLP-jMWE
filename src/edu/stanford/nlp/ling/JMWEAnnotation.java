package edu.stanford.nlp.ling;

import java.util.List;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.stanford.nlp.util.ErasureUtils;

/**
 * The key for getting the jMWE Annotations
 * @author Tomasz Oliwa
 *
 */
public class JMWEAnnotation implements CoreAnnotation<List<IMWE<IToken>>> {
  public Class<List<IMWE<IToken>>> getType() {
    return ErasureUtils.uncheckedCast(List.class);
  }
}