from flask import Flask, render_template, request, jsonify
from model import predict_traffic  

app = Flask(__name__)

def calculate_signal_time(traffic):
    if traffic > 100:
        return {"green": 60, "red": 30}
    elif traffic > 60:
        return {"green": 40, "red": 40}
    else:
        return {"green": 20, "red": 60}

# Web UI Route 
@app.route('/', methods=['GET', 'POST'])
def index():
    traffic = None
    signal_time = None

    if request.method == 'POST':
        # Read form inputs
        hour = int(request.form.get('hour', 0))
        day_of_week = int(request.form.get('day_of_week', 0))
        month = int(request.form.get('month', 1))
        
        # Weather/event input as 0 if not provided
        weather_vector = None
        event = 0

        # Get prediction from LSTM
        traffic = predict_traffic(hour, day_of_week, month, weather_vector, event)
        signal_time = calculate_signal_time(traffic)

    return render_template('index.html', traffic=traffic, signal_time=signal_time)

# API Route for Java Swing or other clients 
@app.route('/predict', methods=['GET'])
def api_predict():
    try:
        hour = int(request.args.get('hour', 0))
        day_of_week = int(request.args.get('day_of_week', 0))
        month = int(request.args.get('month', 1))
        
        # Weather/event input as 0 if not provided
        weather_vector = None
        event = 0

        # Predict traffic
        traffic = predict_traffic(hour, day_of_week, month, weather_vector, event)
        signal_time = calculate_signal_time(traffic)

        return jsonify({
            "traffic": traffic,
            "signal_time": signal_time
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 400

# Run Flask app
if __name__ == '__main__':
    app.run(debug=True)