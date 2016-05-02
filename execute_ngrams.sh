####
use this script to execute NGrams
####
#!/bin/bash
CORPUS=$1
OUT=$4
MIN_COUNT=$5
PVAL=$2
PERM=$3
PREFIX=$6

mvn clean install
java -cp target/tt-1-jar-with-dependencies.jar ComputeNGrams $CORPUS $PVAL $OUT $MIN_COUNT $PERM $PREFIX
