import sys
import os
pypath = sys.argv[1]
japath = sys.argv[2]
pyfiles = os.listdir(pypath)
jafiles = os.listdir(japath)
pylen = len(pyfiles)
jalen = len(jafiles)

for pyfile in pyfiles:
    if pyfile not in jafiles:
        print("----- "+pyfile+" missing------ ")
        continue
    print("----------- "+pyfile+" ---------")
    pycontent = open(pypath+"/"+pyfile).readlines()
    jacontent = open(japath+"/"+pyfile).readlines()
    if(len(pycontent) != len(jacontent)):
        print("the length of the file is different")
        continue
    pycontent = sorted(pycontent)
    jacontent = sorted(jacontent)
    for id in range(len(pycontent)):
        if pycontent[id] != jacontent[id]:
            print("unmatched line : "+str(id))
            print(pycontent[id])
            print(jacontent[id])


