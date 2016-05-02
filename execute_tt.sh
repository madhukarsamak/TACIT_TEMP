###
use this script to execute turbotopics
###
#!/bin/bash
CORPUS=$1
ASSIGN=$2
VOCAB=$3
OUT=$4
N_TOPICS=$5
MIN_COUNT=$6
PVAL=$7
PERM=$8
PREFIX=$9

mvn clean install
java -cp target/tt-1-jar-with-dependencies.jar LDAtopics $CORPUS $ASSIGN $VOCAB $OUT $N_TOPICS $MIN_COUNT $PVAL $PERM $PREFIX
