package edu.illinois.cs.cogcomp.chunker.utils;

import edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token;
import edu.illinois.cs.cogcomp.lbjava.parse.LinkedVector;
import org.apache.commons.cli.CommandLine;

import java.util.Random;

public class CoNLL2000ParserWithPartial extends CoNLL2000Parser {
    public static final String UNLABELED = "UN";
    private int seed;
    private int mode;
    private Random random;
    private double samplingRate;
    private static CommandLine cmd;


    public CoNLL2000ParserWithPartial(String file, int mode, double samplingRate) {
        super(file);
        seed = 0;
        this.mode = mode;
        random = new Random(seed);
        this.samplingRate = samplingRate;
    }

    public void setSeed(int seed) {
        this.seed = seed;
        random = new Random(seed);
    }

    @Override
    public void reset() {
        super.reset();
        random = new Random(seed);
    }

    @Override
    public Object next() {
        LinkedVector lv = (LinkedVector) super.next();
        if(lv==null) return null;
        switch(mode){
            case 0:
                for(int i=0;i<lv.size();i++){
                    if(random.nextDouble()<=samplingRate) continue;
                    ((Token) lv.get(i)).label = UNLABELED;
                }
                return lv;
            case 1:
                while(random.nextDouble()>samplingRate){
                    lv = (LinkedVector) super.next();
                }
                return lv;
            default:
                System.out.println("[WARNING] mode of CoNLL2000ParserWithPartial not defined.");
                return null;
        }
    }
}
