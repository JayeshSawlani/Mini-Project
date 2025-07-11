import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

@SuppressWarnings("serial")
public class LoginSignupUI extends JFrame {

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginBtn, signupBtn;
    private JCheckBox showPassword;

    public LoginSignupUI() {
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);  

        // Create split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(0);
        splitPane.setEnabled(false);
        splitPane.setDividerLocation(450);

        // Left panel 
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.decode("#d6e4f0"));
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Load college 
        ImageIcon logoIcon = new ImageIcon("C:\\Users\\91830\\eclipse-workspace\\MiniProject2\\src\\college.png");
        Image logoImg = logoIcon.getImage().getScaledInstance(380, 120, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        leftPanel.add(logoLabel, gbc);

        JLabel lblTitle1 = new JLabel("ATTENDANCE");
        lblTitle1.setFont(new Font("Verdana", Font.BOLD, 36));
        leftPanel.add(lblTitle1, gbc);
        JLabel lblTitle2 = new JLabel("MANAGEMENT SYSTEM");
        lblTitle2.setFont(new Font("Verdana", Font.PLAIN, 20));
        leftPanel.add(lblTitle2, gbc);

        // Right panel (login form)
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.decode("#334756"));
        rightPanel.setLayout(null);

        // Custom close button since frame is undecorated
        JButton closeBtn = new JButton("X");
        closeBtn.setBounds(760, 10, 30, 30);
        closeBtn.setBorder(null);
        closeBtn.setFocusPainted(false);
        closeBtn.setForeground(Color.white);
        closeBtn.setBackground(Color.decode("#334756"));
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn);

        JLabel loginLabel = new JLabel("LOGIN");
        loginLabel.setForeground(Color.white);
        loginLabel.setFont(new Font("Serif", Font.BOLD, 32));
        loginLabel.setBounds(150, 30, 200, 40);
        rightPanel.add(loginLabel);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.white);
        nameLabel.setBounds(50, 100, 100, 25);
        rightPanel.add(nameLabel);
        nameField = new JTextField();
        nameField.setBounds(150, 100, 180, 25);
        rightPanel.add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.white);
        emailLabel.setBounds(50, 150, 100, 25);
        rightPanel.add(emailLabel);
        emailField = new JTextField();
        emailField.setBounds(150, 150, 180, 25);
        rightPanel.add(emailField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.white);
        passLabel.setBounds(50, 200, 100, 25);
        rightPanel.add(passLabel);
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 200, 180, 25);
        rightPanel.add(passwordField);

        showPassword = new JCheckBox("Show Password");
        showPassword.setForeground(Color.white);
        showPassword.setBackground(Color.decode("#334756"));
        showPassword.setBounds(150, 235, 180, 20);
        showPassword.addActionListener(e -> passwordField.setEchoChar(showPassword.isSelected() ? (char) 0 : '*'));
        rightPanel.add(showPassword);

        loginBtn = new JButton("LOGIN");
        loginBtn.setBounds(100, 300, 100, 35);
        loginBtn.setBackground(Color.decode("#f0f4ff"));
        loginBtn.setEnabled(false);
        rightPanel.add(loginBtn);

        signupBtn = new JButton("SIGN UP");
        signupBtn.setBounds(220, 300, 110, 35);
        signupBtn.setBackground(Color.decode("#90ee90"));
        signupBtn.setEnabled(false);
        rightPanel.add(signupBtn);

        KeyAdapter inputCheck = new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                boolean allFilled = !nameField.getText().isEmpty()
                        && !emailField.getText().isEmpty()
                        && passwordField.getPassword().length > 0;
                loginBtn.setEnabled(allFilled);
                signupBtn.setEnabled(allFilled);
            }
        };
        nameField.addKeyListener(inputCheck);
        emailField.addKeyListener(inputCheck);
        passwordField.addKeyListener(inputCheck);

        loginBtn.addActionListener(e -> login());
        signupBtn.addActionListener(e -> signup());

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane);

        setVisible(true);
    }

    private void login() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, emailField.getText());
            stmt.setString(2, new String(passwordField.getPassword()));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose();
                new MainDashboardUI();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void signup() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO users(name, email, password) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nameField.getText());
            stmt.setString(2, emailField.getText());
            stmt.setString(3, new String(passwordField.getPassword()));
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Signup Successful! You can now log in.");
        } catch (SQLException ex) {
            ex.getMessage();
            JOptionPane.showMessageDialog(this, "Email already exists!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginSignupUI::new);
    }
}
