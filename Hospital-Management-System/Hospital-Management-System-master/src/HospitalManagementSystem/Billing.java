package HospitalManagementSystem;
import java.sql.*;
import java.util.Scanner;

public class Billing {
    private Connection connection;
    private Scanner scanner;

    public Billing(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void generateBill() {
        System.out.print("Enter Patient ID: ");
        int patientId = scanner.nextInt();

        // Check if the patient exists
        if (!checkPatientExists(patientId)) {
            System.out.println("Patient ID not found. Please enter a valid ID.");
            return;
        }

        System.out.println("\n--- BILLING SYSTEM ---");
        System.out.print("Enter consultation fee: ");
        double consultationFee = scanner.nextDouble();

        System.out.print("Enter medicine cost: ");
        double medicineCost = scanner.nextDouble();

        System.out.print("Enter room charges: ");
        double roomCharges = scanner.nextDouble();

        System.out.print("Enter any other charges: ");
        double otherCharges = scanner.nextDouble();

        double totalAmount = consultationFee + medicineCost + roomCharges + otherCharges;
        System.out.println("Total Bill Amount: $" + totalAmount);

        // Insert bill into the database
        try {
            String query = "INSERT INTO billing(patient_id, consultation_fee, medicine_cost, room_charges, other_charges, total_amount) VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, patientId);
            preparedStatement.setDouble(2, consultationFee);
            preparedStatement.setDouble(3, medicineCost);
            preparedStatement.setDouble(4, roomCharges);
            preparedStatement.setDouble(5, otherCharges);
            preparedStatement.setDouble(6, totalAmount);
            preparedStatement.executeUpdate();
            System.out.println("Bill generated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPatientExists(int patientId) {
        try {
            String query = "SELECT id FROM patients WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, patientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // Returns true if patient exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void viewBills() {
        System.out.println("\n--- BILL HISTORY ---");
        try {
            String query = "SELECT * FROM billing";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                System.out.println("Bill ID: " + resultSet.getInt("id") +
                        ", Patient ID: " + resultSet.getInt("patient_id") +
                        ", Total Amount: $" + resultSet.getDouble("total_amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
