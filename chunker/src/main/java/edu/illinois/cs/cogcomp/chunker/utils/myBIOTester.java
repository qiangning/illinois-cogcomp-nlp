package edu.illinois.cs.cogcomp.chunker.utils;

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;
import edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete;
import edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token;
import edu.illinois.cs.cogcomp.lbjava.parse.LinkedVector;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;
import edu.illinois.cs.cogcomp.temporal.utils.CoDL.ScoringFunc;

import java.util.List;
import java.util.Vector;

public class myBIOTester {
    private ScoringFunc<Token> classifier;
    protected Classifier labeler;
    protected Parser parser;

    public myBIOTester(ScoringFunc<Token> classifier, Classifier labeler, Parser parser) {
        this.classifier = classifier;
        this.labeler = labeler;
        this.parser = parser;
    }

    public TestDiscrete test() {
        TestDiscrete results = new TestDiscrete();
        results.addNull("O");
        parser.reset();
        for (Token t = (Token) parser.next(); t != null;
             t = (Token) parser.next()) {
            Vector<Token> vector = new Vector<>();
            for (; t.next != null; t = (Token) parser.next()) vector.add(t);
            vector.add(t);

            int N = vector.size();
            String[] predictions = new String[N], labels = new String[N];

            for (int i = 0; i < N; ++i) {
                predictions[i] = classifier.discreteValue(vector.get(i));
                labels[i] = labeler.discreteValue(vector.get(i));
            }

            for (int i = 0; i < N; ++i) {
                String p = "O", l = "O";
                int pEnd = -1, lEnd = -1;

                if (predictions[i].startsWith("B-")
                        || predictions[i].startsWith("I-")
                        && (i == 0
                        || !predictions[i - 1]
                        .endsWith(predictions[i].substring(2)))) {
                    p = predictions[i].substring(2);
                    pEnd = i;
                    while (pEnd + 1 < N && predictions[pEnd + 1].equals("I-" + p))
                        ++pEnd;
                }

                if (labels[i].startsWith("B-")
                        || labels[i].startsWith("I-")
                        && (i == 0 || !labels[i - 1].endsWith(labels[i].substring(2)))) {
                    l = labels[i].substring(2);
                    lEnd = i;
                    while (lEnd + 1 < N && labels[lEnd + 1].equals("I-" + l)) ++lEnd;
                }

                if (!p.equals("O") || !l.equals("O")) {
                    if (pEnd == lEnd) results.reportPrediction(p, l);
                    else {
                        if (!p.equals("O")) results.reportPrediction(p, "O");
                        if (!l.equals("O")) results.reportPrediction("O", l);
                    }
                }
            }
        }

        return results;
    }
    public static void test(List<LinkedVector> goldlist, List<LinkedVector> predlist) {
        TestDiscrete results = new TestDiscrete();
        results.addNull("O");
        for(int k=0;k<goldlist.size();k++){
            LinkedVector ex_gold = goldlist.get(k);
            LinkedVector ex_pred = predlist.get(k);

            Vector<Token> vector_gold = new Vector<>();
            for (int j=0;j<ex_gold.size();j++) vector_gold.add((Token) ex_gold.get(j));
            Vector<Token> vector_pred = new Vector<>();
            for (int j=0;j<ex_pred.size();j++) vector_pred.add((Token) ex_pred.get(j));

            int N = vector_gold.size();
            String[] predictions = new String[N], labels = new String[N];

            for (int i = 0; i < N; ++i) {
                predictions[i] =vector_pred.get(i).label;
                labels[i] = vector_gold.get(i).label;
            }

            for (int i = 0; i < N; ++i) {
                String p = "O", l = "O";
                int pEnd = -1, lEnd = -1;

                if (predictions[i].startsWith("B-")
                        || predictions[i].startsWith("I-")
                        && (i == 0
                        || !predictions[i - 1]
                        .endsWith(predictions[i].substring(2)))) {
                    p = predictions[i].substring(2);
                    pEnd = i;
                    while (pEnd + 1 < N && predictions[pEnd + 1].equals("I-" + p))
                        ++pEnd;
                }

                if (labels[i].startsWith("B-")
                        || labels[i].startsWith("I-")
                        && (i == 0 || !labels[i - 1].endsWith(labels[i].substring(2)))) {
                    l = labels[i].substring(2);
                    lEnd = i;
                    while (lEnd + 1 < N && labels[lEnd + 1].equals("I-" + l)) ++lEnd;
                }

                if (!p.equals("O") || !l.equals("O")) {
                    if (pEnd == lEnd) results.reportPrediction(p, l);
                    else {
                        if (!p.equals("O")) results.reportPrediction(p, "O");
                        if (!l.equals("O")) results.reportPrediction("O", l);
                    }
                }
            }
        }

        results.printPerformance(System.out);
    }
}
