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
            initializeDatabase(); 
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database. Is MySQL running?");
            e.printStackTrace();
            return;
        }

        while (true) {
            System.out.println("\n  _\\|/_ LIBRARY MANAGEMENT FUNCTIONS _\\|/_");
            System.out.println("1. Add Book");
            System.out.println("2. View All Books");
            System.out.println("3. Search for a Book");
            System.out.println("4. Borrow a Book");
            System.out.println("5. Return a Book");
            System.out.println("6. Delete Book");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");

            int choice = -1;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number");
                continue; 
            }

            switch (choice) {
                case 1: addBook(); break;
                case 2: viewBooks(); break;
                case 3: searchBook(); break;
                case 4: borrowBook(); break;
                case 5: returnBook(); break;
                case 6: deleteBook(); break;
                case 7: exitApp(); return;
                default: System.out.println("Invalid choice! Please select 1-7");
            }
        }
    }

    static void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS books (" +
                     "id INT PRIMARY KEY, " +
                     "name VARCHAR(255) NOT NULL, " +
                     "author VARCHAR(255) NOT NULL, " +
                     "status VARCHAR(50) DEFAULT 'AVAILABLE')";
        try (Statement stmt = con.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error initializing database table.");
        }
    }

    // 1. ADD BOOK
    static void addBook() {
        String sql = "INSERT INTO books (id, name, author, status) VALUES (?, ?, ?, 'AVAILABLE')";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            System.out.print("Enter Book ID: ");
            ps.setInt(1, Integer.parseInt(sc.nextLine()));
            System.out.print("Enter Book Name: ");
            ps.setString(2, sc.nextLine());
            System.out.print("Enter Author: ");
            ps.setString(3, sc.nextLine());

            if (ps.executeUpdate() > 0) System.out.println("Book Added Successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Must be a number.");
        } catch (SQLException e) {
            System.out.println("Error adding book. Does this ID already exist?");
        }
    }

    // 2. VIEW BOOKS
    static void viewBooks() {
        String sql = "SELECT * FROM books";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            printResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. SEARCH BOOK
    static void searchBook() {
        String sql = "SELECT * FROM books WHERE name LIKE ? OR author LIKE ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            System.out.print("Enter title or author keyword: ");
            String keyword = "%" + sc.nextLine() + "%";
            ps.setString(1, keyword);
            ps.setString(2, keyword);
            
            try (ResultSet rs = ps.executeQuery()) {
                printResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 4. BORROW BOOK 
    static void borrowBook() {
        System.out.print("Enter Book ID to borrow: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            
            if (checkBookStatus(id).equals("AVAILABLE")) {
                updateStatusInDB(id, "ISSUED");
                System.out.println("Book successfully issued to you!");
            } else {
                System.out.println("Sorry, this book is currently unavailable or doesn't exist");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }

    // 5. RETURN BOOK
    static void returnBook() {
        System.out.print("Enter Book ID to return: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            
            if (checkBookStatus(id).equals("ISSUED")) {
                updateStatusInDB(id, "AVAILABLE");
                System.out.println("Book returned successfully! Thank you");
            } else {
                System.out.println("This book is not currently issued out, or doesn't exist");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }

    // 6. DELETE BOOK
    static void deleteBook() {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            System.out.print("Enter Book ID to delete: ");
            ps.setInt(1, Integer.parseInt(sc.nextLine()));
            
            if (ps.executeUpdate() > 0) System.out.println("Book Deleted Successfully!");
            else System.out.println("Book not found.");
        } catch (NumberFormatException | SQLException e) {
            System.out.println("Invalid input or database error.");
        }
    }

    // HELPER METHODS
    static void printResultSet(ResultSet rs) throws SQLException {
        System.out.printf("\n%-10s %-25s %-20s %-15s\n", "ID", "Name", "Author", "Status");
        System.out.println("-------------------------------------------------------------------------");
        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.printf("%-10d %-25s %-20s %-15s\n",
                    rs.getInt("id"), rs.getString("name"),
                    rs.getString("author"), rs.getString("status"));
        }
        if (!found) System.out.println("No books matched your criteria.");
    }

    static String checkBookStatus(int id) {
        String sql = "SELECT status FROM books WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "NOT_FOUND";
    }

    static void updateStatusInDB(int id, String newStatus) {
        String sql = "UPDATE books SET status = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 7. EXIT APP
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