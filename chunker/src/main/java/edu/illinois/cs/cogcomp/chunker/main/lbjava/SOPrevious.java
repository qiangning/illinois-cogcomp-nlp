// Modifying this comment will cause the next execution of LBJava to overwrite this file.
// F1B880000000000000005A15D5B43C0341DFB27A5074B49696BEB91728F1B7B2BD0639D3C8225D56538525D425B28CEFBB946D5DA34F146F21E427FED3F173B162F940554FC0B89DC50D7765732DB56DF2493ADA5C6C7C5688F403EA0C80AB77C8D6D806B5B087C4F5362068B2489264324B1DB6744076C0ED4551148123F53F891DD3BBF386328D161A403E5EA378A2A49ABB83D6F0F67E939FE2755C32DAE8A125DD066C6D17EAB06152D3A4D027D8B079473DBFEBE0723CA7795C6E22ADDA3784AF3D837AF1AC4C1B20CC01CC76B85615694587DAFDBC8DA4B19DB96EE020314FFA916B587E4FEF22AF6D41594FE6BE96695A17C1FCB0129A85135FCA19265451DDF43BBEB994639B3FDC2DD9E3ECFB64D4A0949B4A075F2007138D05007F1C5D76F4C6CDF90E27825A29FD5BABFBB365A31249A470A49C1925A65967F5B777296B40300000

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


public class SOPrevious extends Classifier
{
  private static final POSTagger __POSTagger = new POSTagger();

  public SOPrevious()
  {
    containingPackage = "edu.illinois.cs.cogcomp.chunker.main.lbjava";
    name = "SOPrevious";
  }

  public String getInputType() { return "edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof Token))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'SOPrevious(Token)' defined on line 33 of chunk_partial.lbj received '" + type + "' as input.");
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
    String[] tags = new String[3];
    String[] labels = new String[2];
    tags[0] = tags[1] = tags[2] = "null";
    labels[0] = labels[1] = "null";
    i = 0;
    for (; w != word; w = (Token) w.next)
    {
      tags[i] = __POSTagger.discreteValue(w);
      labels[i] = w.label;
      i++;
    }
    tags[i] = __POSTagger.discreteValue(w);
    if (!labels[0].equals(CoNLL2000ParserWithPartial.UNLABELED) && !labels[1].equals(CoNLL2000ParserWithPartial.UNLABELED))
    {
      __id = "ll";
      __value = "" + (labels[0] + "_" + labels[1]);
      __result.addFeature(new DiscretePrimitiveStringFeature(this.containingPackage, this.name, __id, __value, valueIndexOf(__value), (short) 0));
    }
    if (!labels[0].equals(CoNLL2000ParserWithPartial.UNLABELED))
    {
      __id = "lt1";
      __value = "" + (labels[0] + "_" + tags[1]);
      __result.addFeature(new DiscretePrimitiveStringFeature(this.containingPackage, this.name, __id, __value, valueIndexOf(__value), (short) 0));
    }
    if (!labels[1].equals(CoNLL2000ParserWithPartial.UNLABELED))
    {
      __id = "lt2";
      __value = "" + (labels[1] + "_" + tags[2]);
      __result.addFeature(new DiscretePrimitiveStringFeature(this.containingPackage, this.name, __id, __value, valueIndexOf(__value), (short) 0));
    }
    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    if (!(examples instanceof Token[]))
    {
      String type = examples == null ? "null" : examples.getClass().getName();
      System.err.println("Classifier 'SOPrevious(Token)' defined on line 33 of chunk_partial.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    return super.classify(examples);
  }

  public int hashCode() { return "SOPrevious".hashCode(); }
  public boolean equals(Object o) { return o instanceof SOPrevious; }
}

