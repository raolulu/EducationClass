#!/usr/bin/python3 
#-*-:coding:utf-8-*-


import os


os.system("rm -rf ./*.class")

root = os.getcwd();

for root,dirs,files in os.walk(root):
    print(root)
    print(dirs)
    print(files)

    os.system("rm -rf " + root + "/*。class");


os.system("javac -cp .:$LIB_JAR_PATH MainRun.java")
os.system("java -cp .:$LIB_JAR_PATH MainRun")
