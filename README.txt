SIMPLE CHATTING SYSTEM - NIMBUS MYSQL

Stack:
JSP, Servlet 4.0, AJAX (fetch), JDBC, MySQL, HTML/CSS, Maven, Tomcat 9.

Nimbus DB connection is configured in:
src/main/java/com/chat/DBConnection.java

Host: db01.dbhost.dev
Port: 5051
Database: db_454cnbsqh
Username: user_454cnbsqh

1. Run database.sql inside db_454cnbsqh on Nimbus.
2. Open this folder in VS Code.
3. Make sure the terminal is in this folder (it contains pom.xml).
4. Build:
   mvn clean package
5. Copy target/SimpleChattingSystem.war to Tomcat 9 webapps.
6. Start Tomcat 9.
7. Open http://localhost:8080/SimpleChattingSystem/
8. Login using alice, bob, or charlie.

Important:
- This project is built for Tomcat 9 because it uses javax.servlet.*.
- Maven is independent of VS Code; VS Code only opens/runs the project.
- If Maven reports BUILD FAILURE, copy the first [ERROR] block and the final 20 lines.
