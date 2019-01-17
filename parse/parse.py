#!/usr/bin/python

import re
import os
import codecs
import errno
import sys

categories = ["irodalom", "tortenelem", "zenetortenet", "vizualis_kultura"]
months = ["Január", "Február", "Március", "Április", "Május", "Június", "Július", "Augusztus", "Szeptember", "Október", "November", "December"]

year_regexp = re.compile("20(\d){2}\n")
month_regexp = re.compile("(" + "|".join(months) + ")\n")
day_regexp = re.compile("(\d+)\. *?\t(.*)\n")
uri_regexp = re.compile("(?<=\s)http\S*")
uri_www_regexp = re.compile("(?<=\s)www\S*")

if len(sys.argv) < 2:
	print("not enough arguments")
	exit(1)

srcFile = sys.argv[1]
dstDir = sys.argv[2]

semesterPos = -1
month = "?"
day = "?"
text = ""

def mkdir_p(path):
    try:
        os.makedirs(path)
    except OSError as exc:
        if exc.errno == errno.EEXIST and os.path.isdir(path):
            pass
        else:
            raise

def printBuf():
	global text

	if text != "" and semesterPos >= 0:
		text = uri_regexp.sub("<a href='\\g<0>'>\\g<0></a>", text);
		text = uri_www_regexp.sub("<a href='http://\\g<0>'>\\g<0></a>", text);
		
		dirPath = os.path.join(dstDir, categories[semesterPos // 2], str(month))
		mkdir_p(dirPath)
		filePath = os.path.join(dirPath, day + ".txt")
		with codecs.open(filePath, "w", "utf-8") as file:
			file.write(text)
	
	text = ""

with open(srcFile, "r", encoding="utf-8") as file:
	for line in file:
		
		yearMatch = year_regexp.fullmatch(line)
		if yearMatch is not None:
			printBuf()
			semesterPos += 1
			continue
		
		monthMatch = month_regexp.fullmatch(line)
		if monthMatch is not None:
			printBuf()
			month = months.index(monthMatch.group(1))
			continue
		
		dayMatch = day_regexp.match(line)
		if dayMatch is not None:
			printBuf()
			day = dayMatch.group(1)
			text += dayMatch.group(2)
			continue
		
		text += line
		
	printBuf();
