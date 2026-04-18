# Library Management System (Java CLI + MySQL)

A command-line Library Management System built with Java and JDBC. 
This application is fully containerized using Docker and Docker Compose, 
completely separating the database from the application logic while allowing 
them to communicate securely over a custom Docker network.

## 📁 Project Files

To successfully run this project, ensure the following files are located in the same directory:
* `LibraryManagementSystem.java` (main application source code)
* `Dockerfile` (Builds the Java runtime environment)
* `docker-compose.yml` (Configures the MySQL database)
* `mysql-connector-j-9.6.0.jar` (MySQL JDBC Driver required for database connection)

---

## 🚀 How to Run the Application

### Step 1: Start the Docker Environment
Open your terminal inside the project folder and run the following command. 
This will build the Java image, download the MySQL image, and 
start both containers in the background:
```bash
docker-compose up -d --build
```

### Step 2: Launch the Java Application
To interact with the Library Management System, 
hook into the running Java container by executing the application directly:

```bash
docker exec -it java-container java -cp ".:mysql-connector-j-9.6.0.jar" LibraryManagementSystem
```

### Step 3: Access the Database Directly (Optional)
If you wish to view the backend database, verify tables, 
or run manual SQL queries, you can launch an interactive 
MySQL terminal inside the database container:

``` bash
docker exec -it mysql-container mysql -u root -p
```

->When prompted for the password, enter: rootpassword
->Once inside, you can run commands like USE librarydb; and 
SELECT * FROM books; 
Type exit to leave the MySQL terminal.


### 🛑 How to Stop the Environment
When you are completely finished testing the project, 
you can safely shut down both the Java and MySQL containers by running:

``` Bash
docker-compose down
```
# Note on Data Persistence: 
Shutting down the containers using the command above will not delete your saved books. 
Docker uses a named volume to safely persist your data between sessions. 
If you wish to permanently wipe the database and start completely fresh, run:

``` Bash
docker-compose down -v
```