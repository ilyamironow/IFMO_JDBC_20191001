package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DaoFactory {

    private ResultSet getResultSet(String s) {
        try {
            return ConnectionSource.instance().createConnection().createStatement().executeQuery(s);
        } catch (SQLException e) {
            return null;
        }
    }

    private Employee getEmployee(ResultSet resultSet) {
        try {
            FullName fullname = new FullName(resultSet.getString("firstname"),
                    resultSet.getString("lastname"),
                    resultSet.getString("middlename"));
            BigInteger managerID = resultSet.getString("manager") == null
                    ? BigInteger.ZERO : new BigInteger(resultSet.getString("manager"));
            BigInteger departmentID = resultSet.getString("department") == null
                    ? BigInteger.ZERO : new BigInteger(resultSet.getString("department"));
            return new Employee(
                    new BigInteger(resultSet.getString("id")),
                    fullname, Position.valueOf(resultSet.getString("position")),
                    LocalDate.parse(resultSet.getString("hiredate")),
                    new BigDecimal(resultSet.getString("salary")),
                    managerID,
                    departmentID);
        } catch (SQLException e) {
            System.out.println("error in EMR");
            return null;
        }
    }

    private Department departmentMapRow(ResultSet resultSet) {
        try {
            BigInteger id = new BigInteger(resultSet.getString("id"));
            return new Department(
                    id,
                    resultSet.getString("name"),
                    resultSet.getString("location"));
        } catch (SQLException e) {
            System.out.println("error in DMR");
            return null;
        }
    }

    public EmployeeDao employeeDAO() {
        return new EmployeeDao() {
            @Override
            public List<Employee> getByDepartment(Department department) {
                List<Employee> employees = new ArrayList<>();
                try {
                    ResultSet resultSet = getResultSet("select * from employee where department = " + department.getId());
                    assert resultSet != null;
                    while (resultSet.next()) {
                        employees.add(getEmployee(resultSet));
                    }
                    return employees;
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public List<Employee> getByManager(Employee manager) {
                try {
                    List<Employee> employees = new ArrayList<>();
                    ResultSet resultSet = getResultSet("select * from employee where manager = " + manager.getId());
                    assert resultSet != null;
                    while (resultSet.next()) {
                        employees.add(getEmployee(resultSet));
                    }
                    return employees;
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public Optional<Employee> getById(BigInteger Id) {
                try {
                    ResultSet resultSet = getResultSet("select * from employee where id = " + Id.toString());
                    assert resultSet != null;
                    if (resultSet.next()) {
                        return Optional.ofNullable(getEmployee(resultSet));
                    } else {
                        return Optional.empty();
                    }
                } catch (SQLException e) {
                    return Optional.empty();
                }
            }

            @Override
            public List<Employee> getAll() {
                try {
                    List<Employee> employees = new ArrayList<>();
                    ResultSet resultSet = getResultSet("select * from employee");
                    while (resultSet.next()) {
                        employees.add(getEmployee(resultSet));
                    }
                    return employees;
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public Employee save(Employee employee) {
                try {
                    PreparedStatement preparedStatement = ConnectionSource.instance().createConnection().prepareStatement("insert into employee values (?,?,?,?,?,?,?,?,?)");
                    preparedStatement.setInt(1, employee.getId().intValue());
                    preparedStatement.setString(2, employee.getFullName().getFirstName());
                    preparedStatement.setString(3, employee.getFullName().getLastName());
                    preparedStatement.setString(4, employee.getFullName().getMiddleName());
                    preparedStatement.setString(5, employee.getPosition().toString());
                    preparedStatement.setInt(6, employee.getManagerId().intValue());
                    preparedStatement.setDate(7, Date.valueOf(employee.getHired()));
                    preparedStatement.setDouble(8, employee.getSalary().doubleValue());
                    preparedStatement.setInt(9, employee.getDepartmentId().intValue());
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return employee;
            }

            @Override
            public void delete(Employee employee) {
                try {
                    Connection connection = ConnectionSource.instance().createConnection();
                    Statement statement = connection.createStatement();
                    statement.execute("delete from employee where id = " + employee.getId().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

        };
    }

    public DepartmentDao departmentDAO() {
        return new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger Id) {
                try {
                    ResultSet resultSet = getResultSet("select * from department where id = " + Id.toString());;
                    assert resultSet != null;
                    if (resultSet.next()) {
                        return Optional.ofNullable(departmentMapRow(resultSet));
                    } else {
                        return  Optional.empty();
                    }
                } catch (SQLException e) {
                    return Optional.empty();
                }
            }

            @Override
            public List<Department> getAll() {
                List<Department> deps = new ArrayList<>();
                try {
                    ResultSet rs = getResultSet("select * from department");
                    while (rs.next()) {
                        deps.add(departmentMapRow(rs));
                    }
                    return deps;
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public Department save(Department department) {
                try {
                    Statement statement = ConnectionSource.instance().createConnection().createStatement();
                    ResultSet resultSet = statement.executeQuery("select * from department where id = " + department.getId());
                    if (!resultSet.next()) {
                        statement.executeUpdate(String.format(
                                "insert into department values (%d, '%s', '%s')",
                                department.getId(),
                                department.getName(),
                                department.getLocation()));
                    } else {
                        statement.executeUpdate(String.format(
                                "update department set name='%s', location='%s' where id=%d",
                                department.getName(),
                                department.getLocation(),
                                department.getId()));
                    }
                    return department;
                } catch (SQLException e) {
                    return null;
                }

            }

            @Override
            public void delete(Department department){
                try {
                    ConnectionSource.instance().createConnection().createStatement().executeUpdate("delete from department where id = " + department.getId().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}

