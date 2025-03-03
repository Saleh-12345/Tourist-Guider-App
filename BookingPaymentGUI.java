package MyProject;

 import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BookingPaymentGUI extends JFrame {
    private int bookingId;
    private JTextField customerIdField;
    private JTextField guiderIdField;
    private JTextField amountField;
    private JTextField currencyCodeField;

    public BookingPaymentGUI(int bookingId) {
        this.bookingId = bookingId;
        setTitle("Make Payment");
        setSize(500, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel mainPanel = new ImagePanel("background.jpeg"); // Set background image
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Add Customer ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel customerIdLabel = new JLabel("Customer Account No:");
        styleLabel(customerIdLabel);
        mainPanel.add(customerIdLabel, gbc);

        gbc.gridx = 1;
        customerIdField = new JTextField();
        styleTextField(customerIdField, Color.RED); // Set red background
        mainPanel.add(customerIdField, gbc);

        // Add Guider ID
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel guiderIdLabel = new JLabel("Guider Account No:");
        styleLabel(guiderIdLabel);
        mainPanel.add(guiderIdLabel, gbc);

        gbc.gridx = 1;
        guiderIdField = new JTextField();
        styleTextField(guiderIdField, Color.RED); // Set red background
        mainPanel.add(guiderIdField, gbc);

        // Add Amount
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel amountLabel = new JLabel("Amount:");
        styleLabel(amountLabel);
        mainPanel.add(amountLabel, gbc);

        gbc.gridx = 1;
        amountField = new JTextField();
        styleTextField(amountField, Color.RED); // Set red background
        mainPanel.add(amountField, gbc);

        // Add Currency Code
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel currencyCodeLabel = new JLabel("Currency Code:");
        styleLabel(currencyCodeLabel);
        mainPanel.add(currencyCodeLabel, gbc);

        gbc.gridx = 1;
        currencyCodeField = new JTextField();
        styleTextField(currencyCodeField, Color.RED); // Set red background
        mainPanel.add(currencyCodeField, gbc);

        // Add Buttons
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10)); // Modified to add an extra button
        JButton payButton = new JButton("Pay");
        styleButton(payButton, Color.GREEN);
        payButton.addActionListener(e -> makePayment());
        buttonPanel.add(payButton);

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, Color.RED);
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        
        JButton backButton = new JButton("Back"); // New button
        styleButton(backButton, Color.YELLOW); // Yellow background
        backButton.addActionListener(e -> goBackToBookingGUI()); // ActionListener to go back to BookingGUI
        buttonPanel.add(backButton); // Add back button
        
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void styleLabel(JLabel label) {
        label.setOpaque(true);
        label.setBackground(new Color(173, 216, 230)); // Light Blue background
        label.setForeground(Color.BLACK); // Change label text color to black
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void styleTextField(JTextField textField, Color background) {
        textField.setBackground(background);
        textField.setForeground(Color.BLACK);
        textField.setPreferredSize(new Dimension(200, 30));
    }

    private void styleButton(JButton button, Color background) {
        button.setBackground(background);
        button.setForeground(Color.WHITE);
    }

    private void makePayment() {

        String customerIdText = customerIdField.getText();
        String guiderIdText = guiderIdField.getText();
        String amountText = amountField.getText();
        String currencyCode = currencyCodeField.getText();

        if (customerIdText.isEmpty() || guiderIdText.isEmpty() || amountText.isEmpty() || currencyCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int customerId = Integer.parseInt(customerIdText);
            int guiderId = Integer.parseInt(guiderIdText);
            double amount = Double.parseDouble(amountText);

            String sql = "INSERT INTO booking_transactions (booking_id, customer_account, guider_account, amount, currency_code) VALUES (?, ?, ?, ?, ?)";
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "mysql@123");
                 PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setInt(1, bookingId);
                pstmt.setInt(2, customerId);
                pstmt.setInt(3, guiderId);
                pstmt.setDouble(4, amount);
                pstmt.setString(5, currencyCode);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Payment of " + amount + " " + currencyCode + " successfully processed for booking ID " + bookingId, "Payment Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to process payment", "Payment Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format", "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error processing payment", "Payment Error", JOptionPane.ERROR_MESSAGE);
        }
    
    }
    
    private void goBackToBookingGUI() {
        // Logic to go back to BookingGUI
        new BookingGUI().setVisible(true); // Assuming BookingGUI exists and has setVisible method
        dispose(); // Close the current window
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookingPaymentGUI(123).setVisible(true));
    }
}
