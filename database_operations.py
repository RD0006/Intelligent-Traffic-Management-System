import csv
import mysql.connector

conn = mysql.connector.connect(
    host="localhost",
    user="root",
    password="@radhika06",
    database="Intelligent_Traffic_Management_System"
)

cursor = conn.cursor()

with open('traffic_data.csv', 'r') as file:
    csv_reader = csv.reader(file)
    
    next(csv_reader)
    
    for row in csv_reader:
        cursor.execute("""
            INSERT INTO TrafficData
            (road_id, timestamp, day_of_week, weather, event, traffic_volume)
            VALUES (%s, %s, %s, %s, %s, %s)
        """, row)


with open('road_data.csv', 'r') as file:
    csv_reader = csv.reader(file)
    
    next(csv_reader)
    
    for row in csv_reader:
        cursor.execute("""
            INSERT INTO Roads
            (road_id, road_name, location)
            VALUES (%s, %s, %s)
        """, row)

conn.commit()

cursor.close()
conn.close()