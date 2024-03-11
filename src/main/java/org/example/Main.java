package org.example;

import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String url = "jdbc:sqlite:mydb.db";

    public static void main(String[] args) {
        try {
            Connection c = DriverManager.getConnection(url);
            System.out.println("CONNECTED");

            // Check if the table exists, if not create it
            createTableIfNotExists(c);

            Scanner scanner = new Scanner(System.in);
            boolean running = true;
            while (running) {
                System.out.println("Choose an operation:");
                System.out.println("1. Add Item");
                System.out.println("2. View Inventory");
                System.out.println("3. Update Item");
                System.out.println("4. Delete Item");
                System.out.println("5. Exit");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addItem(c, scanner);
                        break;
                    case 2:
                        viewInventory(c);
                        break;
                    case 3:
                        updateItem(c, scanner);
                        break;
                    case 4:
                        deleteItem(c, scanner);
                        break;
                    case 5:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            }

            c.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void createTableIfNotExists(Connection connection) throws SQLException {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS ShopManagement (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "price REAL," +
                "quantity INTEGER" +
                ");";

        Statement statement = connection.createStatement();
        statement.execute(createTableQuery);
    }

    private static void addItem(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter item name:");
        String name = scanner.nextLine();
        System.out.println("Enter item price:");
        double price = scanner.nextDouble();
        System.out.println("Enter item quantity:");
        int quantity = scanner.nextInt();

        String insertQuery = "INSERT INTO ShopManagement (name, price, quantity) VALUES (?, ?, ?);";
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
        preparedStatement.setString(1, name);
        preparedStatement.setDouble(2, price);
        preparedStatement.setInt(3, quantity);
        preparedStatement.executeUpdate();

        System.out.println("Item added successfully.");
    }

    private static void viewInventory(Connection connection) throws SQLException {
        String selectQuery = "SELECT * FROM ShopManagement;";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(selectQuery);

        System.out.println("Inventory:");
        System.out.println("ID\tName\tPrice\tQuantity");
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            double price = resultSet.getDouble("price");
            int quantity = resultSet.getInt("quantity");
            System.out.println(id + "\t" + name + "\t" + price + "\t" + quantity);
        }
    }

    private static void updateItem(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter item ID to update:");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.println("Enter new item name:");
        String name = scanner.nextLine();
        System.out.println("Enter new item price:");
        double price = scanner.nextDouble();
        System.out.println("Enter new item quantity:");
        int quantity = scanner.nextInt();

        String updateQuery = "UPDATE ShopManagement SET name=?, price=?, quantity=? WHERE id=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
        preparedStatement.setString(1, name);
        preparedStatement.setDouble(2, price);
        preparedStatement.setInt(3, quantity);
        preparedStatement.setInt(4, id);
        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Item updated successfully.");
        } else {
            System.out.println("No item found with the given ID.");
        }
    }

    private static void deleteItem(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter item ID to delete:");
        int id = scanner.nextInt();

        String deleteQuery = "DELETE FROM ShopManagement WHERE id=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
        preparedStatement.setInt(1, id);
        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Item deleted successfully.");
        } else {
            System.out.println("No item found with the given ID.");
        }
    }
}
