package com.efimchick.ifmo.web.jdbc;

/**
 * Implement sql queries like described
 */
public class SqlQueries {
    //Select all employees sorted by last name in ascending order
    //language=HSQLDB
    String select01 = "SELECT * FROM Employee ORDER BY lastname";

    //Select employees having no more than 5 characters in last name sorted by last name in ascending order
    //language=HSQLDB
    String select02 = "SELECT * FROM Employee WHERE length(lastname) <= 5 ORDER BY lastname";

    //Select employees having salary no less than 2000 and no more than 3000
    //language=HSQLDB
    String select03 = "SELECT * FROM Employee WHERE salary BETWEEN 2000 AND 3000";

    //Select employees having salary no more than 2000 or no less than 3000
    //language=HSQLDB
    String select04 = "SELECT * FROM Employee WHERE salary NOT BETWEEN 2001 AND 2999";

    //Select employees assigned to a department and corresponding department name
    //language=HSQLDB
    String select05 = "SELECT * FROM Employee " +
                "INNER JOIN DEPARTMENT ON Employee.DEPARTMENT = DEPARTMENT.id";

    //Select all employees and corresponding department name if there is one.
    //Name column containing name of the department "depname".
    //language=HSQLDB
    String select06 = "SELECT Employee.id, firstname, lastname, middlename, " +
            "position, manager, hiredate, salary, DEPARTMENT.NAME AS depname FROM Employee " +
            "LEFT JOIN DEPARTMENT on Employee.DEPARTMENT = DEPARTMENT.ID";

    //Select total salary pf all employees. Name it "total".
    //language=HSQLDB
    String select07 = "SELECT SUM(salary) AS total FROM Employee";

    //Select all departments and amount of employees assigned per department
    //Name column containing name of the department "depname".
    //Name column containing employee amount "staff_size".
    //language=HSQLDB
    String select08 = "SELECT DEPARTMENT.NAME AS depname, COUNT(Employee.ID) AS staff_size FROM Employee " +
            "INNER JOIN DEPARTMENT ON Employee.DEPARTMENT = DEPARTMENT.ID GROUP BY DEPARTMENT.NAME";

    //Select all departments and values of total and average salary per department
    //Name column containing name of the department "depname".
    //language=HSQLDB
    String select09 = "SELECT DEPARTMENT.name AS depname, SUM(Employee.salary) AS total, AVG(Employee.salary) " +
            "AS average FROM DEPARTMENT INNER JOIN Employee ON DEPARTMENT.ID = Employee.DEPARTMENT GROUP BY DEPARTMENT.name";

    //Select all employees and their managers if there is one.
    //Name column containing employee lastname "employee".
    //Name column containing manager lastname "manager".
    //language=HSQLDB
    String select10 = "SELECT Employee1.lastname AS Employee, Employee2.lastname AS manager FROM Employee Employee1 LEFT JOIN Employee Employee2 ON Employee1.manager=Employee2.ID";


}
