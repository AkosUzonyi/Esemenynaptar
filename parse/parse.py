#!/usr/bin/python

import re
import sys
import sqlite3

first_month = 6 #first month of the first semester - months before this belongs to the second semester
categories = ["irodalom", "tortenelem", "zenetortenet", "vizualis_kultura"]
months = ["Január", "Február", "Március", "Április", "Május", "Június", "Július", "Augusztus", "Szeptember", "Október", "November", "December"]

year_regexp = re.compile(r"(20\d{2})\n")
month_regexp = re.compile(r"(" + "|".join(months) + ")\n")
day_regexp = re.compile(r"(\d+)\. *?\t(.*)\n")
uri_regexp = re.compile(r"http\S*")
uri_www_regexp = re.compile(r"(?<!/)www\S*")
italic_regexp = re.compile(r"„.*?”(.{,3}\(.*?\))?")
ago_regexp = re.compile(r"\d+ éve")
name_regexp = re.compile(r"([A-Z]\w* ){2,3}(?=\(\d{4}.(\d{4})?\))")

if len(sys.argv) < 2:
	print("not enough arguments")
	exit(1)

srcFile = sys.argv[1]
databaseFile = sys.argv[2]

semesterPos = -1 #Incremented if a new semester is found. Even means first, odd means second semester. (semesterPos // 2) is the category index
year = -1
month = -1
day = -1
text = ""
count = 0
cursor = None

def writeRecord():
	global text, count

	if text != "" and semesterPos >= 0 and year >= 0 and month >= 0 and day >= 0:
		text = uri_regexp.sub("<a href='\\g<0>'>\\g<0></a>", text)
		text = uri_www_regexp.sub("<a href='http://\\g<0>'>\\g<0></a>", text)
		text = italic_regexp.sub("<i>\\g<0></i>", text)
		text = ago_regexp.sub("<b>\\g<0></b>", text)
		text = name_regexp.sub("<b>\\g<0></b>", text)

		try:
			cursor.execute("INSERT INTO event(year, month, day, category, text) VALUES(?,?,?,?,?)", (year, month, day, categories[semesterPos // 2], text))
			count += 1
		except sqlite3.IntegrityError as ie:
			print("error:", year, month, day, categories[semesterPos // 2], ie)
	
	text = ""

def printStat():
	global count

	print(year, categories[semesterPos // 2], "->", count, "events")
	count = 0


with open(srcFile, "r", encoding="utf-8") as file, sqlite3.connect(databaseFile) as db:
	cursor = db.cursor()
	cursor.execute("CREATE TABLE IF NOT EXISTS event(year INTEGER NOT NULL, month INTEGER NOT NULL, day INTEGER NOT NULL, category TEXT NOT NULL, text TEXT NOT NULL, PRIMARY KEY (year, month, day, category));")

	for line in file:
		
		yearMatch = year_regexp.fullmatch(line)
		if yearMatch is not None:
			if semesterPos >= 0:
				writeRecord()
				printStat()

			year = int(yearMatch.group(1))
			month = -1
			day = -1
			semesterPos += 1

			#as soon as we know the year, delete old events in this year
			if semesterPos == 0:
				cursor.execute(f"DELETE FROM event where year == ? and month >= {first_month} or year == ? and month < {first_month}", (year, year + 1))

			continue
		
		monthMatch = month_regexp.fullmatch(line)
		if monthMatch is not None:
			writeRecord()
			month = months.index(monthMatch.group(1))
			day = -1
			continue
		
		dayMatch = day_regexp.match(line)
		if dayMatch is not None:

			newDay = int(dayMatch.group(1))
			if day == newDay: #we do not allow multiple events per category on the same day
				text += "<br><br>" #just add a bit separation, and let the next event to be merged into this one
				text += dayMatch.group(2) + "<br>"
				continue

			writeRecord()
			day = newDay
			text += dayMatch.group(2) + "<br>"
			continue
		
		text += line + "<br>"
		
	writeRecord()
	printStat()

	db.commit()
