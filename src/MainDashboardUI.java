import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class MainDashboardUI extends JFrame {
    private JTabbedPane tabbedPane;
    private StudentManagementPanel studentPanel;

    public MainDashboardUI() {
        setTitle("Class Attendance - Dashboard");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.decode("#eef2f3"));
        tabbedPane.setForeground(Color.decode("#0f3057"));
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.addTab("Faculty Selection", createFacultyPanel());

        add(tabbedPane);
        getContentPane().setBackground(Color.decode("#f0f4ff")); // main window background

        setVisible(true);
    }

    private JPanel createFacultyPanel() {
        JPanel facultyPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        facultyPanel.setBackground(Color.decode("#f0f4ff"));
        facultyPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));

        JLabel subjectLabel = new JLabel("Select Subject:");
        subjectLabel.setForeground(Color.decode("#112d4e"));
        subjectLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JComboBox<String> subjectBox = new JComboBox<>(new String[]{
            "Java", "ADBMS", "Cyber Law", "VCS", "Ethical Hacking"
        });
        subjectBox.setBackground(Color.white);
        subjectBox.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel dateLabel = new JLabel("Select Date:");
        dateLabel.setForeground(Color.decode("#112d4e"));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd-MM-yyyy"));
        dateSpinner.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton proceedBtn = new JButton("Proceed");
        proceedBtn.setBackground(Color.decode("#3f72af"));
        proceedBtn.setForeground(Color.white);
        proceedBtn.setFont(new Font("Arial", Font.BOLD, 14));
        proceedBtn.setFocusPainted(false);
        proceedBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        facultyPanel.add(subjectLabel);
        facultyPanel.add(subjectBox);
        facultyPanel.add(dateLabel);
        facultyPanel.add(dateSpinner);
        facultyPanel.add(proceedBtn);

        proceedBtn.addActionListener(e -> {
            String subject = (String) subjectBox.getSelectedItem();
            Date date = (Date) dateSpinner.getValue();
            String dateStr = new SimpleDateFormat("dd-MM-yyyy").format(date);

            if (studentPanel != null) {
                tabbedPane.remove(studentPanel);
            }

            studentPanel = new StudentManagementPanel(subject, dateStr); // updated constructor with reference
            tabbedPane.addTab("Student Management", studentPanel);
            tabbedPane.setSelectedComponent(studentPanel);
        });

        return facultyPanel;
    }

 
    public void showAvailableListPage(JPanel availableListPage) {
        tabbedPane.addTab("Available List", availableListPage);
        tabbedPane.setSelectedComponent(availableListPage);
    }

     public void showMainPanel() {
        tabbedPane.setSelectedIndex(0);
    }
}
