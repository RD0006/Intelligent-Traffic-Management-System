import java.util.*;

class TrafficRecord {
    String roadId;
    String timestamp;
    int vehicleCount;
    String dayOfWeek;
    String weather;
    String event;

    public TrafficRecord(String roadId, String timestamp, int vehicleCount,
                         String dayOfWeek, String weather, String event) {
        this.roadId = roadId;
        this.timestamp = timestamp;
        this.vehicleCount = vehicleCount;
        this.dayOfWeek = dayOfWeek;
        this.weather = weather;
        this.event = event;
    }

    @Override
    public String toString() {
        return roadId + " | " + timestamp + " | " + vehicleCount + " vehicles";
    }
}

public class TrafficDataStructures {

    // List of all traffic records
    private List<TrafficRecord> trafficRecords = new ArrayList<>();

    // Map of Road ID → Current Vehicle Count
    private Map<String, Integer> roadVehicleMap = new HashMap<>();

    // Queue for vehicles waiting at signals depending upon road
    private Map<String, Queue<String>> roadVehicleQueue = new HashMap<>();

    // Graph for Road Network (Adjacency List)
    private Map<String, List<String>> roadGraph = new HashMap<>();

    public TrafficDataStructures(List<String> initialRoads) {
        for (String road : initialRoads) {
            addRoad(road);
        }
    }

    public synchronized void addRecord(TrafficRecord record) {
        trafficRecords.add(record);
        updateVehicleCount(record.roadId, record.vehicleCount);
    }

    public void addRoad(String roadId) {
        roadGraph.putIfAbsent(roadId, new ArrayList<>());
        roadVehicleQueue.putIfAbsent(roadId, new LinkedList<>());
        roadVehicleMap.putIfAbsent(roadId, 0);
    }

    public void connectRoads(String road1, String road2) {
        roadGraph.get(road1).add(road2);
        roadGraph.get(road2).add(road1); // bidirectional
    }

    public int getVehicleCount(String roadId) {
        return roadVehicleMap.getOrDefault(roadId, 0);
    }

    public synchronized void updateVehicleCount(String roadId, int vehicleCount) {
        roadVehicleMap.put(roadId, vehicleCount);

        Queue<String> queue = roadVehicleQueue.getOrDefault(roadId, new LinkedList<>());
        queue.clear();
        for (int i = 0; i < vehicleCount; i++) {
            queue.add("Vehicle_" + (i + 1));
        }
        roadVehicleQueue.put(roadId, queue);
    }

    public synchronized String popVehicle(String roadId) {
        Queue<String> queue = roadVehicleQueue.get(roadId);
        if (queue != null && !queue.isEmpty()) {
            return queue.poll();
        }
        return null;
    }

    public String getCongestionLevel(String roadId) {
        int count = getVehicleCount(roadId);
        if (count > 100) return "High";
        else if (count > 60) return "Medium";
        else return "Low";
    }

    public void printRoadGraph() {
        for (String road : roadGraph.keySet()) {
            System.out.println(road + " → " + roadGraph.get(road));
        }
    }

    public List<TrafficRecord> getRecordsByRoad(String roadId) {
        List<TrafficRecord> list = new ArrayList<>();
        for (TrafficRecord record : trafficRecords) {
            if (record.roadId.equals(roadId)) {
                list.add(record);
            }
        }
        return list;
    }

    public Set<String> getAllRoads() {
        return roadVehicleMap.keySet();
    }
}