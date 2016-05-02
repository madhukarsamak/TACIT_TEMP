import sys
word_assignment = sys.argv[1]
lines = open(word_assignment,"r").readlines()
vocab = {}
for line in lines:
    line = line.strip("\n")
    vocab[line.split("\t")[1]] = 0
vocab = sorted(vocab.keys())
for word in vocab:
    print(word)
