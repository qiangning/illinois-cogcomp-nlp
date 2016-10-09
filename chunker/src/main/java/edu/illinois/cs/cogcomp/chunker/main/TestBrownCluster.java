package edu.illinois.cs.cogcomp.chunker.main;

import edu.illinois.cs.cogcomp.chunker.utils.BrownClusterWrapper;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.edison.annotators.BrownClusterViewGenerator;
import edu.illinois.cs.cogcomp.edison.features.factory.BrownClusterFeatureExtractor;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;
/**
 * Created by qning2 on 10/5/16.
 */
public class TestBrownCluster {
    public static void main(String[] args){
        String text = "ltesjratljestl. Yesterday I bought a cat. Her name is Cathy and she is very cute. Everyone loves her very much.";
        TokenizerTextAnnotationBuilder tokenizer = new TokenizerTextAnnotationBuilder(new IllinoisTokenizer());
        TextAnnotation ta = tokenizer.createTextAnnotation("test", "test", text);
        /*try {
            BrownClusterViewGenerator brown = new BrownClusterViewGenerator("test", BrownClusterViewGenerator.file100);
            brown.addView(ta);
        }catch(Exception err){
            err.printStackTrace();
        }*/
        try{
            BrownClusterWrapper mybrown = new BrownClusterWrapper("test", BrownClusterViewGenerator.file100);
            System.out.println(mybrown.getClusterId("american"));
        }catch(Exception err){
            err.printStackTrace();
        }
        //System.out.println(ta.getView("TOKENS"));
    }
}
