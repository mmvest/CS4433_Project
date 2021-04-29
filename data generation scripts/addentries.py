import mysql.connector
import random
import datetime

mydb = mysql.connector.connect(
    host="localhost",
    user="apiuser",
    password="apiUSERp@55w0rd",
    database="test_timekeeper"
)

mycursor = mydb.cursor()

#read in file
userFile = open("users.csv", "r")
userFileLines = userFile.readlines()

#parse usernames; store them in array
usernames=[" "] * 100
i = 0
for user in userFileLines:
    result = user.split(',')
    usernames[i] = result[0].replace(" ", "")
    i += 1

#get all the category names and store them in an array
query = "SELECT name FROM category"
mycursor.execute(query)
result = mycursor.fetchall()

#add all the categories to list
categories = [name[0] for name in result]

#strip all leading and trailing spaces
for category in categories:
    category = category.strip()

#define notes here if needed
note = ""

#define a start and end date
startDate = datetime.datetime(2021, 1, 1)
endDate = datetime.datetime(2021, 4, 30)

#Now for each username, generate entries
for username in usernames:
    start_date_time = startDate;
    while start_date_time< endDate:

        #generate random hours
        hourRange = random.randrange(1,9) + 1 #random hours from 1 to 8
        minuteRange = random.randrange(1,60) #random minutes from 1 to 59
        #select a random category
        category_name = random.choice(categories)
        
        end_date_time = start_date_time + datetime.timedelta(hours=hourRange, minutes=minuteRange)

        #insert into database
        query = "INSERT INTO entry (start_date_time, end_date_time, note, username, category_name) VALUES (%s, %s, %s, %s, %s)"
        values = (start_date_time, end_date_time, note, username, category_name)
        mycursor.execute(query, values)

        mydb.commit()
        start_date_time = end_date_time
