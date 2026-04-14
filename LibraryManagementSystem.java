import java.sql.*;
import java.util.Scanner;

public class LibraryManagementSystem {

    static final String URL = "jdbc:mysql://mysql-container:3306/librarydb";
    static final String USER = "libraryuser";
    static final String PASSWORD = "librarypass";

    static Scanner sc = new Scanner(System.in);
    static Connection con;

    public static void main(String[] args) {

        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to MySQL successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Book");
            System.out.println("2. View Books");
            System.out.println("3. Delete Book");
            System.out.println("4. Update Book Status");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    viewBooks();
                    break;
                case 3:
                    deleteBook();
                    break;
                case 4:
                    updateBookStatus();
                    break;
                case 5:
                    exitApp();
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ADD BOOK
    static void addBook() {

        String sql = "INSERT INTO books (id, name, author, status) VALUES (?, ?, ?, 'AVAILABLE')";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Enter Book ID: ");
            int id = Integer.parseInt(sc.nextLine());

            System.out.print("Enter Book Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Author: ");
            String author = sc.nextLine();

            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, author);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("Book Added Successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // VIEW BOOKS
    static void viewBooks() {

        String sql = "SELECT * FROM books";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.printf("\n%-5s %-20s %-20s %-10s\n", "ID", "Name", "Author", "Status");

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.printf("%-5d %-20s %-20s %-10s\n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("author"),
                        rs.getString("status"));
            }

            if (!found)
                System.out.println("No books found.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE BOOK
    static void deleteBook() {

        String sql = "DELETE FROM books WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Enter Book ID to delete: ");
            int id = Integer.parseInt(sc.nextLine());

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("Book Deleted Successfully!");
            else
                System.out.println("Book not found.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // UPDATE STATUS
    static void updateBookStatus() {

        String sql = "UPDATE books SET status = ? WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Enter Book ID: ");
            int id = Integer.parseInt(sc.nextLine());

            System.out.print("Enter New Status (AVAILABLE / ISSUED): ");
            String status = sc.nextLine().toUpperCase();

            ps.setString(1, status);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("Book Status Updated!");
            else
                System.out.println("Book not found.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // EXIT APP
    static void exitApp() {
        try {
            if (con != null) con.close();
            sc.close();
            System.out.println("Connection closed. Exiting...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}