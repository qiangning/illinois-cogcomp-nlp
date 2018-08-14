package edu.illinois.cs.cogcomp.chunker.main;

import edu.illinois.cs.cogcomp.chunker.main.lbjava.Chunker;
import edu.illinois.cs.cogcomp.chunker.utils.CoNLL2000ParserWithPartial;
import edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token;
import edu.illinois.cs.cogcomp.lbjava.parse.LinkedVector;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;
import org.apache.commons.cli.*;

public class ChunkerTrainWithPartial extends ChunkerTrain{
    private static CommandLine cmd;

    public ChunkerTrainWithPartial(int iter) {
        super(iter);
    }

    @Override
    public void trainModelsWithParser(Parser parser) {
        Chunker.isTraining = true;

        // Run the learner
        for (int i = 1; i <= iter; i++) {
            LinkedVector ex;
            while ((ex = (LinkedVector) parser.next()) != null) {
                for (int j = 0; j < ex.size(); j++) {
                    Token t = (Token)ex.get(j);
                    if(!t.label.equals(CoNLL2000ParserWithPartial.UNLABELED))
                        chunker.learn(t);
                }
            }
            parser.reset();
            chunker.doneWithRound();
            logger.info("Iteration number : " + i);
        }
        chunker.doneLearning();
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

        Option mode = new Option("mode", "samplingMode", true, "0: partial sampling. 1: full sampling.");
        mode.setRequired(true);
        options.addOption(mode);

        Option round = new Option("round", "round", true, "training round");
        round.setRequired(true);
        options.addOption(round);

        Option trainFile = new Option("tf", "trainFile", true, "training file");
        trainFile.setRequired(true);
        options.addOption(trainFile);

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
    public static void main(String[] args) {
        cmdParser(args);
        String modelDir = cmd.getOptionValue("modelDir");
        String modelPrefixName = cmd.getOptionValue("modelName");
        String trainFile = cmd.getOptionValue("trainFile");
        double samplingRate = Double.valueOf(cmd.getOptionValue("samplingRate"));
        int round = Integer.valueOf(cmd.getOptionValue("round"));
        int mode = Integer.valueOf(cmd.getOptionValue("mode"));
        String modelName = String.format("%s_mode%d_sr%.2f",modelPrefixName,mode,samplingRate);

        ChunkerTrainWithPartial trainer = new ChunkerTrainWithPartial(round);
        Parser parser = new CoNLL2000ParserWithPartial(trainFile,mode,samplingRate);
        trainer.trainModelsWithParser(parser);
        trainer.writeModelsToDisk(modelDir,modelName);
    }
}
