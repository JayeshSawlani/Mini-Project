import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.MessageFormat;
import java.util.Vector;

@SuppressWarnings("serial")
public class StudentManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private String subject;
    private String selectedDate;

    public StudentManagementPanel(String subject, String selectedDate) {
        this.subject = subject;
        this.selectedDate = selectedDate;

        setLayout(new BorderLayout());
        setBackground(new Color(220, 240, 255));
        setPreferredSize(new Dimension(800, 600)); // 💡 Ensure panel is large enough

        // 🔹 Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(153, 221, 255));
        headerPanel.setBorder(BorderFactory.createLineBorder(new Color(120, 190, 230)));

        JLabel subjectLabel = new JLabel("Subject: " + subject);
        JLabel dateLabel = new JLabel("Date: " + selectedDate);
        subjectLabel.setFont(new Font("Arial", Font.BOLD, 18));
        dateLabel.setFont(new Font("Arial", Font.BOLD, 18));
        subjectLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        dateLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        headerPanel.add(subjectLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 🔸 Table
        model = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setPreferredScrollableViewportSize(new Dimension(750, 300)); // ✅ Ensures scroll pane fits

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Present", "Absent"});
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(statusBox));

        loadStudents(); // Load data
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 🔸 Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        btnPanel.setBackground(new Color(220, 240, 255));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // btnPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Debug border (optional)

        JButton addBtn = createStyledButton("Add", new Color(102, 204, 102));
        JButton updateBtn = createStyledButton("Update", new Color(51, 153, 255));
        JButton deleteBtn = createStyledButton("Delete", new Color(255, 102, 102));
        JButton listBtn = createStyledButton("Available List", new Color(255, 204, 102));
        JButton printBtn = createStyledButton("Print", new Color(255, 153, 51));
        JButton submitBtn = createStyledButton("Submit", new Color(153, 153, 255));

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(listBtn);
        btnPanel.add(printBtn);
        btnPanel.add(submitBtn);

        add(btnPanel, BorderLayout.SOUTH); // ✅ Ensure SOUTH is visible with preferred size

        // 🔹 Actions
        addBtn.addActionListener(e -> openEditDialog(null));
        updateBtn.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected != -1) {
                openEditDialog(model.getDataVector().elementAt(selected));
            }
        });

        deleteBtn.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected != -1) {
                String id = model.getValueAt(selected, 0).toString();
                try (Connection conn = DBConnection.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM students WHERE student_id = ? AND subject = ? AND attendance_date = TO_DATE(?, 'MM/DD/YYYY')");
                    ps.setString(1, id);
                    ps.setString(2, subject);
                    ps.setString(3, selectedDate);
                    ps.executeUpdate();
                    model.removeRow(selected);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        printBtn.addActionListener(e -> {
            try {
                MessageFormat header = new MessageFormat("Attendance Report - " + subject + " (" + selectedDate + ")");
                MessageFormat footer = new MessageFormat("Page {0}");
                table.print(JTable.PrintMode.FIT_WIDTH, header, footer);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to print: " + ex.getMessage());
            }
        });

        listBtn.addActionListener(e -> loadAvailableStudents());
        submitBtn.addActionListener(e -> mailAllStudents());
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(120, 35));
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        return button;
    }

    private void loadStudents() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT student_id, student_name, email, status FROM students WHERE subject = ? AND attendance_date = TO_DATE(?, 'MM/DD/YYYY')")) {
            ps.setString(1, subject);
            ps.setString(2, selectedDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getString("email"),
                        rs.getString("status")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadAvailableStudents() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT student_id, student_name, email FROM available_students")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getString("email"),
                        ""
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void openEditDialog(Object rowData) {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Present", "Absent"});

        if (rowData != null) {
            Vector<?> row = (Vector<?>) rowData;
            idField.setText(row.get(0).toString());
            nameField.setText(row.get(1).toString());
            emailField.setText(row.get(2).toString());
            statusBox.setSelectedItem(row.get(3).toString());
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("ID:")); panel.add(idField);
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Email:")); panel.add(emailField);
        panel.add(new JLabel("Status:")); panel.add(statusBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Student Details", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                if (rowData == null) {
                    PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO students(student_id, student_name, email, status, subject, attendance_date)"
                        + " VALUES(?,?,?,?,?,TO_DATE(?, 'MM/DD/YYYY'))");
                    ps.setString(1, idField.getText());
                    ps.setString(2, nameField.getText());
                    ps.setString(3, emailField.getText());
                    ps.setString(4, statusBox.getSelectedItem().toString());
                    ps.setString(5, subject);
                    ps.setString(6, selectedDate);
                    ps.executeUpdate();
                    model.addRow(new Object[]{idField.getText(), nameField.getText(), emailField.getText(), statusBox.getSelectedItem().toString()});
                } else {
                    PreparedStatement ps = conn.prepareStatement(
                        "UPDATE students SET student_name=?, email=?, status=?"
                        + " WHERE student_id=? AND subject=? AND attendance_date = TO_DATE(?, 'MM/DD/YYYY')");
                    ps.setString(1, nameField.getText());
                    ps.setString(2, emailField.getText());
                    ps.setString(3, statusBox.getSelectedItem().toString());
                    ps.setString(4, idField.getText());
                    ps.setString(5, subject);
                    ps.setString(6, selectedDate);
                    ps.executeUpdate();
                    int selected = table.getSelectedRow();
                    model.setValueAt(nameField.getText(), selected, 1);
                    model.setValueAt(emailField.getText(), selected, 2);
                    model.setValueAt(statusBox.getSelectedItem().toString(), selected, 3);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void mailAllStudents() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No students to mail.");
            return;
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            String name = model.getValueAt(i, 1).toString();
            String email = model.getValueAt(i, 2).toString();
            String status = model.getValueAt(i, 3).toString();

            String subjectLine = "Attendance for " + subject + " on " + selectedDate;
            String messageBody = "Hello " + name + ",\n\nYour attendance status for " + subject
                + " on " + selectedDate + " is: " + status + ".\n\nRegards,\nClass Attendance System,\nPujita & Jayesh";

            MailSender.sendEmail(email, subjectLine, messageBody);
        }

        JOptionPane.showMessageDialog(this, "Attendance emails sent.");
    }
}
