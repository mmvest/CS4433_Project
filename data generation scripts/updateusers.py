import mysql.connector
import random
import os
import hashlib
import datetime

mydb = mysql.connector.connect(
    host="localhost",
    user="apiuser",
    password="apiUSERp@55w0rd",
    database="test_timekeeper"
)

mycursor = mydb.cursor()

#read in file
userFile = open("users.csv", "r+")
userFileLines = userFile.readlines()

#parse username and passwords; store them in arrays
usernames=[" "] * 100
passwords=[" "] * 100
i = 0
for user in userFileLines:
    result = user.split(',')
    usernames[i] = result[0].replace(" ", "").rstrip()
    passwords[i] = result[1].replace(" ", "").rstrip()
    i += 1

i = 0
for username in usernames:
    #generate 64 character salt
    salt = os.urandom(32).hex()

    #append salt to password
    password = salt + passwords[i]

    #hash password
    password = hashlib.sha256(password.encode()).hexdigest()

    #determine creation time
    timeOfCreation = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    #update user passwords in database
    query = "UPDATE user SET salt = %s, password=%s WHERE username = %s"
    values = (salt, password, username)
    mycursor.execute(query, values)

    mydb.commit()

    print(mycursor.rowcount, "records updated.")
    i += 1
