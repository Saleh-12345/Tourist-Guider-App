package MyProject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class AuthenticationGUI extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Connection con;
    public AuthenticationGUI() {
        setTitle("User Authentication");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Initialize the connection
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            String url = "jdbc:mysql://localhost:3306/project";
            String username = "root";
            String password = "mysql@123";
            con = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error connecting to the database", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Exit the program if connection fails
        }

        JPanel panel = new JPanel(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        loginButton.setBackground(new Color(50, 100, 200)); // Blue color
        loginButton.setForeground(Color.WHITE); // White text color
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(this);
        signUpButton.setBackground(new Color(200, 50, 50)); // Red color
        signUpButton.setForeground(Color.WHITE); // White text color

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(signUpButton);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Login")) {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password", "Empty Fields", JOptionPane.WARNING_MESSAGE);
            } else if (authenticateUser(username, password)) {
                // Move to UserType.java
                dispose();
                new User_type().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getActionCommand().equals("Sign Up")) {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password", "Empty Fields", JOptionPane.WARNING_MESSAGE);
            } else if (userExists(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists, please choose another one", "Username Exists", JOptionPane.WARNING_MESSAGE);
            } else {
                addUserCredentials(username, password);
                // Move to UserType.java
                dispose();
                new User_type().setVisible(true);
            }
        }
    }

    // Method to authenticate user credentials
    private boolean authenticateUser(String username, String password) {
        String sql = "SELECT * FROM authentications WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // If a row is found, authentication succeeds
            }
        } catch (SQLException ex) {
            System.err.println("Error authenticating user");
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error authenticating user", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Method to check if the username already exists
    private boolean userExists(String username) {
        String sql = "SELECT * FROM authentications WHERE username = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // If a row is found, user already exists
            }
        } catch (SQLException ex) {
            System.err.println("Error checking username existence");
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error checking username existence", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Method to add user credentials to the database
    private void addUserCredentials(String username, String password) {
        String sql = "INSERT INTO authentications (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            System.out.println("User credentials added successfully.");
        } catch (SQLException ex) {
            System.err.println("Error adding user credentials to the database");
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding user credentials to the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AuthenticationGUI().setVisible(true));
    }
}
