import re
import os
import codecs
import errno

months = ["Január", "Február", "Március", "Április", "Május", "Június", "Július", "Augusztus", "Szeptember", "Október", "November", "December"]

category_regexp = re.compile("#category (.*)\n")
month_regexp = re.compile("(" + "|".join(months) + ")\n")
day_regexp = re.compile("(\d+)\. *?\t(.*)\n")
uri_regexp = re.compile("(?<=\s)http\S*")
uri_www_regexp = re.compile("(?<=\s)www\S*")

dir = "alma"

category = "?"
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
	if text == "":
		return
	
	text = uri_regexp.sub("<a href='\\g<0>'>\\g<0></a>", text);
	text = uri_www_regexp.sub("<a href='http://\\g<0>'>\\g<0></a>", text);
	
	if True:
		dirPath = os.path.join(dir, category, str(month))
		mkdir_p(dirPath)
		filePath = os.path.join(dirPath, day + ".txt")
		with codecs.open(filePath, "w", "utf-8") as file:
			file.write(text)
	else:
		print(category, month, day, text[0:40], sep="\t")
	
	text = ""

with open("plain.txt", "r", encoding="utf-8") as file:
	for line in file:
		
		#print(line)
		categoryMatch = category_regexp.fullmatch(line)
		if categoryMatch is not None:
			printBuf()
			category = categoryMatch.group(1)
			#print(category)
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
