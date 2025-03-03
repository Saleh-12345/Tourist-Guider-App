 package MyProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class User_type extends JFrame implements ActionListener {
    public User_type() {
        setTitle("User Type Selection");
        setSize(360, 260);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create an ImagePanel with the background image
        ImagePanel panel = new ImagePanel("background.jpeg");
        panel.setLayout(new GridLayout(4, 1, 10, 10));

        JButton customerButton = createButton("Customer", new Color(50, 150, 50));
        JButton guiderButton = createButton("Guider", new Color(50, 100, 200));
        JButton bookGuiderButton = createButton("Book Your Guide", new Color(200, 50, 50));
        JButton registerHotelButton = createButton("Register Hotel", new Color(255, 215, 0));  // Yellow button

        panel.add(customerButton);
        panel.add(guiderButton);
        panel.add(bookGuiderButton);
        panel.add(registerHotelButton);

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String userType = ((JButton) e.getSource()).getText();
        if (userType.equals("Customer")) {
            new CustomerGUI().setVisible(true);
        } else if (userType.equals("Guider")) {
            new GuiderGUI().setVisible(true);
        } else if (userType.equals("Register Hotel")) {
        	new hotelGUI().setVisible(true);    
        }
        else if (userType.equals("Book Your Guide")) {
        	new  BookingGUI().setVisible(true);    
        }
        setVisible(false);
    }

    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.addActionListener(this);
        button.setFont(new Font("Arial", Font.BOLD, 10)); // Smaller font size
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new User_type().setVisible(true));
    }
}

class ImagePanel extends JPanel {
    private Image backgroundImage;

    public ImagePanel(String fileName) {
        try {
            backgroundImage = new ImageIcon(fileName).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}

class CustomerGUI extends JFrame {
    private JTextField cnicField;
    private JTextField familyIdField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField addressField;

    public CustomerGUI() {
        setTitle("Customer GUI");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel panel = new ImagePanel("background.jpeg");
        // Create the panel and set layout
       panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Set GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Add fields
        cnicField = createTextFieldWithLabel(panel, "CNIC:", "Enter CNIC", gbc);
        gbc.gridy++;
        familyIdField = createTextFieldWithLabel(panel, "Family ID:", "Enter Family ID", gbc);
        gbc.gridy++;
        firstNameField = createTextFieldWithLabel(panel, "First Name:", "Enter First Name", gbc);
        gbc.gridy++;
        lastNameField = createTextFieldWithLabel(panel, "Last Name:", "Enter Last Name", gbc);
        gbc.gridy++;
        emailField = createTextFieldWithLabel(panel, "Email:", "Enter Email", gbc);
        gbc.gridy++;
        addressField = createTextFieldWithLabel(panel, "Address:", "Enter Address", gbc);

        // Add buttons
        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(50, 150, 50));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Arial", Font.BOLD, 10));
        submitButton.addActionListener(e -> {
            String cnic = cnicField.getText();
            String familyId = familyIdField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();
            String address = addressField.getText();

            storeCustomerData(cnic, familyId, firstName, lastName, email, address);
        });

        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(30, 144, 255)); // Dodger Blue
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> {
            dispose();
            new User_type().setVisible(true);
        });

        // Add buttons to panel
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(submitButton, gbc);

        gbc.gridy++;
        panel.add(backButton, gbc);

        // Set preferred size of panel
        panel.setPreferredSize(new Dimension(400, 400)); // Adjust the size as needed

        // Add panel to frame
        add(panel);

        // Pack and center the frame
        pack();
        setLocationRelativeTo(null);
    }
  
    private void storeCustomerData(String cnic, String familyId, String firstName, String lastName, String email, String address) {
        String insertSQL = "INSERT INTO customer(customer_cnic, family_id, first_name, last_name, email, residential_address) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "mysql@123");
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, cnic);
            pstmt.setString(2, familyId);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            pstmt.setString(5, email);
            pstmt.setString(6, address);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data saved successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private JTextField createTextFieldWithLabel(JPanel panel, String labelText, String hintText, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.BLACK);
        label.setOpaque(true);
        label.setBackground(new Color(173, 216, 230)); // Light Blue background
        label.setFont(new Font("Arial", Font.PLAIN, 10));
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(label, gbc);

        JTextField textField = new JTextField(hintText);
        textField.setBackground(new Color(255, 99, 71)); // Red background
        textField.setForeground(Color.BLACK);
        textField.setFont(new Font("Arial", Font.PLAIN, 10));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(textField, gbc);

        gbc.gridy++;
        return textField;
    }
}

class GuiderGUI extends JFrame {
    private JTextField cnicField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField ageField;
    private JTextField genderField;
    private JTextField ratingsField;
    private JTextField contactNoField;
    private JTextField addressField;
    public GuiderGUI() {
        setTitle("Guider GUI");
        setSize(400, 400); // Increased frame size
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new ImagePanel("background.jpeg");
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        cnicField = createTextFieldWithLabel(panel, "CNIC:", "Enter CNIC", gbc);
        firstNameField = createTextFieldWithLabel(panel, "First Name:", "Enter First Name", gbc);
        lastNameField = createTextFieldWithLabel(panel, "Last Name:", "Enter Last Name", gbc);
        ageField = createTextFieldWithLabel(panel, "Age:", "Enter Age", gbc);
        genderField = createTextFieldWithLabel(panel, "Gender:", "Enter Gender", gbc);
        ratingsField = createTextFieldWithLabel(panel, "Ratings:", "Enter Ratings", gbc);
        contactNoField = createTextFieldWithLabel(panel, "Contact No:", "Enter Contact No", gbc);
        addressField = createTextFieldWithLabel(panel, "Address:", "Enter Address", gbc);

        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(50, 150, 50));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Arial", Font.BOLD, 12)); // Increased font size

        int buttonHeight = 30; // Set smaller button height
        int buttonWidth = 120; // Set wider button width

        submitButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight)); // Set preferred size
        submitButton.addActionListener(e -> {
            String cnic = cnicField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String age = ageField.getText();
            String gender = genderField.getText();
            String ratings = ratingsField.getText();
            String contactNo = contactNoField.getText();
            String address = addressField.getText();

            storeGuiderData(cnic, firstName, lastName, age, gender, ratings, contactNo, address);
        });

        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(50, 150, 50)); // Green color
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 12)); // Increased font size
        backButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight)); // Set preferred size
        backButton.addActionListener(e -> {
            dispose();
            new User_type().setVisible(true);
        });

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 1; // Expand horizontally
        gbc.anchor = GridBagConstraints.CENTER;

        panel.add(submitButton, gbc);

        gbc.gridx++; // Move to next column
        panel.add(backButton, gbc);

        add(panel);
    }



    private void storeGuiderData(String cnic, String firstName, String lastName, String age, String gender, String ratings, String contactNo, String address) {
        String insertSQL = "INSERT INTO guider(guider_cnic, first_name, last_name, age, gender, ratings, contact_no, residential_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "mysql@123");
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, cnic);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, age);
            pstmt.setString(5, gender);
            pstmt.setString(6, ratings);
            pstmt.setString(7, contactNo);
            pstmt.setString(8, address);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data saved successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    private JTextField createTextFieldWithLabel(JPanel panel, String labelText, String hintText, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.BLACK);
        label.setOpaque(true);
        label.setBackground(new Color(173, 216, 230)); // Light Blue background
        label.setFont(new Font("Arial", Font.PLAIN, 10));
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(label, gbc);

        JTextField textField = new JTextField(hintText);
        textField.setBackground(new Color(255, 99, 71)); // Red background
        textField.setForeground(Color.BLACK);
        textField.setFont(new Font("Arial", Font.PLAIN, 10));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(textField, gbc);

        gbc.gridy++;
        return textField;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuiderGUI().setVisible(true));
    }
}
