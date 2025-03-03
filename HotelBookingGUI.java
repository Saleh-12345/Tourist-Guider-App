 package MyProject;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.sql.*;

public class HotelBookingGUI extends JFrame {
    private JTextField takecity;
    private DefaultListModel<String> HotelListModel;
    private JList<String> HotelList;
    private JTextField HotelIDField;
    private JTextField CheckinDateField;
    private JTextField CheckoutDateField;
    private JTextField RoomNoField;
    private JTextField CNICField;
    private JComboBox<String> updateComboBox;

    public HotelBookingGUI() {
        setTitle("Hotel Booking");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // Left Panel for City Input and Hotel List
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);

        // Top Panel for City Input and Search Button
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.WHITE);
        JLabel cityLabel = createColoredLabel("Enter City:", Color.ORANGE);
        takecity = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(Color.BLUE);
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> searchHotels());
        topPanel.add(cityLabel);
        topPanel.add(takecity);
        topPanel.add(searchButton);
        leftPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel for Hotel List
        HotelListModel = new DefaultListModel<>();
        HotelList = new JList<>(HotelListModel);
        HotelList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!HotelList.isSelectionEmpty()) {
                    String selectedHotelInfo = HotelList.getSelectedValue();
                    String hotelId = selectedHotelInfo.split(",")[0].split(": ")[1].trim();
                    HotelIDField.setText(hotelId);
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(HotelList);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        // Right Panel for Booking Details
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Bottom Panel for Booking Details
        JPanel bottomPanel = new JPanel(new GridLayout(6, 2, 10, 10)); // Added spacing between the boxes
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(createColoredLabel("Hotel ID:", Color.ORANGE));
        HotelIDField = new JTextField();
        HotelIDField.setBackground(Color.LIGHT_GRAY); // Changed color to light gray
        HotelIDField.setForeground(Color.BLACK);
        bottomPanel.add(HotelIDField);
        bottomPanel.add(createColoredLabel("Check-in Date:", Color.ORANGE));
        CheckinDateField = new JTextField();
        CheckinDateField.setBackground(Color.LIGHT_GRAY); // Changed color to light gray
        CheckinDateField.setForeground(Color.BLACK);
        bottomPanel.add(CheckinDateField);
        bottomPanel.add(createColoredLabel("Check-out Date:", Color.ORANGE));
        CheckoutDateField = new JTextField();
        CheckoutDateField.setBackground(Color.LIGHT_GRAY); // Changed color to light gray
        CheckoutDateField.setForeground(Color.BLACK);
        bottomPanel.add(CheckoutDateField);
        bottomPanel.add(createColoredLabel("Room No:", Color.ORANGE));
        RoomNoField = new JTextField();
        RoomNoField.setBackground(Color.LIGHT_GRAY); // Changed color to light gray
        RoomNoField.setForeground(Color.BLACK);
        bottomPanel.add(RoomNoField);
        bottomPanel.add(createColoredLabel("CNIC:", Color.ORANGE));
        CNICField = new JTextField();
        CNICField.setBackground(Color.LIGHT_GRAY); // Changed color to light gray
        CNICField.setForeground(Color.BLACK);
        bottomPanel.add(CNICField);
        bottomPanel.add(createColoredLabel("Update:", Color.ORANGE));
        updateComboBox = new JComboBox<>(new String[]{"Check-in Date", "Check-out Date", "Room No"});
        bottomPanel.add(updateComboBox);
        rightPanel.add(bottomPanel, BorderLayout.CENTER);

        // Panel for Confirm, Update, and Delete Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        JButton confirmBookingButton = new JButton("Confirm Booking");
        confirmBookingButton.setBackground(Color.GREEN);
        confirmBookingButton.setForeground(Color.WHITE);
        confirmBookingButton.addActionListener(e -> confirmBooking());
        JButton updateBookingButton = new JButton("Update Booking");
        updateBookingButton.setBackground(Color.ORANGE);
        updateBookingButton.setForeground(Color.WHITE);
        updateBookingButton.addActionListener(e -> updateBooking());
        JButton deleteBookingButton = new JButton("Delete Booking");
        deleteBookingButton.setBackground(Color.RED);
        deleteBookingButton.setForeground(Color.WHITE);
        deleteBookingButton.addActionListener(e -> deleteBooking());
        buttonPanel.add(confirmBookingButton);
        buttonPanel.add(updateBookingButton);
        buttonPanel.add(deleteBookingButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel);
    }

    private JLabel createColoredLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(color);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return label;
    }

    private void searchHotels() {
        String city = takecity.getText();
        if (!city.isEmpty()) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "mysql@123");
                String sql = "SELECT hotel_id, hotel_name, facilities, address, contact_no FROM Hotel WHERE address = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, city);
                ResultSet rs = pstmt.executeQuery();

                // Clear previous hotel IDs from the list model
                HotelListModel.clear();

                while (rs.next()) {
                    String hotelId = rs.getString("hotel_id");
                    String hotelName = rs.getString("hotel_name");
                    String facilities = rs.getString("facilities");
                    String address = rs.getString("address");
                    String contactNo = rs.getString("contact_no");

                    // Concatenate the hotel attributes into a single string
                    String hotelDetails = "Hotel ID: " + hotelId + ", Name: " + hotelName + ", Facilities: " + facilities + ", Address: " + address + ", Contact No: " + contactNo;

                    // Store the hotel details in the list model
                    HotelListModel.addElement(hotelDetails);
                }

                rs.close();
                pstmt.close();
                conn.close();

                if (HotelListModel.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No hotels available for the provided city.", "No Hotels Found", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error retrieving hotels: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a city to search for hotels.", "Missing City", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void confirmBooking() {
        String hotelID = HotelIDField.getText();
        String checkinDate = CheckinDateField.getText();
        String checkoutDate = CheckoutDateField.getText();
        String roomNo = RoomNoField.getText();
        String cnic = CNICField.getText();
        String bookingDate = new java.sql.Date(System.currentTimeMillis()).toString(); // Current date as booking date

        if (!hotelID.isEmpty() && !checkinDate.isEmpty()&& !checkoutDate.isEmpty() && !roomNo.isEmpty() && !cnic.isEmpty()) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "mysql@123");
                String sql = "INSERT INTO hotel_booking (hotel_id, booking_date, room_no, checkin_date, checkout_date, cnic) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, hotelID);
                pstmt.setString(2, bookingDate);
                pstmt.setString(3, roomNo);
                pstmt.setString(4, checkinDate);
                pstmt.setString(5, checkoutDate);
                pstmt.setString(6, cnic);
                pstmt.executeUpdate();
                pstmt.close();
                conn.close();

                JOptionPane.showMessageDialog(this, "Booking confirmed!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error confirming booking: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please fill in all fields to confirm booking.", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateBooking() {
        String hotelID = HotelIDField.getText();
        String checkinDate = CheckinDateField.getText();
        String checkoutDate = CheckoutDateField.getText();
        String roomNo = RoomNoField.getText();
        String cnic = CNICField.getText();
        String selectedUpdate = (String) updateComboBox.getSelectedItem();

        if (!cnic.isEmpty() && !hotelID.isEmpty()) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "mysql@123");
                String sql = null;
                if ("Check-in Date".equals(selectedUpdate)) {
                    sql = "UPDATE  hotel_booking  SET checkin_date = ? WHERE cnic = ? AND hotel_id = ?";
                } else if ("Check-out Date".equals(selectedUpdate)) {
                    sql = "UPDATE  hotel_booking  SET checkout_date = ? WHERE cnic = ? AND hotel_id = ?";
                } else if ("Room No".equals(selectedUpdate)) {
                    sql = "UPDATE  hotel_booking  SET room_no = ? WHERE cnic = ? AND hotel_id = ?";
                }
                PreparedStatement pstmt = conn.prepareStatement(sql);
                if ("Check-in Date".equals(selectedUpdate)) {
                    pstmt.setString(1, checkinDate);
                } else if ("Check-out Date".equals(selectedUpdate)) {
                    pstmt.setString(1, checkoutDate);
                } else if ("Room No".equals(selectedUpdate)) {
                    pstmt.setString(1, roomNo);
                }
                pstmt.setString(2, cnic);
                pstmt.setString(3, hotelID);
                int rowsAffected = pstmt.executeUpdate();
                pstmt.close();
                conn.close();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Booking updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No booking found with the provided CNIC and Hotel ID.", "No Booking Found", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating booking: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a CNIC and Hotel ID to update booking.", "Missing Information", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteBooking() {
        String cnic = CNICField.getText();

        if (!cnic.isEmpty()) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "mysql@123");
                String sql = "DELETE FROM  hotel_booking  WHERE cnic = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, cnic);
                int rowsAffected = pstmt.executeUpdate();
                pstmt.close();
                conn.close();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Booking deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No booking found with the provided CNIC.", "No Booking Found", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting booking: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a CNIC to delete booking.", "Missing CNIC", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelBookingGUI().setVisible(true));
    }
}
