# importing libraries
import numpy as np
import pandas as pd
from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_absolute_error, mean_squared_error
from tensorflow.keras.models import Sequential, load_model
from tensorflow.keras.layers import LSTM, Dense, Dropout
import joblib
import matplotlib.pyplot as plt


# PREPROCESSING

# loading dataset
df = pd.read_csv("traffic_data.csv", parse_dates = ["Timestamp"])

# extracting features 
df["Hour"] = df["Timestamp"].dt.hour
df["Month"] = df["Timestamp"].dt.month

# features
features = ["Hour", "Day_of_Week", "Month", "Weather", "Event"]
target = ["Traffic_Volume"]

# encoding weather data
weather_encoded = pd.get_dummies(df["Weather"], prefix = "Weather")
df = pd.concat([df, weather_encoded], axis = 1)
df.drop("Weather", axis = 1, inplace = True)

# scaling features to range [0, 1]
feature_scaler = MinMaxScaler()
scaled_features = feature_scaler.fit_transform(df.drop(["Timestamp", "Traffic_Volume"], axis = 1))
joblib.dump(feature_scaler, "feature_scaler.save")

target_scaler = MinMaxScaler()
scaled_target = target_scaler.fit_transform(df[["Traffic_Volume"]])
joblib.dump(target_scaler, "target_scaler.save")

# creating sequences
def create_sequences(features, target, seq_length = 12):
    X = []
    y = []
    for i in range(seq_length, len(features)):
        X.append(features[i - seq_length : i])
        y.append(target[i])
    return np.array(X), np.array(y)

seq_length = 12
X, y = create_sequences(scaled_features, scaled_target, seq_length)

# splitting dataset into training and testing sets with ratio 80 : 20
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size = 0.2, shuffle = True, random_state = 42
)


# LSTM MODEL

# building model
model = Sequential()
model.add(LSTM(50, activation = "relu", input_shape = (X_train.shape[1], X_train.shape[2])))
model.add(Dropout(0.2))
model.add(Dense(1))

model.compile(optimizer = "adam", loss = "mse")
print(model.summary())

# training the model
history = model.fit(
    X_train,
    y_train,
    epochs = 10,
    batch_size = 64, 
    validation_split = 0.2
)

# saving model
model.save("lstm.keras")

# loading model and scalers
model = load_model("lstm.keras")
feature_scaler = joblib.load("feature_scaler.save")
target_scaler = joblib.load("target_scaler.save")

# prediction
y_pred = model.predict(X_test)
y_test_original = target_scaler.inverse_transform(y_test.reshape(-1, 1))
y_pred_original = target_scaler.inverse_transform(y_pred.reshape(-1, 1))

# evaluation of model - mae, mse, and rmse
mae = mean_absolute_error(y_test_original, y_pred_original)
print(f"Mean Absolute Error (MAE): {mae:.2f}")

mse = mean_squared_error(y_test_original, y_pred_original)
print(f"Mean Squared Error (MSE): {mse:.2f}")

rmse = np.sqrt(mse)
print(f"Root Mean Squared Error (RMSE): {rmse:.2f}")

# visualization
plt.figure(figsize = (15, 5))
plt.plot(y_test_original[ : 500], label = "Actual")
plt.plot(y_pred_original[ : 500], label = "Predicted")
plt.title("Traffic Volume Prediction (First 500 Data Records)")
plt.xlabel("Time Steps")
plt.ylabel("Normalized Traffic Volume")
plt.legend()
plt.show()