#!/usr/bin/env bash
#----Please make sure "illinois-chunker-model" is removed from the dependency list in pom.xml----#

#   Specify the training set
TRAINFILE=/home/qning2/Servers/root/shared/corpora/corporaWeb/written/eng/chunking/conll2000distributions/train.txt


#   Specify the training round
ROUND=20

#   Specify the dir and name to save the resulting model
MODELDIR=model
mkdir -p $MODELDIR
MODELNAMEPRE=chunker_partial
for mode in 0 1
do
	for sr in 0.01 0.05 0.09
	do
		echo $mode $sr
		mvn exec:java -Dexec.mainClass=edu.illinois.cs.cogcomp.chunker.main.ChunkerTrainWithPartial -Dexec.args="-tf $TRAINFILE -d $MODELDIR -n $MODELNAMEPRE -round $ROUND -sr $sr -mode $mode" #> logs/$MODELNAMEPRE-train-mode${mode}-sr${sr}.txt
	done
done