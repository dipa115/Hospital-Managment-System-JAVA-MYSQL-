package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "Admin@123";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found!");
            e.printStackTrace();
            return;
        }

        Scanner scanner = new Scanner(System.in);

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            login loginn = new login(connection, scanner);
             String userRole = null;
            while (userRole==null) {
                System.out.println("\n===== HOSPITAL MANAGEMENT SYSTEM LOGIN =====");
                userRole = loginn.authenticate();
            }

            if (userRole.equals("admin")) {
                System.out.println("Welcome, Admin! You have full access.");
            } else if (userRole.equals("doctor")) {
                System.out.println("Welcome, Doctor! You can manage patients.");
            } else if (userRole.equals("staff")) {
                System.out.println("Welcome, Staff! You can schedule appointments.");
            }
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);
            Billing billing = new Billing(connection, scanner);

            while (true) {
                System.out.println(" !! WELCOME TO LifeLine HMS !!");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Generate Bill");
                System.out.println("6. View Bills");
                System.out.println("7. Exit");
                System.out.print("Enter your choice: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next(); // Clear invalid input
                    continue;
                }
                int choice = scanner.nextInt();
                scanner.nextLine(); // Clear buffer

                switch (choice) {
                    case 1:
                        patient.addPatient();
                        break;
                    case 2:
                        patient.viewPatients();
                        break;
                    case 3:
                        doctor.viewDoctors();
                        break;
                    case 4:
                        bookAppointment(patient, doctor, connection, scanner);
                        break;
                    case 5:
                        billing.generateBill();
                        break;
                    case 6:
                        billing.viewBills();
                        break;
                    case 7:
                        System.out.println("THANK YOU FOR USING LIFELINE HMS!!");
                        return;
                    default:
                        System.out.println("Enter a valid choice!!!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database connection error!");
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.print("Enter Patient Id: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input! Patient ID should be a number.");
            scanner.next(); // Clear invalid input
            return;
        }
        int patientId = scanner.nextInt();
        scanner.nextLine(); // Clear buffer

        System.out.print("Enter Doctor Id: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input! Doctor ID should be a number.");
            scanner.next(); // Clear invalid input
            return;
        }
        int doctorId = scanner.nextInt();
        scanner.nextLine(); // Clear buffer

        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.nextLine();

        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery)) {
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Appointment Booked Successfully!");
                    } else {
                        System.out.println("Failed to Book Appointment!");
                    }
                } catch (SQLException e) {
                    System.out.println("Error booking appointment!");
                    e.printStackTrace();
                }
            } else {
                System.out.println("Doctor is not available on this date!");
            }
        } else {
            System.out.println("Either the doctor or patient doesn't exist!");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking doctor availability!");
            e.printStackTrace();
        }
        return false;
    }
}
