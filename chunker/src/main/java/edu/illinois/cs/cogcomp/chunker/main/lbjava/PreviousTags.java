// Modifying this comment will cause the next execution of LBJava to overwrite this file.
// F1B8800000000000000055D8BCA02C030154F75E671A4B84B41796450F5BB22E2417D157AA38121D425B288FFE6AA51499DCC3ECD337067B7B4E9A38595AB1B9ACDA5E1D5CB63762D8AD8D342816A87085B70B04BB7C8ED73182D854CC16CC5081364AD747BB8A3BB4B2344328EA49218435E4287A98E7CE9CF0620FCF8A44002AFACFF95D477FD818B44C15D9929B32591D5B29AC5C333BC2A8E7E97EB296D19DD2BF358EC3B459D6695C46AB82613F4A90B32DE80CDBE16086D1DCFE05F271D2CE9190100000

package edu.illinois.cs.cogcomp.chunker.main.lbjava;

import edu.illinois.cs.cogcomp.chunker.utils.CoNLL2000ParserWithPartial;
import edu.illinois.cs.cogcomp.lbjava.classify.*;
import edu.illinois.cs.cogcomp.lbjava.infer.*;
import edu.illinois.cs.cogcomp.lbjava.io.IOUtilities;
import edu.illinois.cs.cogcomp.lbjava.learn.*;
import edu.illinois.cs.cogcomp.lbjava.nlp.*;
import edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token;
import edu.illinois.cs.cogcomp.lbjava.parse.*;
import edu.illinois.cs.cogcomp.pos.lbjava.POSTagger;
import edu.illinois.cs.cogcomp.pos.lbjava.POSWindow;


/**
  * Feature generator that senses the chunk tags of the previous two words.
  * During training, labels are present, so the previous two chunk tags are
  * simply read from the data.  Otherwise, the prediction of the
  * {@link Chunker} is used.
  *
  * @author Nick Rizzolo
 **/
public class PreviousTags extends Classifier
{
  public PreviousTags()
  {
    containingPackage = "edu.illinois.cs.cogcomp.chunker.main.lbjava";
    name = "PreviousTags";
  }

  public String getInputType() { return "edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof Token))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'PreviousTags(Token)' defined on line 19 of chunk_partial.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    Token word = (Token) __example;

    FeatureVector __result;
    __result = new FeatureVector();
    String __id;
    String __value;

    int i;
    Token w = word;
    for (i = 0; i > -2 && w.previous != null; --i)
    {
      w = (Token) w.previous;
    }
    for (; w != word; w = (Token) w.next)
    {
      if (!w.label.equals(CoNLL2000ParserWithPartial.UNLABELED))
      {
        __id = "" + (i++);
        __value = "" + (w.label);
        __result.addFeature(new DiscretePrimitiveStringFeature(this.containingPackage, this.name, __id, __value, valueIndexOf(__value), (short) 0));
      }
    }
    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    if (!(examples instanceof Token[]))
    {
      String type = examples == null ? "null" : examples.getClass().getName();
      System.err.println("Classifier 'PreviousTags(Token)' defined on line 19 of chunk_partial.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    return super.classify(examples);
  }

  public int hashCode() { return "PreviousTags".hashCode(); }
  public boolean equals(Object o) { return o instanceof PreviousTags; }
}

