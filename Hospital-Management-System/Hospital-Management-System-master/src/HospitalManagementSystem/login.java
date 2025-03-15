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

    public boolean authenticate() {
        System.out.print("Enter Username: ");
        String username = scanner.next();
        System.out.print("Enter Password: ");
        String password = scanner.next();

        String query = "SELECT role FROM users WHERE username = ? AND password = ?";
        
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password); // In real applications, use password hashing

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String role = resultSet.getString("role");
                System.out.println("Login Successful! Role: " + role);
                return true;
            } else {
                System.out.println("Invalid Credentials. Please try again!");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
