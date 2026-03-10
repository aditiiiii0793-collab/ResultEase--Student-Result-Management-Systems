# ResultEase: Student Result Management System 🚀

A web application for managing and viewing student academic results. This project was built as part of my Semester 3 curriculum.

[ working screenshots of the project : screenshots]

## Features

* **Admin Panel:** Admins can securely log in to add, update, and delete student records and results.
* **Student View:** Students can log in to view their own mark sheets and results by semester.
* **Secure Authentication:** Uses Spring Security (if you've added it) for login.
* **Dynamic Web Pages:** Uses Thymeleaf to display data from the backend.

## 🛠️ Tech Stack

* **Backend:** Java (Spring Boot)
* **Frontend:** Thymeleaf, HTML, CSS
* **Database:** MariaDB (SQL)
* **Build Tool:** Maven

## 🏃 How to Run Locally

To get a local copy up and running, follow these steps.

**Prerequisites:**
* JDK (Java Development Kit) 17 or newer
* Apache Maven
* A MariaDB server

**Installation:**

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/YOUR_USERNAME/resultease.git](https://github.com/YOUR_USERNAME/resultease.git)
    cd resultease
    ```
2.  **Set up the database:**
    * Create a new database in MariaDB named `resultease_db`.
    * (If you have a `.sql` script) Import the schema: `mysql -u root -p resultease_db < schema.sql`
3.  **Configure the application:**
    * Open `src/main/resources/application.properties`.
    * Update the `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` to match your local MariaDB setup.
    **Note:** Make sure this `application.properties` file is in your `.gitignore` if it contains real passwords!
4.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
5.  Open your browser and go to `http://localhost:8080`.

