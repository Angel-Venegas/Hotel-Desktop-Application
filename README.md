1. Install the latest version of InteliJ. I used the community version.
2. Upon cloning an then opening this repository, you will need to download Java oenjdk-21.0.1 or higher.
3. Install JavaFX openjfx-21.0.01 or higher and unzip it to a location you will remember.
4. Click on "File" in IntelliJ, click "Project Structure", "Libraries", "+ sign", "Java", locate the javafx sdk and select the folder that says "lib" then click "Apply" then "Ok".

5. Download the mySQL driver "https://www.mysql.com/products/connector/", select "JDBC Driver for MySQL (Connector/J)", and then download the one that is "Platform Independent". Then unzip it to a location you will remember.
6. On the project with IntelliJ, click "File", "Project Structure", "Libraries", "+", "Java", navigate to the mySQL connector folder and open it to select the .jar file (the one I used is called "mysql-connector-j-8.2.0.jar". Click on it and then click ok and then "Apply".
7. Install mySQL community editions and if on Windows 11 "https://dev.mysql.com/downloads/installer/" select version 8.0.35 or higher and "https://dev.mysql.com/downloads/workbench/" version 8.0.35 or higher.
8. Open mySQL workbench, login as the "root" user in your local instance,
   create this database in a query "CREATE DATABASE hotel_db;"
   create a user by typing this into a query "CREATE USER 'HotelGuest'@'localhost' IDENTIFIED BY 'HotelGuestLogin123';"
   and then "GRANT ALL PRIVILEGES ON `hotel_db`.* TO 'HotelGuest'@'localhost';
             FLUSH PRIVILEGES;"
9. Then logout of the root instance and create a new connection;
   Connection Name: Hotel_Db
   Connection Method: Standard (TCP/IP)
   Make sure you are in the "Parameters" tab,
     Hostname: 127.0.0.1 Port: 3306
     Username: HotelGuest
     Default Schema: hotel_db
   and then click "ok".
10. Click on that instance and enter the password "HotelGuestLogin123".
11. After logging in you may navigate to schemas to observe any data from the program.

12. Since there are no tables, upon runnning the program a textfile within the program as well as the database in mySQL will automatically be populated with data.
13. You may register a new user if you wish and begin using the hotel application!

14. To login as a manager in the hotel application the username is "m" and the password is "p".
