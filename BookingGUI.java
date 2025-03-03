 package MyProject;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.*;

public class BookingGUI extends JFrame {
    private JComboBox<String> genderComboBox;
    private JComboBox<String> ageComboBox;
    private DefaultListModel<String> guiderListModel;
    private JList<String> guiderList;
    private JTextField dateField;
    private JTextField customerIdField;
    private JTextField guiderIdField;
    private JTextField placeField;
    private JTextField advisoryFeeField;
    private JTextField tripDaysField;
    private int currentBookingId;

    public BookingGUI() {
        setTitle("Booking Details");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(createCurvedBorder(new Color(200, 200, 200), 20));

        // Top Panel for Gender and Age Selection
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        filterPanel.setBackground(new Color(200, 200, 200));
        filterPanel.setBorder(createCurvedBorder(new Color(100, 100, 100), 15));
        JLabel genderLabel = new JLabel("Select Guider Gender:");
        genderLabel.setFont(new Font("Arial", Font.BOLD, 14));
        String[] genders = {"All", "Male", "Female"};
        genderComboBox = new JComboBox<>(genders);
        filterPanel.add(genderLabel);
        filterPanel.add(genderComboBox);

        JLabel ageLabel = new JLabel("Select Guider Age Range:");
        ageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        String[] ageRanges = {"All", "Below 30", "30 to 50", "Above 50"};
        ageComboBox = new JComboBox<>(ageRanges);
        filterPanel.add(ageLabel);
        filterPanel.add(ageComboBox);

        JButton filterButton = new JButton("Filter");
        filterButton.addActionListener(e -> filterGuiders());
        filterButton.setBackground(new Color(50, 150, 50));
        filterButton.setForeground(Color.WHITE);
        filterPanel.add(filterButton);

        mainPanel.add(filterPanel, BorderLayout.NORTH);
      
        // Center Panel for Guider List
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        guiderListModel = new DefaultListModel<>();
        guiderList = new JList<>(guiderListModel);
        guiderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        guiderList.addListSelectionListener(e -> displaySelectedGuiderId());
        JScrollPane guiderScrollPane = new JScrollPane(guiderList);
        centerPanel.add(guiderScrollPane);

        // Right Panel for Booking Details
        JPanel bookingPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        bookingPanel.setBackground(new Color(220, 220, 220));
        bookingPanel.setBorder(createCurvedBorder(new Color(150, 150, 150), 15));
        Font labelFont = new Font("Arial", Font.BOLD, 12);
        Font textFieldFont = new Font("Arial", Font.PLAIN, 12);
        Color labelColor = new Color(255, 150, 0);
        Color textFieldColor = new Color(255, 255, 100);
        JLabel dateLabel = createColoredLabel("Date of Booking:", labelColor, labelFont);
        dateField = createColoredTextField("Enter Date", textFieldColor, textFieldFont);
        bookingPanel.add(dateLabel);
        bookingPanel.add(dateField);
        JLabel customerIdLabel = createColoredLabel("Customer ID:", labelColor, labelFont);
        customerIdField = createColoredTextField("Enter Customer ID", textFieldColor, textFieldFont);
        bookingPanel.add(customerIdLabel);
        bookingPanel.add(customerIdField);
        JLabel guiderIdLabel = createColoredLabel("Guider ID:", labelColor, labelFont);
        guiderIdField = createColoredTextField("Guider ID", textFieldColor, textFieldFont);
        guiderIdField.setEditable(false);
        bookingPanel.add(guiderIdLabel);
        bookingPanel.add(guiderIdField);
        JLabel placeLabel = createColoredLabel("Place to Visit:", labelColor, labelFont);
        placeField = createColoredTextField("Enter Place", textFieldColor, textFieldFont);
        bookingPanel.add(placeLabel);
        bookingPanel.add(placeField);
        JLabel advisoryFeeLabel = createColoredLabel("Advisory Fee:", labelColor, labelFont);
        advisoryFeeField = createColoredTextField("Enter Advisory Fee", textFieldColor, textFieldFont);
        bookingPanel.add(advisoryFeeLabel);
        bookingPanel.add(advisoryFeeField);
        JLabel tripDaysLabel = createColoredLabel("Trip Days:", labelColor, labelFont);
        tripDaysField = createColoredTextField("Enter Trip Days", textFieldColor, textFieldFont);
        bookingPanel.add(tripDaysLabel);
        bookingPanel.add(tripDaysField);
        centerPanel.add(bookingPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel for Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(new Color(240, 240, 240));
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> storeBookingData());
        submitButton.setBackground(new Color(200, 50, 50));
        submitButton.setForeground(Color.WHITE);
        bottomPanel.add(submitButton);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e ->   navigateToUserType());
        JButton payButton = new JButton("Pay");
        bottomPanel.add(backButton);
        payButton.addActionListener(e -> {
            if (fieldsAreFilled()) {
                if (currentBookingId > 0) {
                    openPaymentGUI(currentBookingId);
                } else {
                    JOptionPane.showMessageDialog(this, "Please store the booking data first", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all fields before proceeding to payment", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
            }
        });
        payButton.setBackground(new Color(200, 50, 50));
        payButton.setForeground(Color.WHITE);
        bottomPanel.add(payButton);
        JButton makeChangesButton = new JButton("Make Changes");
        makeChangesButton.addActionListener(e -> makeChanges());
        makeChangesButton.setBackground(new Color(200, 50, 50));
        makeChangesButton.setForeground(Color.WHITE);
        bottomPanel.add(makeChangesButton);
        JButton searchBookingsButton = new JButton("Search Your Bookings");
        searchBookingsButton.addActionListener(e -> searchBookings());
        searchBookingsButton.setBackground(new Color(50, 100, 200));
        searchBookingsButton.setForeground(Color.WHITE);
        
        bottomPanel.add(searchBookingsButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
     
        add(mainPanel);
    }

    private JLabel createColoredLabel(String text, Color color, Font font) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(color);
        label.setFont(font        );
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Adding padding to the label
        return label;
    }

    private JTextField createColoredTextField(String hint, Color color, Font font) {
        JTextField textField = new JTextField(hint);
        textField.setOpaque(true);
        textField.setBackground(color);
        textField.setFont(font);
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
        textField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Adding padding to the text field
        return textField;
    }

    private Border createCurvedBorder(Color color, int thickness) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, thickness),
                BorderFactory.createEmptyBorder(thickness, thickness, thickness, thickness)
        );
    }

    private void filterGuiders() {
        guiderListModel.clear();
        String selectedGender = genderComboBox.getSelectedItem().toString();
        String selectedAgeRange = ageComboBox.getSelectedItem().toString();
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "mysql@123")) {
            String sql;
            if (selectedGender.equals("All")) {
                sql = "SELECT * FROM Guider";
            } else {
                sql = "SELECT * FROM Guider WHERE gender = ?";
            }
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                if (!selectedGender.equals("All")) {
                    pstmt.setString(1, selectedGender);
                }
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int age = rs.getInt("age");
                        if (selectedAgeRange.equals("All") ||
                            (selectedAgeRange.equals("Below 30") && age < 30) ||
                            (selectedAgeRange.equals("30 to 50") && age >= 30 && age <= 50) ||
                            (selectedAgeRange.equals("Above 50") && age > 50)) {
                            String guiderInfo = rs.getInt("Guider_ID") + " - " + rs.getString("first_name") + " " + rs.getString("last_name") +
                                    " (Age: " + age + ", Address: " + rs.getString("residential_address") + ")";
                            guiderListModel.addElement(guiderInfo);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error filtering guiders", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void searchBookings() {
        int customerId = Integer.parseInt(customerIdField.getText());
        guiderListModel.clear();
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "mysql@123")) {
            String sql = "SELECT * FROM booking WHERE customer_id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setInt(1, customerId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String bookingInfo = "Booking ID: " + rs.getInt("booking_id") + ", Guider ID: " + rs.getInt("guider_id") +
                            ", Place: " + rs.getString("place_to_visit") + ", Advisory Fee: " + rs.getDouble("advisory_fee") +
                            ", Trip Days: " + rs.getInt("trip_days");
                    guiderListModel.addElement(bookingInfo);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching bookings", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void displaySelectedGuiderId() {
        if (!guiderList.isSelectionEmpty()) {
            String selectedGuiderInfo = guiderList.getSelectedValue();
            String[] guiderParts = selectedGuiderInfo.split(" - ");
            int guiderId = Integer.parseInt(guiderParts[0]);
            guiderIdField.setText(String.valueOf(guiderId));
            guiderIdField.setCaretPosition(0); // Move the caret to the beginning of the text
            guiderIdField.setToolTipText(null); // Remove the hint
        }
    }

    private void navigateToUserType() {
        // Code to navigate to User_type screen
        // Assuming User_type class exists and has a method to show the screen
        new User_type().setVisible(true); // Show the User_type screen
        dispose(); // Close the current window
    }

    

    private void storeBookingData() {
        String date = dateField.getText();
        int customerId = Integer.parseInt(customerIdField.getText());
        int guiderId = Integer.parseInt(guiderIdField.getText());
        String place = placeField.getText();
        double advisoryFee = Double.parseDouble(advisoryFeeField.getText());
        int tripDays = Integer.parseInt(tripDaysField.getText());

        // Check if the booking already exists for the same customer
        if (bookingAlreadyExists(customerId, place, date)) {
            JOptionPane.showMessageDialog(this, "Booking already exists for the same customer", "Booking Exists", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO booking (date_of_booking, customer_id, guider_id, place_to_visit, advisory_fee, trip_days) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "mysql@123");
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, date);
            pstmt.setInt(2, customerId);
            pstmt.setInt(3, guiderId);
            pstmt.setString(4, place);
            pstmt.setDouble(5, advisoryFee);
            pstmt.setInt(6, tripDays);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        currentBookingId = generatedKeys.getInt(1);
                        JOptionPane.showMessageDialog(this, "Booking data stored successfully with ID: " + currentBookingId, "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to store booking data", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error storing booking data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean bookingAlreadyExists(int customerId, String place, String date) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "mysql@123")) {
            String sql = "SELECT * FROM booking WHERE customer_id = ? AND place_to_visit = ? AND date_of_booking = ?";
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setInt(1, customerId);
                pstmt.setString(2, place);
                pstmt.setString(3, date);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next(); // Returns true if a booking already exists for the same customer, place, and date
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error checking existing booking", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }


            private void openPaymentGUI(int bookingId) {
                BookingPaymentGUI paymentGUI = new BookingPaymentGUI(bookingId);
                paymentGUI.setVisible(true);
            }

            private boolean fieldsAreFilled() {
                return !dateField.getText().isEmpty() &&
                        !customerIdField.getText().isEmpty() &&
                        !guiderIdField.getText().isEmpty() &&
                        !placeField.getText().isEmpty() &&
                        !advisoryFeeField.getText().isEmpty() &&
                        !tripDaysField.getText().isEmpty();
            }
            private void makeChanges() {
                if (currentBookingId > 0) {
                    // Get the updated values from the text fields
                    String place = placeField.getText();
                    double advisoryFee = Double.parseDouble(advisoryFeeField.getText());
                    int tripDays = Integer.parseInt(tripDaysField.getText());

                    // Update the corresponding row in the database
                    String sql = "UPDATE booking SET place_to_visit = ?, advisory_fee = ?, trip_days = ? WHERE booking_id = ?";
                    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "mysql@123");
                         PreparedStatement pstmt = con.prepareStatement(sql)) {
                        pstmt.setString(1, place);
                        pstmt.setDouble(2, advisoryFee);
                        pstmt.setInt(3, tripDays);
                        pstmt.setInt(4, currentBookingId);

                        int rowsAffected = pstmt.executeUpdate();
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Booking data updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to update booking data", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error updating booking data", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No booking selected to make changes", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
                }
            }

            public static void main(String[] args) {
                SwingUtilities.invokeLater(() -> new BookingGUI().setVisible(true));
            }
        }
