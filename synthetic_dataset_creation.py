# importing libraries
import numpy as np
import pandas as pd

# generating timestamps for the entire year of 2025 at 5-minute intervals
timestamps = pd.date_range(start = "2025-01-01", end = "2025-12-31", freq = "5min")

# initializing list to store traffic data
data = []

# looping through timestamps to simulate traffic 
for time in timestamps:

    # hour and day
    hour = time.hour
    day = time.dayofweek

    # handling rush-hour vs peak-hour data
    if (8 <= hour <= 11) or (17 <= hour <= 21):
        base_traffic = np.random.randint(50, 120)
    else:
        base_traffic = np.random.randint(20, 60)

    # handling data for weekends
    if day >= 5:
        base_traffic -= 10
        if (day == 6) and (7 <= hour <= 11):
            base_traffic -= 10
    elif (day == 4) and (17 <= hour <= 21):
        base_traffic += 20

    # simulating the impact of weather on data
    weather = np.random.choice(
        ["clear", "cloudy", "rain"],
        p = [0.6, 0.2, 0.2]
    )

    if weather == "rain":
        base_traffic += 10
    elif weather == "snow":
        base_traffic += 15

    # simulating special events (concerts, festivals, sports, etc)
    event = np.random.choice(
        [0, 1], 
        p = [0.9, 0.1]
    )

    if event == 1:
        base_traffic += 20


    # setting realistic minimum trheshold for data
    base_traffic = max(base_traffic, 10)

    # appending data to the data list
    data.append([time, day, weather, event, base_traffic])

# converting list into DataFrame
df = pd.DataFrame(
    data, 
    columns = ["Timestamp", "Day_of_Week", "Weather", "Event", "Traffic_Volume"]
)

# exporting DataFrame as a CSV file
df.to_csv("traffic_data.csv", index = False)
    