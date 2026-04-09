# Intelligent-Traffic-Management-System

# Intelligent Traffic Management System (ITMS)

## Overview
The **Intelligent Traffic Management System (ITMS)** is a smart traffic prediction and management platform that integrates **LSTM-based traffic forecasting**, **traffic analytics**, and **real-time visualization** for urban roads. The system predicts traffic volume based on historical data, weather, and special events, and adjusts traffic signal timings dynamically. It features a web interface powered by Flask and a desktop dashboard using Java Swing. 

---

## Features

### Traffic Prediction
- Predicts traffic volume using an **LSTM neural network** trained on historical traffic data.
- Handles input features such as hour, day of the week, month, weather conditions, and events.
- Provides a scalable prediction API via Flask for web and desktop clients.

### Signal Timing Optimization
- Determines traffic signal durations based on predicted traffic:
  - High traffic: longer green signal
  - Medium traffic: balanced green/red
  - Low traffic: longer red signal
- Can be integrated into real-world traffic light controllers or simulations.

### Data Analytics
- **TrafficAnalytics** module for:
  - Peak hour analysis
  - Daily traffic trends
  - Weather impact on traffic
  - Visualization: hourly/daily bar charts, heatmaps
- Supports analysis from CSV files or database connections.

### Dashboard Visualization
- **Java Swing-based dashboard** displays:
  - Live traffic volume
  - Predicted congestion
  - Signal timing recommendations
  - Historical data in a table with color-coded congestion levels
- Updates every few seconds using simulated or API-driven data.

### Synthetic Data Generation
- Generates synthetic traffic datasets for testing and training:
  - Handles rush hours, weekends, special events, and weather conditions.
  - Creates CSV files compatible with the LSTM model and analytics tools.

---

## Model Performance

| Metric | Value  |
|--------|--------|
| MAE    | 14.21  |
| MSE    | 307.70 |
| RMSE   | 17.54  |

> The LSTM model predicts traffic volumes with reasonable accuracy, suitable for dynamic signal optimization and congestion forecasting.

---

## License
This project is licensed under the **MIT License**.

## Collaborators
- Radhika Diwan
- Raushan Singh
- Satyam Chaudhary
- Shreyansh Shrivastava
- Utsav Shrivastava