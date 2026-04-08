import java.util.*;

// ===== Traffic Record Class =====
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

// ===== Traffic Data Structures =====
public class TrafficDataStructures {

    // 1. List of all traffic records
    private List<TrafficRecord> trafficRecords = new ArrayList<>();

    // 2. Map: Road ID → Current Vehicle Count
    private Map<String, Integer> roadVehicleMap = new HashMap<>();

    // 3. Queue for vehicles waiting at signals per road
    private Map<String, Queue<String>> roadVehicleQueue = new HashMap<>();

    // 4. Graph: Road Network (Adjacency List)
    private Map<String, List<String>> roadGraph = new HashMap<>();

    // ===== Constructor to preload roads =====
    public TrafficDataStructures(List<String> initialRoads) {
        for (String road : initialRoads) {
            addRoad(road);
        }
    }

    // ===== Add new traffic record =====
    public synchronized void addRecord(TrafficRecord record) {
        trafficRecords.add(record);
        updateVehicleCount(record.roadId, record.vehicleCount);
    }

    // ===== Add road to road network =====
    public void addRoad(String roadId) {
        roadGraph.putIfAbsent(roadId, new ArrayList<>());
        roadVehicleQueue.putIfAbsent(roadId, new LinkedList<>());
        roadVehicleMap.putIfAbsent(roadId, 0);
    }

    // ===== Connect roads =====
    public void connectRoads(String road1, String road2) {
        roadGraph.get(road1).add(road2);
        roadGraph.get(road2).add(road1); // bidirectional
    }

    // ===== Get vehicle count for road =====
    public int getVehicleCount(String roadId) {
        return roadVehicleMap.getOrDefault(roadId, 0);
    }

    // ===== Update vehicle count from LSTM prediction =====
    public synchronized void updateVehicleCount(String roadId, int vehicleCount) {
        roadVehicleMap.put(roadId, vehicleCount);

        // Update queue for simulation
        Queue<String> queue = roadVehicleQueue.getOrDefault(roadId, new LinkedList<>());
        queue.clear();
        for (int i = 0; i < vehicleCount; i++) {
            queue.add("Vehicle_" + (i + 1));
        }
        roadVehicleQueue.put(roadId, queue);
    }

    // ===== Pop vehicle from queue =====
    public synchronized String popVehicle(String roadId) {
        Queue<String> queue = roadVehicleQueue.get(roadId);
        if (queue != null && !queue.isEmpty()) {
            return queue.poll();
        }
        return null;
    }

    // ===== Get congestion level =====
    public String getCongestionLevel(String roadId) {
        int count = getVehicleCount(roadId);
        if (count > 100) return "High";
        else if (count > 60) return "Medium";
        else return "Low";
    }

    // ===== Print road network =====
    public void printRoadGraph() {
        for (String road : roadGraph.keySet()) {
            System.out.println(road + " → " + roadGraph.get(road));
        }
    }

    // ===== Get traffic records by road =====
    public List<TrafficRecord> getRecordsByRoad(String roadId) {
        List<TrafficRecord> list = new ArrayList<>();
        for (TrafficRecord record : trafficRecords) {
            if (record.roadId.equals(roadId)) {
                list.add(record);
            }
        }
        return list;
    }

    // ===== Get all roads =====
    public Set<String> getAllRoads() {
        return roadVehicleMap.keySet();
    }
}