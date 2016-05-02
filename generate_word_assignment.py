import sys
import os

corpus_file = sys.argv[1]
word_weight_file=sys.argv[2]

word_weights = open(word_weight_file,"r").readlines()
word_topic={}
for line in word_weights:
    line = line.strip("\n")
    components = line.split("\t")
    topic = int(components[0])
    word = components[1]
    weight = float(components[2])
    curr_assign = word_topic.get(word,{})
    curr_weight = curr_assign.get("weight",0.0)
    if curr_weight < weight:
        word_topic[word] = {"topic":topic,"weight":weight}

#print(word_topic)
#print(str(len(word_topic)))

vocab = {}
count = 0
for word in sorted(word_topic.keys()):
    vocab[word] = count
    count = count + 1

parentpath = corpus_file+"/"
if os.path.isdir(corpus_file):
    list_of_files = sorted(os.listdir(corpus_file))
else:
    list_of_files = []
    list_of_files.append(corpus_file)
    parentpath=""

globali = 0
for file in list_of_files:
    content = open(parentpath+file,"r").readlines()
    for i in range(len(content)):
        annotated_line = str(globali)
        line = content[i].strip("\n")
        words = line.split(" ")
        for word in words:
            if word in vocab and word in word_topic:
                annotated_line = annotated_line+" "+str(vocab[word])+":"+str(word_topic[word]["topic"])
        globali = globali + 1
        print(annotated_line)







