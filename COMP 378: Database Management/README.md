## Lab 1

### Create database and tables

```sql
CREATE DATABASE univeristy;

CREATE TABLE student (
    stud_id integer,
    stud_name varchar(30),
    address varchar(40),
    telephone varchar(10)
);

CREATE TABLE course (
    course_no varchar(7),
    title varchar(40),
    inst_no integer
);

CREATE TABLE registration (
    stud_id integer,
    course_no varchar(7),
    term varchar(15)
);

CREATE TABLE instructor (
    inst_no integer,
    inst_name varchar(30),
    department varchar(50)
);
```

### Populate the tables with some records

```sql
INSERT INTO student 
VALUES (10, 'Bob Smith','123 sesame street New York USA','7803333222');

INSERT INTO student 
VALUES (11, 'Karen Lee', '1 university drive Athabasca Canada','5875555444');

INSERT INTO student 
VALUES ('COMP378', 'Database Management', 103);

INSERT INTO course
VALUES values ('COMP456', 'Artificial Intelligence', 103);

INSERT INTO registration
VALUES ('10', 'COMP378', 'Winter 2019');

INSERT INTO registration
VALUES ('11', 'COMP456', 'Spring 2020');

INSERT INTO instructor
VALUES ('103', 'Lary Mckay', 'School of Computing and Information Systems');

INSERT INTO instructor
VALUES ('104', 'Harris Terrien', 'School of Business');
```

### Query the tables contents

```sql
SELECT * FROM student;
```

```sql
SELECT * FROM course;
```

```sql
SELECT * FROM registration;
```

```sql
SELECT * FROM instructor;
```

## Assignment 2

### Question 2

Consider these three relations and then write SQL statements to answer the questions
that follow.  

TRAVEL_AGENT (TA_no, name, age, salary)  
CUSTOMER (C_ID, name, departure_city, destination, journey_class)  
TRANSACTION (number, C_ID, TA_no, date, amount_paid)  

```sql
CREATE TABLE TRAVEL_AGENT (
    TA_no INT NOT NULL,
    name TEXT,
    age INT,
    salary NUMERIC,
CONSTRAINT TRAVEL_AGENT_PK PRIMARY KEY(TA_no)
);

CREATE TABLE CUSTOMER (
    C_ID INT NOT NULL,
    name TEXT,
    departure_city TEXT,
    destination TEXT,
    journey_class TEXT,
CONSTRAINT Customer_PK PRIMARY KEY(C_ID)
);

CREATE TABLE PAYMENT (
    pay_no INT NOT NULL,
    C_ID INT,
    TA_no INT,
    date DATE,
    amount_paid NUMERIC,
CONSTRAINT Payment_PK PRIMARY KEY(pay_no),
CONSTRAINT Payment_FK1 FOREIGN KEY(C_ID) REFERENCES CUSTOMER(C_ID),
CONSTRAINT Payment_FK2 FOREIGN KEY(TA_no) REFERENCES TRAVEL_AGENT(TA_no)
);

INSERT INTO travel_agent VALUES (1, 'Bob', 69, 69000.00);
INSERT INTO travel_agent VALUES (2, 'Bill', 70, 45000.00);
INSERT INTO customer VALUES (1, 'Alex', 'Toronto', 'Lisbon', 'Economy');
INSERT INTO customer VALUES (2, 'Sam', 'Toronto', 'Lisbon', 'First Class');
INSERT INTO customer VALUES (3, 'Elon Musk', 'San Jose', 'Austin', 'Private');
INSERT INTO payment VALUES (1, 1, 1, '2024-03-13', 1000.01);
INSERT INTO payment VALUES (2, 2, 2, '2024-03-13', 5000.00);
INSERT INTO payment VALUES (3, 3, 1, '2024-01-01', 50000.00);

SELECT * FROM travel_agent;
SELECT * FROM customer;
SELECT * FROM payment;
```

a. Compute the number of different customers who have a transaction.
```sql
SELECT COUNT(DISTINCT C_ID) 
FROM TRANSACTION;
```

b. Display the name of the oldest travel agent.
```sql
SELECT MAX(age) 
FROM TRAVEL_AGENT;
```

c. List the total number of transactions for each travel agent. Consider only those transactions where the amount paid exceeds 1000.
```sql
SELECT TA_no, COUNT(*) AS total_transactions
FROM TRANSACTION
WHERE amount_paid > 1000
GROUP BY TA_no;
```

d. Display the names and ages of the travel agents who have arranged journeys for customer “John Smith”, in descending order of age. (Use a subquery.)
```sql
SELECT name, age
FROM travel_agent
WHERE ta_no IN (SELECT ta_no FROM payment WHERE c_id = (SELECT c_id FROM customer WHERE name = 'John Smith'))
ORDER BY age DESC;
```

e. Display the names and ages of travel agents who have arranged journeys for customer “John Smith”, in descending order of age. (Do not use a subquery.)
```sql
SELECT DISTINCT t.name, t.age
FROM travel_agent t
INNER JOIN payment p ON t.TA_no = p.TA_no
INNER JOIN customer c ON p.C_ID = c.C_ID
WHERE c.name = 'John Smith'
ORDER BY t.age DESC;
```

f. Display the age of travel agents who have arranged journeys for customer “John Smith” to “Ottawa”. (Use a subquery.)
```sql
SELECT age
FROM travel_agent
WHERE ta_no IN (SELECT ta_no FROM payment WHERE c_id = (SELECT c_id FROMcustomer WHERE name = 'John Smith' AND destination = 'Ottawa'));
```

g. Display the age of travel agents who have arranged journeys for customer “John Smith” to “Ottawa”. (Do not use a subquery.)
```sql
SELECT DISTINCT t.age
FROM travel_agent t
INNER JOIN payment p ON t.TA_no = p.TA_no
INNER JOIN customer c ON p.C_ID = c.C_ID
WHERE c.name = 'John Smith' AND c.destination = 'Ottawa';
```

h. Display the names and salaries of all travel agents who have not arranged journeys for customer “John Smith”, in ascending order of salary.
```sql
SELECT t.name, t.salary
FROM travel_agent t
LEFT JOIN payment p ON t.TA_no = p.TA_no AND p.C_ID = (SELECT C_ID FROM customer WHERE name = 'John Smith')
WHERE p.C_ID IS NULL
ORDER BY t.salary ASC;
```

i. Display the names of travel agents who have five or more transactions.
```sql
SELECT ta_no, COUNT(*) AS num_transactions
FROM payment
GROUP BY ta_no
HAVING COUNT(*) >= 5;
```

j. Display the names of all travel agents who have arranged at least ten journeys to “Ottawa”.
```sql
SELECT t.name
FROM travel_agent t
INNER JOIN payment p ON t.TA_no = p.TA_no
INNER JOIN customer c ON p.C_ID = c.C_ID
WHERE c.destination = 'Ottawa'
GROUP BY t.name
HAVING COUNT(*) >= 10;
```

### Question 3

Consider the following Realtor database with three relations.  

RealtorAgent (realtor_no, name, age, address, commission_percentage)  
Customer (C_ID, name, property_id, property_address, description, property_tax, property_value)  
Transaction (realtor_no, C_ID, property_id, price_sold, date_sold)  

```sql
CREATE TABLE realtor_agent (
    realtor_no INT PRIMARY KEY,
    name VARCHAR, 
    age INT, 
    address VARCHAR, 
    commission_percentage DECIMAL
);

CREATE TABLE customer (
    c_id INT, 
    name VARCHAR, 
    property_id INT,
    property_address VARCHAR, 
    description TEXT, 
    property_tax DECIMAL, 
    property_value DECIMAL,
    PRIMARY KEY (c_id, property_id)
);

CREATE TABLE payment (
    realtor_no INT REFERENCES realtor_agent, 
    c_id INT, 
    property_id INT, 
    price_sold DECIMAL, 
    date_sold DATE,
    FOREIGN KEY (c_id, property_id) REFERENCES customer
);

INSERT INTO realtor_agent VALUES (1, 'Carmen', 50, '123 Anywhere St', 20.0);
INSERT INTO realtor_agent VALUES (2, 'Joe', 60, '55 Some Rd', 35.2);

INSERT INTO customer VALUES (1, 'Alex', 1, '1 Blue Jay Way', 'Rogers Center', 20.1, 69000000.00);
INSERT INTO customer VALUES (2, 'Sydney', 2, '27 Kings College Circle', 'University of Toronto', 0.1, 3.50);

INSERT INTO payment VALUES (1, 1, 1, 250000, '2018-03-13');
INSERT INTO payment VALUES (2, 2, 2, 5000000.01, '2024-03-13');

SELECT * FROM realtor_agent;
SELECT * FROM customer;
SELECT * FROM payment;
```

Write SQL statements for
creating a view called AgentsOfTheYear that will display the name and address of realtors who have
sold a total amount of 5 million dollars’ worth of properties in 2021.

```sql
CREATE VIEW AgentsOfTheYear AS
SELECT name, address
FROM realtor_agent
WHERE realtor_no IN (
    SELECT realtor_no
    FROM ( 
        SELECT realtor_no, SUM(price_sold) AS total_sales
        FROM payment
        WHERE EXTRACT(YEAR FROM date_sold) = '2021'
        GROUP BY realtor_no
    ) AS SubTotalSales
    WHERE total_sales >= 5000000
);

SELECT * FROM AgentsOfTheYear;
```

### Question 4

Consider the following relations:  

Student (student_no, name, address, GPA)  
Course (course_no, title, texbook)  
Instructor (instructor_no, name, phone_no, specialization)  
Registration (student_no, course_no, instructor_no, session)  
Qualified_to_teach (instructor_no, course_no)  

```sql
CREATE TABLE Student (
    student_no INT PRIMARY KEY, 
    name VARCHAR, 
    address VARCHAR, 
    GPA DECIMAL
);

CREATE TABLE Course (
    course_no INT PRIMARY KEY, 
    title VARCHAR, 
    texbook VARCHAR
);

CREATE TABLE Instructor (
    instructor_no INT PRIMARY KEY, 
    name VARCHAR, 
    phone_no VARCHAR, 
    specialization VARCHAR
);

CREATE TABLE Registration (
    student_no INT REFERENCES Student, 
    course_no INT REFERENCES Course, 
    instructor_no INT REFERENCES Instructor, 
    session VARCHAR
);

CREATE TABLE Qualified_to_teach (
    instructor_no INT REFERENCES Instructor, 
    course_no INT REFERENCES Course
);

INSERT INTO Student VALUES (1, 'Alex Perrin', 'Kingston ON', 2.4);
INSERT INTO Student VALUES (2, 'Sydney Perrin', 'Toronto ON', 4.0);

INSERT INTO Course VALUES (1, 'COMP 378', 'Databases');
INSERT INTO Course VALUES (2, 'COMP 418', 'Coding');
INSERT INTO Course VALUES (3, 'BAWE 420', 'Basket Weaving');

INSERT INTO Instructor VALUES (1, 'Mr Prof', '555-555-5555', 'Engineering');
INSERT INTO Instructor VALUES (2, 'Dr Science', '444-444-4444', 'Computer Science');

INSERT INTO Registration VALUES (1, 1, 1, 'Winter 2019'); -- AP, 378, Mr Prof
INSERT INTO Registration VALUES (1, 1, 1, 'Winter 2020'); -- AP, 378, Mr Prof
INSERT INTO Registration VALUES (1, 2, 2, 'Winter 2021'); -- AP, 418, Dr Science
INSERT INTO Registration VALUES (2, 1, 2, 'Winter 2020'); -- Syd, 378, Dr Science
INSERT INTO Registration VALUES (2, 2, 2, 'Winter 2021'); -- Syd, 418, Dr Science
INSERT INTO Registration VALUES (2, 3, 1, 'Winter 2020'); -- Syd, 420, Mr Prof

INSERT INTO Qualified_to_teach VALUES (1, 1);
INSERT INTO Qualified_to_teach VALUES (2, 1);
INSERT INTO Qualified_to_teach VALUES (1, 3);
INSERT INTO Qualified_to_teach VALUES (2, 2);

SELECT * FROM Student;
SELECT * FROM Course;
SELECT * FROM Instructor;
SELECT * FROM Registration;
SELECT * FROM Qualified_to_teach;
```

Write SQL query to list the names of instructors who have taught COMP 378 but not COMP 418
during the following sessions: Winter 2020 and Winter 2021.

```sql
SELECT DISTINCT name
FROM Instructor
JOIN Qualified_to_teach ON Instructor.instructor_no = Qualified_to_teach.instructor_no
JOIN Registration ON Qualified_to_teach.instructor_no = Registration.instructor_no
JOIN Course ON Registration.course_no = Course.course_no
WHERE Registration.session IN ('Winter 2020', 'Winter 2021')
AND Course.title = 'COMP 378'
AND NOT EXISTS (
    SELECT 1
    FROM Course AS Course2
    JOIN Registration AS Registration2 ON Course2.course_no = Registration2.course_no
    WHERE Registration2.instructor_no = Qualified_to_teach.instructor_no
    AND Registration2.session IN ('Winter 2020', 'Winter 2021')
    AND Course2.title = 'COMP 418'
);

```