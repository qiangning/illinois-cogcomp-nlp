package edu.illinois.cs.cogcomp.chunker.main;

import edu.illinois.cs.cogcomp.chunker.main.lbjava.ChunkLabel;
import edu.illinois.cs.cogcomp.chunker.main.lbjava.Chunker;
import edu.illinois.cs.cogcomp.chunker.utils.CoNLL2000Parser;
import edu.illinois.cs.cogcomp.chunker.utils.CoNLL2000ParserWithPartial;
import edu.illinois.cs.cogcomp.chunker.utils.myBIOTester;
import edu.illinois.cs.cogcomp.lbjava.classify.ScoreSet;
import edu.illinois.cs.cogcomp.lbjava.learn.Learner;
import edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token;
import edu.illinois.cs.cogcomp.lbjava.parse.ChildrenFromVectors;
import edu.illinois.cs.cogcomp.lbjava.parse.LinkedVector;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;
import edu.illinois.cs.cogcomp.nlp.util.PrecisionRecallManager;
import edu.illinois.cs.cogcomp.temporal.utils.CoDL.CoDLWrapper_LBJ;
import edu.illinois.cs.cogcomp.temporal.utils.myLogFormatter;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChunkerTrainWithPartial_CoDL_FixNoSent extends CoDLWrapper_LBJ<LinkedVector,Token> {
    private double graphSamplingRate = 1;
    private int graphSamplingMode = 1;// 0,2,3: sample edges in graph. 1: sample docs.
    private int learningRound = 20;
    private int inferenceMode = 0;
    private boolean partialInBaseCls = false;

    private static CommandLine cmd;
    private static boolean debug = false;

    public ChunkerTrainWithPartial_CoDL_FixNoSent(boolean OneMdlOrTwoMdl, boolean saveCache, boolean forceUpdate, boolean partialInBaseCls, double lambda, int maxRound, int seed, String modelDir, String modelNamePrefix, double graphSamplingRate, int graphSamplingMode, int learningRound, int inferenceMode) throws Exception {
        super(OneMdlOrTwoMdl, saveCache, forceUpdate, lambda, maxRound, seed, modelDir, modelNamePrefix+String.format("_sm%d_sr%.2f",graphSamplingMode,graphSamplingRate));
        this.graphSamplingRate = graphSamplingRate;
        this.graphSamplingMode = graphSamplingMode;
        this.learningRound = learningRound;
        this.inferenceMode = inferenceMode;
        this.partialInBaseCls = partialInBaseCls;
        CoDL_LoadData();

        initModel();
    }

    public void loadData() throws Exception{
        Parser parser_full = new CoNLL2000Parser("/home/qning2/Servers/root/shared/corpora/corporaWeb/written/eng/chunking/conll2000distributions/train_1-2010.txt");
        //Parser parser_full = new CoNLL2000Parser("/home/qning2/Servers/root/shared/corpora/corporaWeb/written/eng/chunking/conll2000distributions/train_1-113334.txt");
        Parser parser_partial = new CoNLL2000ParserWithPartial("/home/qning2/Servers/root/shared/corpora/corporaWeb/written/eng/chunking/conll2000distributions/train_6004-20018.txt",graphSamplingMode,graphSamplingRate);
        trainStructs_full = new ArrayList<>();
        trainStructs_partial = new ArrayList<>();
        LinkedVector ex;
        if(!debug){
            while ((ex = (LinkedVector) parser_full.next()) != null) {
                trainStructs_full.add(ex);
            }
            while ((ex = (LinkedVector) parser_partial.next()) != null) {
                trainStructs_partial.add(ex);
            }
        }
        else{//debug
            int maxcnt = 1000;
            int cnt = 0;
            while ((ex = (LinkedVector) parser_full.next()) != null) {
                trainStructs_full.add(ex);
                cnt++;
                if(cnt>maxcnt)
                    break;
            }
            cnt = 0;
            while ((ex = (LinkedVector) parser_partial.next()) != null) {
                trainStructs_partial.add(ex);
                cnt++;
                if(cnt>maxcnt)
                    break;
            }
        }
    }

    @Override
    public void loadData_1model() throws Exception {
        loadData();
    }

    @Override
    public void loadData_2model() throws Exception {
        //loadData();
        System.out.println("[ERROR] only handle 1model now.");
        System.exit(-1);
    }

    @Override
    public Learner loadBaseCls() throws Exception {
        if(!forceUpdate&&modelExists()){
            System.out.println(myLogFormatter.fullBlockLog("Model exists. Don't load/retrain base classifier."));
            return null;
        }
        if(partialInBaseCls)
            System.out.println(myLogFormatter.fullBlockLog("Retraining base classifier (incorporate partial documents in)"));
        else
            System.out.println(myLogFormatter.fullBlockLog("Retraining base classifier (don't incorporate partial documents in)"));
        List<LinkedVector> structList4basecls = new ArrayList<>();
        structList4basecls.addAll(trainStructs_full);
        if(partialInBaseCls)
            structList4basecls.addAll(trainStructs_partial);
        List<Token> atomList = new ArrayList<>();
        for(LinkedVector ex:structList4basecls){
            for (int j = 0; j < ex.size(); j++) {
                atomList.add((Token)ex.get(j));
            }
        }
        return learnUtil(atomList,-1);
    }

    public Learner learnUtil(List<Token> atomList, int currIter){
        String modelPath = String.format("%s_currIter%d.lc", modelDir+ File.separator+modelNamePrefix, currIter);
        String lexiconPath = String.format("%s_currIter%d.lex", modelDir+ File.separator+modelNamePrefix, currIter);
        Chunker classifier = new Chunker(modelPath,lexiconPath);

        System.out.println(myLogFormatter.fullBlockLog("Learning from "+atomList.size()+" Tokens."));
        Chunker.isTraining = true;
        classifier.forget();
        classifier.beginTraining();
        for(int iter=0;iter<learningRound;iter++){
            Collections.shuffle(atomList, new Random(seed++));
            for(Token tok:atomList){
                try{
                    if(tok.label.equals(CoNLL2000ParserWithPartial.UNLABELED))
                        continue;
                    classifier.learn(tok);
                }
                catch (Exception e){
                    System.out.println("Exception in learn().");
                    e.printStackTrace();
                }
            }
            classifier.doneWithRound();
        }
        classifier.doneLearning();
        Chunker.isTraining = false;
        return classifier;
    }

    @Override
    public Learner loadSavedCls() throws Exception {
        String[] modelandlexpath = modelAndLexPath();
        return new Chunker(modelandlexpath[0],modelandlexpath[1]);
    }

    @Override
    public void setCacheDir() {
        setDefaultCacheDir();
    }

    @Override
    public String getStructId(LinkedVector st) {
        return null;//don't cache
    }

    private void evaluator_token(List<LinkedVector> goldlist, List<LinkedVector> predlist){
        PrecisionRecallManager myeval = new PrecisionRecallManager();
        for(LinkedVector gold:goldlist){
            LinkedVector pred = predlist.get(goldlist.indexOf(gold));
            for(int i=0;i<gold.size();i++){
                String goldlabel = ((Token)gold.get(i)).label;
                String predlabel = ((Token)pred.get(i)).label;
                myeval.addPredGoldLabels(predlabel,goldlabel);
            }
        }
        myeval.printPrecisionRecall(new String[]{"O"});
    }

    public void evalTest() throws Exception{
        Parser parser_test = new CoNLL2000Parser("/home/qning2/Servers/root/shared/corpora/corporaWeb/written/eng/chunking/conll2000distributions/test.noPOS.txt");
        int inferenceMode_bk = inferenceMode;
        for(int infmod = 0; infmod<2;infmod++) {
            inferenceMode = infmod;
            System.out.println(myLogFormatter.fullBlockLog("EVALUATING TESTSET: INFERENCE MODE = " + inferenceMode));
            parser_test.reset();
            List<LinkedVector> testset = new ArrayList<>(), testset_inf = new ArrayList<>();
            LinkedVector ex;
            while ((ex = (LinkedVector) parser_test.next()) != null) {
                testset.add(ex);
            }
            for (LinkedVector sent : testset) {
                LinkedVector sent_inf = inference(sent, false);
                testset_inf.add(sent_inf);
            }

            // Token eval
            evaluator_token(testset, testset_inf);

            // Span eval
            myBIOTester.test(testset, testset_inf);
        }
        inferenceMode = inferenceMode_bk;
    }

    private LinkedVector localInference(LinkedVector sent, boolean respectExisting){
        LinkedVector sent_copy = (LinkedVector)sent.clone();
        for(int i=0;i<sent_copy.size();i++){
            Token t = (Token) sent_copy.get(i);
            if(!respectExisting || t.label.equals(CoNLL2000ParserWithPartial.UNLABELED)){
                t.label = multiClassifiers.discreteValue(t);
            }
        }
        return sent_copy;
    }
    private LinkedVector greedyInference(LinkedVector sent, boolean respectExisting){
        LinkedVector sent_copy = (LinkedVector)sent.clone();
        for(int i=0;i<sent_copy.size();i++){
            Token t = (Token) sent_copy.get(i);
            if(!respectExisting || t.label.equals(CoNLL2000ParserWithPartial.UNLABELED)){
                ScoreSet scores = multiClassifiers.scores(t);
                double max_score = -Double.MAX_VALUE;
                String pred = "[WRONG]";
                Token t_prev = (Token) t.previous;
                if(t_prev==null||t_prev.label.equals("O")){
                    // cannot start with "I"
                    for(Object v:scores.values()){
                        double score_v = scores.get((String) v);
                        if(!((String)v).startsWith("I") && score_v>max_score){
                            max_score = score_v;
                            pred = (String) v;
                        }
                    }
                }
                else{
                    // previous either B-Type or I-Type
                    // then this must be of same "Type" or "O"
                    String type = t_prev.label.substring(2);
                    for(Object v:scores.values()){
                        double score_v = scores.get((String) v);
                        if((!((String) v).startsWith("I") || ((String)v).substring(2).equals(type))
                                && score_v>max_score){
                            max_score = score_v;
                            pred = (String) v;
                        }
                    }
                }
                t.label = pred;
            }
        }
        return sent_copy;
    }
    private LinkedVector globalInference(LinkedVector sent, boolean respectExisting){
        //todo
        return null;
    }
    private LinkedVector inference(LinkedVector sent, boolean respectExisting){
        switch (inferenceMode){
            case 0:
                return localInference(sent,respectExisting);
            case 1:
                return greedyInference(sent,respectExisting);
            case 2:
                return globalInference(sent,respectExisting);
            default:
                System.out.println("[ERROR] Wrong inference mode.");
                return null;
        }
    }

    @Override
    public LinkedVector inference(LinkedVector doc) {
        return inference(doc,true);
    }

    @Override
    public Learner learn(List<LinkedVector> slist, int curr_round) {
        List<Token> atomList = new ArrayList<>();
        for(LinkedVector ex:slist){
            for (int j = 0; j < ex.size(); j++) {
                atomList.add((Token)ex.get(j));
            }
        }
        return learnUtil(atomList,curr_round);
    }
    public static void cmdParser(String[] args) {
        Options options = new Options();

        Option modelDir = new Option("d", "modelDir", true, "model output directory");
        modelDir.setRequired(true);
        options.addOption(modelDir);

        Option modelName = new Option("n", "modelName", true, "model name");
        modelName.setRequired(true);
        options.addOption(modelName);

        Option samplingRate = new Option("sr", "samplingRate", true, "temporal graph sampling rate to generate partial graphs");
        samplingRate.setRequired(true);
        options.addOption(samplingRate);

        Option samplingMode = new Option("sm", "samplingMode", true, "temporal graph sampling mode (0:graphs. 1: documents)");
        samplingMode.setRequired(true);
        options.addOption(samplingMode);

        Option seed = new Option("sd", "seed", true, "seed");
        seed.setRequired(false);
        options.addOption(seed);

        Option OneMdlOrTwoMdl = new Option("o", "OneMdlOrTwoMdl", false, "1-model or 2-model in codl");
        OneMdlOrTwoMdl.setRequired(false);
        options.addOption(OneMdlOrTwoMdl);

        Option maxIter = new Option("max", "maxIter", true, "max iteration in CoDL");
        maxIter.setRequired(false);
        options.addOption(maxIter);

        Option lambda = new Option("lambda", "lambda", true, "lambda in 2-model");
        lambda.setRequired(false);
        options.addOption(lambda);

        Option inferenceMode = new Option("im", "inferenceMode", true, "inference mode: 0--local, 1--greedy global, 2--global");
        inferenceMode.setRequired(false);
        options.addOption(inferenceMode);

        Option respect = new Option("r", "respect", false, "Respect existing relations in docs.");
        respect.setRequired(false);
        options.addOption(respect);

        Option hard = new Option("h", "hardConstraint", false, "Use existing relations as hard constraints. (no effect when hard=false)");
        hard.setRequired(false);
        options.addOption(hard);

        Option partialInBaseCls = new Option("pib", "partialInBaseCls", false, "Put the partial data in training base classifier");
        partialInBaseCls.setRequired(false);
        options.addOption(partialInBaseCls);

        Option forceUpdate = new Option("f", "forceUpdate", false, "force update in CoDL");
        forceUpdate.setRequired(false);
        options.addOption(forceUpdate);

        Option saveCache = new Option("cache", "cache", false, "save CoDL cache or not");
        saveCache.setRequired(false);
        options.addOption(saveCache);

        Option debug = new Option("debug", "debug", false, "debug (load fewer docs)");
        debug.setRequired(false);
        options.addOption(debug);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("TemprelPerceptronTrainer_EE", options);

            System.exit(1);
        }
    }
    public static void main(String[] args) throws Exception{
        cmdParser(args);
        String modelDir = cmd.getOptionValue("modelDir");
        String modelPrefixName = cmd.getOptionValue("modelName");
        double samplingRate = Double.valueOf(cmd.getOptionValue("samplingRate"));
        int samplingMode = Integer.valueOf(cmd.getOptionValue("samplingMode"));
        int seed = Integer.valueOf(cmd.getOptionValue("seed","0"));
        int maxIter = Integer.valueOf(cmd.getOptionValue("max","1"));

        boolean OneMdlOrTwoMdl = cmd.hasOption("OneMdlOrTwoMdl");
        double lambda = Double.valueOf(cmd.getOptionValue("lambda","0.1"));
        int inferenceMode = Integer.valueOf(cmd.getOptionValue("inferenceMode","0"));
        boolean respect = cmd.hasOption("respect");// todo
        boolean hard = cmd.hasOption("hardConstraint");// todo
        modelPrefixName += String.format("_im%d_%s",inferenceMode,respect?"respect":"norespect");
        if(respect)
            modelPrefixName += hard?"_hardConst":"_softConst";

        ChunkerTrainWithPartial_CoDL_FixNoSent.debug = cmd.hasOption("debug");
        boolean forceUpdate = cmd.hasOption("forceUpdate") || ChunkerTrainWithPartial_CoDL_FixNoSent.debug;
        boolean saveCache = cmd.hasOption("cache");
        boolean partialInBaseCls = cmd.hasOption("partialInBaseCls");

        if(ChunkerTrainWithPartial_CoDL_FixNoSent.debug)
            modelPrefixName += "_debug";
        ChunkerTrainWithPartial_CoDL_FixNoSent exp = new ChunkerTrainWithPartial_CoDL_FixNoSent(OneMdlOrTwoMdl,saveCache,forceUpdate,partialInBaseCls,lambda,maxIter,seed,modelDir,modelPrefixName,samplingRate,samplingMode,20,inferenceMode);
        exp.CoDL();
        if(!ChunkerTrainWithPartial_CoDL_FixNoSent.debug){
            exp.saveClassifiers();
            exp.evalTest();
        }
        else{
            exp.evalTest();
        }
    }
}
