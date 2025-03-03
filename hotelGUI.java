package MyProject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class hotelGUI extends JFrame {
    private JTextField hotelNameField;
    private JTextField facilitiesField;
    private JTextField addressField;
    private JTextField contactNoField;

    public hotelGUI() {
    	setTitle("Hotel Registration");
        setSize(400, 400); // Increased height to accommodate the back button
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        ImagePanel mainPanel = new ImagePanel("background.jpeg");
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add spacing between components and frame borders

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel hotelNameLabel = createColoredLabel("Hotel Name:", new Color(135, 206, 235)); // Sky blue color
        mainPanel.add(hotelNameLabel, gbc);

        gbc.gridx = 1;
        hotelNameField = createColoredTextField("Enter Hotel Name", Color.RED); // Changed field color to red
        mainPanel.add(hotelNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel facilitiesLabel = createColoredLabel("Facilities:", new Color(135, 206, 235)); // Sky blue color
        mainPanel.add(facilitiesLabel, gbc);

        gbc.gridx = 1;
        facilitiesField = createColoredTextField("Enter Facilities", Color.RED); // Changed field color to red
        mainPanel.add(facilitiesField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel addressLabel = createColoredLabel("Address:", new Color(135, 206, 235)); // Sky blue color
        mainPanel.add(addressLabel, gbc);

        gbc.gridx = 1;
        addressField = createColoredTextField("Enter Address", Color.RED); // Changed field color to red
        mainPanel.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel contactNoLabel = createColoredLabel("Contact No:", new Color(135, 206, 235)); // Sky blue color
        mainPanel.add(contactNoLabel, gbc);

        gbc.gridx = 1;
        contactNoField = createColoredTextField("Enter Contact No", Color.RED); // Changed field color to red
        mainPanel.add(contactNoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton registerButton = new JButton("Register");
        registerButton.setBackground(Color.GREEN); // Set button background color to green
        registerButton.setForeground(Color.WHITE); // Set button text color to white
        registerButton.addActionListener(e -> registerHotel());
        mainPanel.add(registerButton, gbc);

        gbc.gridy = 5; // Added row for the back button
        JButton backButton = new JButton("Back");
        backButton.setBackground(Color.DARK_GRAY); 
        
        backButton.addActionListener(e -> navigateToUserType());
// Set button background color to blue
        backButton.setForeground(Color.WHITE); // Set button text color to white
      
        mainPanel.add(backButton, gbc);

        add(mainPanel);
    }

    private JLabel createColoredLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(color); // Set label background color
        label.setForeground(Color.WHITE); // Set label text color to white
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add padding to the label
        return label;
    }

    private JTextField createColoredTextField(String hint, Color color) {
        JTextField textField = new JTextField(hint);
        textField.setOpaque(true);
        textField.setBackground(color);
        textField.setForeground(Color.BLACK); // Set text color to black
        textField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(hint)) {
                    textField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(hint);
                }
            }
        });

        return textField;
    }
    private void navigateToUserType() {
        User_type userTypeSelectionForm = new User_type();
        userTypeSelectionForm.setVisible(true);
        dispose(); // Close the current form
    }
    private void registerHotel() {
        String hotelName = hotelNameField.getText();
        String facilities = facilitiesField.getText();
        String address = addressField.getText();
        String contactNo = contactNoField.getText();

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "mysql@123")) {
            // Check if hotel name already exists
            String checkSql = "SELECT COUNT(*) FROM hotel WHERE hotel_name = ?";
            try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
                checkStmt.setString(1, hotelName);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "Hotel name already exists. Please choose a different name.", "Warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            // Insert new hotelo
            String insertSql = "INSERT INTO hotel(hotel_name, facilities, address, contact_no) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = con.prepareStatement(insertSql)) {
                pstmt.setString(1, hotelName);
                pstmt.setString(2, facilities);
                pstmt.setString(3, address);
                pstmt.setString(4, contactNo);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Hotel registered successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to register hotel", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error registering hotel", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        hotelNameField.setText("Enter Hotel Name");
        facilitiesField.setText("Enter Facilities");
        addressField.setText("Enter Address");
        contactNoField.setText("Enter Contact No");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new hotelGUI().setVisible(true));
    }
}
