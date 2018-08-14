#!/usr/bin/env bash
#----Please make sure "illinois-chunker-model" is removed from the dependency list in pom.xml----#

#   Specify the training set
TRAINFILE=/home/qning2/Servers/root/shared/corpora/corporaWeb/written/eng/chunking/conll2000distributions/train_1-6003.txt
TESTFILE=/home/qning2/Servers/root/shared/corpora/corporaWeb/written/eng/chunking/conll2000distributions/test.noPOS.txt


#   Specify the training round
ROUND=30

#   Specify the dir and name to save the resulting model
MODELDIR=model
mkdir -p $MODELDIR
MODELNAME=chunker_base_6003

mvn exec:java -Dexec.mainClass=edu.illinois.cs.cogcomp.chunker.main.ChunkerTrain -Dexec.args="$TRAINFILE $MODELDIR $MODELNAME $ROUND 0.2" > logs/$MODELNAME-train-test.txt

mvn exec:java -Dexec.mainClass=edu.illinois.cs.cogcomp.chunker.main.ChunkTester -Dexec.args="$TESTFILE $MODELDIR $MODELNAME" >> logs/$MODELNAME-train-test.txt