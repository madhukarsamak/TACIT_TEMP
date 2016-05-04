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
    pycontent = sorted(pycontent)
    jacontent = sorted(jacontent)
    map = {}
    for cont in pycontent:
        map[cont] = ""
    for id in range(len(jacontent)):
        if jacontent[id] not in map:
            print("this line in java output is not found in python output : line number in java output: "+str(id))
            print(jacontent[id])


