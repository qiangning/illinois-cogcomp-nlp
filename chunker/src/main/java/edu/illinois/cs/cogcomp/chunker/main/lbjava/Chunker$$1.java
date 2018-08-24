// Modifying this comment will cause the next execution of LBJava to overwrite this file.
// F1B88000000000000000B49CC2E4E2A4D294555507EC82DCBCE4D22515134D809CFCE4DC3582FCF2A41D450B1D5507BCF2ACD26D1507E4C28CC294CC9CCAA4C29CCCFC3D158070AC7845614AA76E5A10510450D13D2D23B22518AA30CF383C333F252FBC5741C718229284225050A30633144300CDC3856908000000

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


public class Chunker$$1 extends Classifier
{
  private static final Forms __Forms = new Forms();
  private static final Capitalization __Capitalization = new Capitalization();
  private static final WordTypeInformation __WordTypeInformation = new WordTypeInformation();
  private static final Affixes __Affixes = new Affixes();
  private static final POSWindow __POSWindow = new POSWindow();
  private static final Mixed __Mixed = new Mixed();
  private static final POSWindowpp __POSWindowpp = new POSWindowpp();
  private static final Formpp __Formpp = new Formpp();

  public Chunker$$1()
  {
    containingPackage = "edu.illinois.cs.cogcomp.chunker.main.lbjava";
    name = "Chunker$$1";
  }

  public String getInputType() { return "edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof Token))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'Chunker$$1(Token)' defined on line 175 of chunk_partial.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    FeatureVector __result;
    __result = new FeatureVector();
    __result.addFeatures(__Forms.classify(__example));
    __result.addFeatures(__Capitalization.classify(__example));
    __result.addFeatures(__WordTypeInformation.classify(__example));
    __result.addFeatures(__Affixes.classify(__example));
    __result.addFeatures(__POSWindow.classify(__example));
    __result.addFeatures(__Mixed.classify(__example));
    __result.addFeatures(__POSWindowpp.classify(__example));
    __result.addFeatures(__Formpp.classify(__example));
    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    if (!(examples instanceof Token[]))
    {
      String type = examples == null ? "null" : examples.getClass().getName();
      System.err.println("Classifier 'Chunker$$1(Token)' defined on line 175 of chunk_partial.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    return super.classify(examples);
  }

  public int hashCode() { return "Chunker$$1".hashCode(); }
  public boolean equals(Object o) { return o instanceof Chunker$$1; }

  public java.util.LinkedList getCompositeChildren()
  {
    java.util.LinkedList result = new java.util.LinkedList();
    result.add(__Forms);
    result.add(__Capitalization);
    result.add(__WordTypeInformation);
    result.add(__Affixes);
    result.add(__POSWindow);
    result.add(__Mixed);
    result.add(__POSWindowpp);
    result.add(__Formpp);
    return result;
  }
}

