from faker import Faker
import random
import sqlite3
from datetime import datetime, timedelta

fake = Faker()

# Function to generate random sensor data with timestamps every hour
def generate_sensor_data(timestamp, prev_data=None):
    if prev_data is None:
        # Generate initial random values
        humidity = abs(random.uniform(20, 80))
        temperature = abs(random.uniform(18, 30))
        co2 = abs(random.gauss(1000, 500))
        voc = abs(random.gauss(0.01, 0.005))
        pm10 = abs(random.gauss(150, 75))
        pm25 = abs(random.gauss(20, 10))
    else:
        # Generate values based on the previous values with some random changes
        humidity = abs(max(0, min(100, prev_data[2] + random.uniform(-1, 1))))
        temperature = abs(max(18, min(38, prev_data[3] + random.uniform(-1, 1))))
        co2 = abs(max(0, min(2500, prev_data[4] + random.gauss(0, 50))))
        voc = abs(max(0, min(0.03, prev_data[5] + random.gauss(0, 0.005))))
        pm10 = abs(max(0, min(450, prev_data[6] + random.gauss(0, 10))))
        pm25 = abs(max(0, min(75, prev_data[7] + random.gauss(0, 5))))

    return (
        None,
        timestamp,
        humidity,
        temperature,
        co2,
        voc,
        pm10,
        pm25
    )

# Connect to the SQLite database
conn = sqlite3.connect('aircare_biased.db')
cursor = conn.cursor()

# Create the table if it doesn't exist
cursor.execute('''
    CREATE TABLE IF NOT EXISTS sensor_data (
        ReadingID INTEGER PRIMARY KEY AUTOINCREMENT,
        Timestamp DATETIME,
        Humidity FLOAT,
        Temperature FLOAT,
        CO2 FLOAT,
        VOC FLOAT,
        PM10 FLOAT,
        PM25 FLOAT
    )
''')

# Generate and insert 1000 dummy entries with timestamps every hour
start_time = datetime.now()
previous_data = None
for i in range(1000):
    timestamp = start_time + timedelta(hours=i)
    data = generate_sensor_data(timestamp, previous_data)
    cursor.execute('''
        INSERT INTO sensor_data
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    ''', data)
    previous_data = data

# Commit the changes and close the connection
conn.commit()
conn.close()