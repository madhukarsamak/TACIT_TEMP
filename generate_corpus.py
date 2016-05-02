import sys
import os
corpus_path = sys.argv[1]
files = os.listdir(corpus_path)
for file in files:
    lines = open(corpus_path+"/"+file,"r").readlines()
    for line in lines:
        line = line.strip("\n")
        print(line)
