#!/usr/bin/env bash
MODELDIR=model
mkdir -p $MODELDIR
MODELNAMEPRE=chunker_partial_codl
MAXITER=1
for sd in 0 1 2 3
do
	for im in 0 1
	do
		for sm in 0 1
		do
			for sr in 0.2 0.4 0.6 0.8 1.0
			do
				echo "sm=$sm, sr=$sr"
				mvn exec:java -Dexec.mainClass=edu.illinois.cs.cogcomp.chunker.main.ChunkerTrainWithPartial_CoDL_FixNoSent -Dexec.args="-d $MODELDIR -n $MODELNAMEPRE -sd $sd -sr $sr -sm $sm -max $MAXITER -f -im $im -pib -r -h -o" > logs/codl/$MODELNAMEPRE-sm${sm}-sr${sr}-im${im}-respect-hardConst-sd${sd}-1mdl-round${MAXITER}.txt
			done
		done
	done
done