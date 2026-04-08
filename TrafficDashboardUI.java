import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.json.JSONObject;  // org.json library for JSON parsing

public class TrafficDashboardUI {

    static DefaultTableModel model;
    static JLabel trafficLabel, signalLabel, predictionLabel;
    static javax.swing.Timer timer; // Use javax.swing.Timer explicitly
    static Random rand = new Random();

    public static void main(String[] args) {

        JFrame frame = new JFrame("Smart Traffic Dashboard");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // ===== LIGHT BACKGROUND =====
        frame.getContentPane().setBackground(new Color(244, 246, 249));

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(244, 246, 249));

        trafficLabel = createCard("Traffic Volume", "0");
        signalLabel = createCard("Signal", "GREEN");
        predictionLabel = createCard("Prediction", "Smooth");

        topPanel.add(trafficLabel);
        topPanel.add(signalLabel);
        topPanel.add(predictionLabel);

        frame.add(topPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] columns = {
                "Timestamp", "Day", "Weather", "Event",
                "Volume", "Signal", "Congestion", "Prediction"
        };

        model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        // TABLE STYLE (LIGHT)
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(60, 60, 60));
        table.setRowHeight(25);

        // HEADER STYLE
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(220, 230, 240));
        header.setForeground(new Color(50, 50, 50));
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // COLOR RENDERER
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                String congestion = table.getValueAt(row, 6).toString();

                if (congestion.equals("High")) {
                    c.setBackground(new Color(255, 205, 210)); // soft red
                } else if (congestion.equals("Medium")) {
                    c.setBackground(new Color(255, 236, 179)); // soft yellow
                } else {
                    c.setBackground(new Color(200, 230, 201)); // soft green
                }

                c.setForeground(new Color(60, 60, 60));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(244, 246, 249));

        JButton startBtn = new JButton("Start");
        JButton stopBtn = new JButton("Stop");

        startBtn.setBackground(new Color(76, 175, 80)); // green
        startBtn.setForeground(Color.WHITE);

        stopBtn.setBackground(new Color(244, 67, 54)); // red
        stopBtn.setForeground(Color.WHITE);

        bottomPanel.add(startBtn);
        bottomPanel.add(stopBtn);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // ===== TIMER =====
        timer = new javax.swing.Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchAndUpdateData(); // Fetch from Flask API (simulate)
            }
        });

        startBtn.addActionListener(e -> timer.start());
        stopBtn.addActionListener(e -> timer.stop());

        frame.setVisible(true);
    }

    // ===== CARD DESIGN (LIGHT THEME) =====
    static JLabel createCard(String title, String value) {
        JLabel label = new JLabel("<html><center>" + title + "<br><h1>" + value + "</h1></center></html>");
        label.setOpaque(true);

        label.setBackground(new Color(227, 242, 253)); // light blue
        label.setForeground(new Color(40, 40, 40));

        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setHorizontalAlignment(JLabel.CENTER);

        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 220), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        return label;
    }

    // ===== FETCH DATA FROM API (SIMULATED) =====
    static void fetchAndUpdateData() {
        // Simulate API response JSON
        int volume = rand.nextInt(300);
        String signalTime;

        if (volume > 200) {
            signalTime = "Green: 60s | Red: 30s";
        } else if (volume > 100) {
            signalTime = "Green: 40s | Red: 40s";
        } else {
            signalTime = "Green: 20s | Red: 60s";
        }

        // Create JSON object (simulate Flask API response)
        JSONObject obj = new JSONObject();
        obj.put("traffic", volume);
        obj.put("signal_time", signalTime);

        // Parse JSON using org.json
        int predictedTraffic = obj.getInt("traffic");
        String signal = obj.getString("signal_time");

        // CURRENT TIME
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

        // UPDATE CARDS
        trafficLabel.setText("<html><center>Traffic Volume<br><h1>" + predictedTraffic + "</h1></center></html>");
        predictionLabel.setText("<html><center>Prediction<br><h1>" + getPrediction(predictedTraffic) + "</h1></center></html>");
        signalLabel.setText("<html><center>Signal<br><h1>" + signal + "</h1></center></html>");

        // ADD ROW
        model.addRow(new Object[]{
                time,
                "Monday",
                "Clear",
                "None",
                predictedTraffic,
                signal,
                getCongestionLevel(predictedTraffic),
                getPrediction(predictedTraffic)
        });
    }

    // ===== Helper Methods =====
    static String getPrediction(int traffic) {
        if (traffic > 200) return "Heavy Traffic";
        if (traffic > 100) return "Moderate";
        return "Smooth";
    }

    static String getCongestionLevel(int traffic) {
        if (traffic > 200) return "High";
        if (traffic > 100) return "Medium";
        return "Low";
    }
}