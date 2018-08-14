#!/usr/bin/env bash
#----Please make sure "illinois-chunker-model" is removed from the dependency list in pom.xml----#

#   Specify the training set
TESTFILE=/home/qning2/Servers/root/shared/corpora/corporaWeb/written/eng/chunking/conll2000distributions/test.noPOS.txt

#   Specify the dir and name to save the resulting model
MODELDIR=model
MODELNAMEPRE=chunker_partial
for mode in 0 1
do
	for sr in 0.01 0.05 0.09
	do
		MODELNAME=${MODELNAMEPRE}_mode${mode}_sr${sr}
		echo $MODELNAME
		mvn exec:java -Dexec.mainClass=edu.illinois.cs.cogcomp.chunker.main.ChunkTester -Dexec.args="$TESTFILE $MODELDIR $MODELNAME" > logs/$MODELNAMEPRE-test-mode${mode}-sr${sr}.txt
	done
done