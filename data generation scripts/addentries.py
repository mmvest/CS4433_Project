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

#define note since it will be the same for all...
note = ""

#define a start and end date
startDate = datetime.datetime(2021, 4, 1)
endDate = datetime.datetime.today() - datetime.timedelta(days=1) #create entries up to yesterday
totalDays = (endDate - startDate).days #total number of days between start and end
print("Total Days Since Start of March: %d" % totalDays)

#Create a schedule
#thingsToDo = ['With Friends', 'Workout', 'Recreation', 'Service', 'Watching TV', 'Streaming (Netflix, Hulu, YouTube, etc...)', 'Practice (Sports, Music, Etc...)', 'Hobby', 'Studying', 'School', 'Homework', 'Traveling']
schedule = [5, 0.5, 0.5, 4, 1, 4, 2, 2, 2, 3] #hours
print("There are %d users that each have %d timeslots per day. Since there are %d days to schedule, that makes a total of %d entries per user and %d entries over all." % (len(usernames), len(schedule), totalDays, (totalDays * len(schedule)), (totalDays * len(schedule) * len(usernames))))

#Now for each username, generate 100 entries
for username in usernames:
    i = 0
    
    start_date_time = startDate;
    while i < totalDays:

        #keep track of the number of hours
        for timeSlot in schedule:
            category_name = ''
            if timeSlot == 5:
                category_name = 'Sleeping'
            else:
                #select a random category
                category_name = random.choice(categories)
            
            end_date_time = start_date_time + datetime.timedelta(hours=timeSlot)

            #insert into database
            query = "INSERT INTO entry (start_date_time, end_date_time, note, username, category_name) VALUES (%s, %s, %s, %s, %s)"
            values = (start_date_time, end_date_time, note, username, category_name)
            mycursor.execute(query, values)

            mydb.commit()
            start_date_time = end_date_time

        i += 1
