package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class login {
    private Connection connection;
    private Scanner scanner;

    public login(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public String authenticate() {
        System.out.print("Enter Username: ");
        String username = scanner.next();
    
        System.out.print("Enter Password: ");
        String password = scanner.next();
    
        // Convert both input and stored usernames to lowercase for case-insensitive comparison
        String query = "SELECT role FROM users WHERE LOWER(username) = LOWER(?) AND BINARY password = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password); // Since passwords are stored in plaintext
    
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("role"); // Return user role
                } else {
                    System.out.println(" Invalid Credentials! Try Again.");
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println(" Database Error During Login!");
            e.printStackTrace();
        }
        return null;
    }
}    
