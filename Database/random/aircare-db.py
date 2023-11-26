from faker import Faker
import random
import sqlite3
from datetime import datetime, timedelta

fake = Faker()

# Function to generate random sensor data with timestamps every hour
def generate_sensor_data(timestamp):
    return (
        None,
        timestamp,
        abs(min(100, random.expovariate(1/40))),  # Humidity (%), capped at 100
        abs(max(15, min(38, random.expovariate(1/20)))),  # Temperature (°C), capped at 5-38
        abs(min(2500, random.gauss(1250, 1249.9999))),  # CO2 (ppm), with some outliers
        abs(min(0.03, random.gauss(0.01, 0.005))),  # VOC (ppb), capped at 0.03, with some outliers
        abs(min(450, random.gauss(225, 224.999))),  # PM10 (µg/m³), capped at 354, with some outliers
        abs(min(70, random.gauss(35, 34.9999)))  # PM2.5 (µg/m³), capped at 50, with some outliers
    )

# Connect to the SQLite database
conn = sqlite3.connect('aircare.db')
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
for i in range(1000):
    timestamp = start_time + timedelta(hours=i)
    cursor.execute('''
        INSERT INTO sensor_data
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    ''', generate_sensor_data(timestamp))

# Commit the changes and close the connection
conn.commit()
conn.close()
