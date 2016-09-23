package edu.illinois.cs.cogcomp.chunker.main;

import edu.illinois.cs.cogcomp.chunker.utils.CoNLL2000Parser;
import edu.illinois.cs.cogcomp.chunker.utils.TimeRecorder;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;
import edu.illinois.cs.cogcomp.lbjava.util.ClassUtils;
import edu.illinois.cs.cogcomp.lbjava.nlp.seg.BIOTester;
import edu.illinois.cs.cogcomp.chunker.main.lbjava.Chunker;
import edu.illinois.cs.cogcomp.chunker.main.lbjava.ChunkLabel;
import edu.illinois.cs.cogcomp.lbjava.parse.ChildrenFromVectors;

/**
 * Created by Qiang Ning on 9/7/16.
 */
public class myTester {
    public static void main(String[] args){
        boolean doTrain = false;
        ResourceManager rm = new ChunkerConfigurator().getDefaultConfig();
        String trainSet = "./chunker/data/TBAQ_full_1label_corr.txt";
        String testFile = "./chunker/data/tempeval_platinum_full_1label_corr.txt";
        Parser parser_train = new CoNLL2000Parser(trainSet);
        Parser parser_test = new CoNLL2000Parser(testFile);
        TimeRecorder testTime1 = new TimeRecorder("Testing on train set");
        TimeRecorder testTime2 = new TimeRecorder("Testing on test set");
        //int[] IterSet = {1,5,10,15,20,25,50};
        int[] IterSet = {50};
        for(int iter : IterSet) {
            System.out.println("------Iter: "+Integer.toString(iter)+"------");
            parser_train.reset();
            parser_test.reset();
            String modelName = "TBAQ_full_1label_corr" + Integer.toString(iter);
            //String modelName = "TBAQ_1label_corr_old";
            if (doTrain) {
                TimeRecorder trainTime = new TimeRecorder("Training");
                trainTime.begin();
                ChunkerTrain trainer = new ChunkerTrain(iter);
                trainer.trainModels(trainSet);
                trainTime.finish();
                trainer.writeModelsToDisk(rm.getString("modelDirPath"), modelName);
            }
            testTime1.begin();
            BIOTester tester1 = new BIOTester(new Chunker(rm.getString("modelDirPath") + modelName + ".lc", rm.getString("modelDirPath") + modelName + ".lex"),
                    new ChunkLabel(),
                    new ChildrenFromVectors(parser_train));
            tester1.test().printPerformance(System.out);
            testTime1.finish();

            testTime2.begin();
            BIOTester tester2 = new BIOTester(new Chunker(rm.getString("modelDirPath") + modelName + ".lc", rm.getString("modelDirPath") + modelName + ".lex"),
                    new ChunkLabel(),
                    new ChildrenFromVectors(parser_test));
            tester2.test().printPerformance(System.out);
            testTime2.finish();
        }
    }
}
