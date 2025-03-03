package MyProject;

 import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class HotelBooking extends JFrame {
    private JTextField bookingDateField;
    private JTextField roomNoField;
    private JTextField checkinDateField;
    private JTextField checkoutDateField;
    private JTextField cnicField;
    private JTextField cityField;
    private JTextField hotelIdField;

    public HotelBooking() {
        setTitle("Hotel Booking");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add spacing between components and frame borders

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel cityLabel = createColoredLabel("City:", Color.RED);
        mainPanel.add(cityLabel, gbc);

        gbc.gridx = 1;
        cityField = createColoredTextField("Enter City", Color.BLUE);
        mainPanel.add(cityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel bookingDateLabel = createColoredLabel("Booking Date:", Color.RED);
        mainPanel.add(bookingDateLabel, gbc);

        gbc.gridx = 1;
        bookingDateField = createColoredTextField("Enter Booking Date", Color.BLUE);
        mainPanel.add(bookingDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel roomNoLabel = createColoredLabel("Room No:", Color.RED);
        mainPanel.add(roomNoLabel, gbc);

        gbc.gridx = 1;
        roomNoField = createColoredTextField("Enter Room No", Color.BLUE);
        mainPanel.add(roomNoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel checkinDateLabel = createColoredLabel("Check-in Date:", Color.RED);
        mainPanel.add(checkinDateLabel, gbc);

        gbc.gridx = 1;
        checkinDateField = createColoredTextField("Enter Check-in Date", Color.BLUE);
        mainPanel.add(checkinDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel checkoutDateLabel = createColoredLabel("Check-out Date:", Color.RED);
        mainPanel.add(checkoutDateLabel, gbc);

        gbc.gridx = 1;
        checkoutDateField = createColoredTextField("Enter Check-out Date", Color.BLUE);
        mainPanel.add(checkoutDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel cnicLabel = createColoredLabel("CNIC:", Color.RED);
        mainPanel.add(cnicLabel, gbc);

        gbc.gridx = 1;
        cnicField = createColoredTextField("Enter CNIC", Color.BLUE);
        mainPanel.add(cnicField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel hotelIdLabel = createColoredLabel("Hotel ID:", Color.RED);
        mainPanel.add(hotelIdLabel, gbc);

        gbc.gridx = 1;
        hotelIdField = createColoredTextField("Hotel ID", Color.BLUE);
        mainPanel.add(hotelIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        JButton searchButton = new JButton("Search Hotels");
        searchButton.setBackground(Color.YELLOW); // Set button background color to yellow
        searchButton.setForeground(Color.BLACK); // Set button text color to black
        searchButton.addActionListener(e -> searchHotels());
        mainPanel.add(searchButton, gbc);

        gbc.gridy = 8;
        JButton confirmButton = new JButton("Confirm Booking");
        confirmButton.setBackground(Color.GREEN); // Set button background color to green
        confirmButton.setForeground(Color.WHITE); // Set button text color to white
        confirmButton.addActionListener(e -> confirmBooking());
        mainPanel.add(confirmButton, gbc);
        gbc.gridy = 9; // Increment the gridy value to add the button below the confirmButton
        JButton updateButton = new JButton("Update Booking");
        updateButton.setBackground(Color.BLUE); // Set button background color to blue
        updateButton.setForeground(Color.WHITE); // Set button text color to white
        updateButton.addActionListener(e -> updateBooking());
        mainPanel.add(updateButton, gbc);
        gbc.gridy = 10; 
     // Add this line inside the constructor to create a button for viewing bookings
        JButton viewBookingsButton = new JButton("View My Bookings");
        viewBookingsButton.setBackground(Color.CYAN); // Set button background color to cyan
        viewBookingsButton.setForeground(Color.BLACK); // Set button text color to black
        viewBookingsButton.addActionListener(e -> showBookingsByCNIC(cnicField.getText())); // Call showBookingsByCNIC with the entered CNIC
        mainPanel.add(viewBookingsButton, gbc);


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
        textField.setForeground(Color.WHITE);
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
    private void showBookingsByCNIC(String cnic) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "mysql@123")) {
            String query = "SELECT * FROM hotel_booking WHERE cnic = ?";
            try (PreparedStatement pstmt = con.prepareStatement(query)) {
                pstmt.setString(1, cnic);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.isBeforeFirst()) {
                        JOptionPane.showMessageDialog(this, "No bookings found for the specified CNIC.", "Information", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    StringBuilder bookingDetails = new StringBuilder();
                    bookingDetails.append("Your bookings:\n");

                    while (rs.next()) {
                        bookingDetails.append("Booking Date: ").append(rs.getString("booking_date")).append("\n");
                        bookingDetails.append("Room No: ").append(rs.getInt("room_no")).append("\n");
                        bookingDetails.append("Check-in Date: ").append(rs.getString("checkin_date")).append("\n");
                        bookingDetails.append("Check-out Date: ").append(rs.getString("checkout_date")).append("\n");
                        bookingDetails.append("Hotel ID: ").append(rs.getInt("hotel_id")).append("\n");
                        bookingDetails.append("\n");
                    }

                    JOptionPane.showMessageDialog(this, bookingDetails.toString(), "Your Bookings", JOptionPane.PLAIN_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching bookings", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void searchHotels() {
        String city = cityField.getText();
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "mysql@123")) {
            String query = "SELECT * FROM hotel WHERE city = ?";
            try (PreparedStatement pstmt = con.prepareStatement(query)) {
                pstmt.setString(1, city);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.isBeforeFirst()) {
                        JOptionPane.showMessageDialog(this, "No hotels found in the specified city.", "Information", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    String[] columnNames = {"Hotel ID", "Hotel Name", "Facilities", "Address", "Contact No"};
                    DefaultTableModel model = new DefaultTableModel(columnNames, 0);

                    while (rs.next()) {
                        Object[] row = {
                                rs.getInt("hotel_id"),
                                rs.getString("hotel_name"),
                                rs.getString("facilities"),
                                rs.getString("address"),
                                rs.getString("contact_no")
                        };
                        model.addRow(row);
                    }

                    JTable table = new JTable(model);
                    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    table.getSelectionModel().addListSelectionListener(e -> {
                        if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                            int selectedHotelId = (int) table.getValueAt(table.getSelectedRow(), 0);
                            hotelIdField.setText(String.valueOf(selectedHotelId));
                        }
                    });

                    JScrollPane scrollPane = new JScrollPane(table);
                    scrollPane.setPreferredSize(new Dimension(500, 200));
                    JOptionPane.showMessageDialog(this, scrollPane, "Select a Hotel", JOptionPane.PLAIN_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching hotel data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmBooking() {
        String bookingDate = bookingDateField.getText();
        int roomNo = Integer.parseInt(roomNoField.getText());
        String checkinDate = checkinDateField.getText();
        String checkoutDate = checkoutDateField.getText();
        String cnic = cnicField.getText();
        int hotelId = Integer.parseInt(hotelIdField.getText());

        if (bookingDate.isEmpty() || roomNoField.getText().isEmpty() || checkinDate.isEmpty() || checkoutDate.isEmpty() || cnic.isEmpty() || hotelIdField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "mysql@123")) {
            String checkSql = "SELECT COUNT(*) FROM hotel_booking WHERE booking_date = ? AND room_no = ? AND cnic = ?";
            try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
                checkStmt.setString(1, bookingDate);
                checkStmt.setInt(2, roomNo);
                checkStmt.setString(3, cnic);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "Booking already exists for this date, room, and CNIC.", "Warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            String insertSql= "INSERT INTO hotel_booking (booking_date, room_no, checkin_date, checkout_date, cnic, hotel_id) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = con.prepareStatement(insertSql)) {
                pstmt.setString(1, bookingDate);
                pstmt.setInt(2, roomNo);
                pstmt.setString(3, checkinDate);
                pstmt.setString(4, checkoutDate);
                pstmt.setString(5, cnic);
                pstmt.setInt(6, hotelId);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Booking confirmed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to confirm booking", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error confirming booking", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        bookingDateField.setText("Enter Booking Date");
        roomNoField.setText("Enter Room No");
        checkinDateField.setText("Enter Check-in Date");
        checkoutDateField.setText("Enter Check-out Date");
        cnicField.setText("Enter CNIC");
        hotelIdField.setText("Hotel ID");
    }
    private void updateBooking() {
        String bookingDate = bookingDateField.getText();
        int roomNo = Integer.parseInt(roomNoField.getText());
        String checkinDate = checkinDateField.getText();
        String checkoutDate = checkoutDateField.getText();
        String cnic = cnicField.getText();
        int hotelId = Integer.parseInt(hotelIdField.getText());
        String city = cityField.getText();

        if (bookingDate.isEmpty() || roomNoField.getText().isEmpty() || checkinDate.isEmpty() || checkoutDate.isEmpty() || cnic.isEmpty() || hotelIdField.getText().isEmpty() || city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "mysql@123")) {
            String updateSql = "UPDATE hotel_booking SET room_no = ?, checkin_date = ?, checkout_date = ?, hotel_id = ? WHERE booking_date = ? AND cnic = ? AND city = ?";
            try (PreparedStatement pstmt = con.prepareStatement(updateSql)) {
                pstmt.setInt(1, roomNo);
                pstmt.setString(2, checkinDate);
                pstmt.setString(3, checkoutDate);
                pstmt.setInt(4, hotelId);
                pstmt.setString(5, bookingDate);
                pstmt.setString(6, cnic);
                pstmt.setString(7, city);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Booking updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "No booking found for the specified criteria.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating booking", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelBooking().setVisible(true));
    }
}

