# traffic_analytics.py

import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

class TrafficAnalytics:
    def __init__(self, file_path=None, db_connection=None, table_name=None):
        
        if file_path:
            self.df = pd.read_csv(file_path, parse_dates=["timestamp"])
        elif db_connection and table_name:
            query = f"SELECT * FROM {table_name}"
            self.df = pd.read_sql(query, db_connection, parse_dates=["timestamp"])
        else:
            raise ValueError("Provide either file_path or db_connection + table_name")

        # Extract useful time features
        self.df["hour"] = self.df["timestamp"].dt.hour
        self.df["day"] = self.df["timestamp"].dt.day_name()
    
    def peak_hours(self):
        hourly_traffic = self.df.groupby("hour")["traffic_volume"].mean().sort_values(ascending=False)
        return hourly_traffic
    
    def daily_trends(self):
        daily_traffic = self.df.groupby("day")["traffic_volume"].mean().reindex([
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        ])
        return daily_traffic
    
    def weather_impact(self):
        if "weather" not in self.df.columns:
            return None
        return self.df.groupby("weather")["traffic_volume"].mean().sort_values(ascending=False)
    
    def plot_hourly_traffic(self):
        hourly = self.peak_hours()
        plt.figure(figsize=(10,5))
        sns.barplot(x=hourly.index, y=hourly.values, palette="viridis", hue = hourly.index, legend = False)
        plt.title("Average Traffic Volume by Hour")
        plt.xlabel("Hour of Day")
        plt.ylabel("Average Traffic Volume")
        plt.show()
    
    def plot_daily_traffic(self):
        daily = self.daily_trends()
        plt.figure(figsize=(10,5))
        
        # Assign x to hue and disable legend to avoid palette warning
        sns.barplot(
            x=daily.index, 
            y=daily.values, 
            palette="coolwarm",
            hue=daily.index,
            dodge=False,
            legend=False
        )
        
        plt.title("Average Traffic Volume by Day")
        plt.xlabel("Day of Week")
        plt.ylabel("Average Traffic Volume")
        plt.show()
    
    def plot_heatmap(self):
        heatmap_data = self.df.pivot_table(
            index="day", columns="hour", values="traffic_volume", aggfunc="mean"
        ).reindex([
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        ])
        plt.figure(figsize=(12,6))
        sns.heatmap(heatmap_data, cmap="YlOrRd", annot=True, fmt=".0f")
        plt.title("Traffic Volume Heatmap (Day vs Hour)")
        plt.show()
    
    def summarize(self):
        print("=== Peak Hours ===")
        print(self.peak_hours())
        print("\n=== Daily Trends ===")
        print(self.daily_trends())
        if "weather" in self.df.columns:
            print("\n=== Weather Impact ===")
            print(self.weather_impact())

# ===== Example Usage =====
if __name__ == "__main__":
    analytics = TrafficAnalytics(file_path="traffic_data.csv")
    
    # Print summaries
    analytics.summarize()
    
    # Generate plots
    analytics.plot_hourly_traffic()
    analytics.plot_daily_traffic()
    analytics.plot_heatmap()