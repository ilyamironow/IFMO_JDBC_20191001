package com.efimchick.ifmo.web.jdbc.service;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceFactory {

    private ResultSet getResultset(String s) {
        try {
            return ConnectionSource.instance().createConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE).executeQuery(s);
        } catch (SQLException e) {
            return null;
        }
    }

    private List<Employee> getEmployeeListByResultSet(ResultSet res) throws SQLException {
        res.beforeFirst();
        List<Employee> ans = new ArrayList<>();
        while (res.next()) {
            ans.add(employeeRowMapper(res,true));
        }
        return ans;
    }

    private Employee employeeRowMapper(ResultSet resultSet, boolean isFirstLevel) {
        Employee cur = null;
        try {
            String managerId = resultSet.getString("MANAGER");
            if (!isFirstLevel){
                managerId = null;
            }
            String departmentId = resultSet.getString("DEPARTMENT");
            cur = new Employee(
                    new BigInteger(String.valueOf(resultSet.getInt("ID"))),
                    new FullName(
                            resultSet.getString("FIRSTNAME"),
                            resultSet.getString("LASTNAME"),
                            resultSet.getString("MIDDLENAME")
                    ),
                    Position.valueOf(resultSet.getString("POSITION")),
                    LocalDate.parse(resultSet.getString("HIREDATE")),
                    new BigDecimal(resultSet.getInt("SALARY")),
                    managerId == null ? null : getEmployeeById(new BigInteger(managerId)),
                    departmentId == null ? null : getDepartmentById(new BigInteger(departmentId))
            );
        } catch (SQLException ignored) {
        }
        return cur;
    }

    private Employee employeeRowMapper(ResultSet resultSet) {
        Employee cur = null;
        try {
            String managerId = resultSet.getString("MANAGER");
            String departmentId = resultSet.getString("DEPARTMENT");
            cur = new Employee(
                    new BigInteger(String.valueOf(resultSet.getInt("ID"))),
                    new FullName(
                            resultSet.getString("FIRSTNAME"),
                            resultSet.getString("LASTNAME"),
                            resultSet.getString("MIDDLENAME")
                    ),
                    Position.valueOf(resultSet.getString("POSITION")),
                    LocalDate.parse(resultSet.getString("HIREDATE")),
                    new BigDecimal(resultSet.getInt("SALARY")),
                    managerId == null ? null : getEmployeeByIdWithChain(new BigInteger(managerId)),
                    departmentId == null ? null : getDepartmentById(new BigInteger(departmentId))
            );
        } catch (SQLException ignored) {
        }
        return cur;
    }

    private Employee getEmployeeByIdWithChain(BigInteger Id) {
        try {
            ResultSet res = getResultset("select * from employee where id = " + Id);
            assert res != null;
            res.next();
            return employeeRowMapper(res);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Employee getEmployeeById(BigInteger Id) {
        try {
            ResultSet res = getResultset("select * from employee where id = " + Id);
            assert res != null;
            res.next();
            return employeeRowMapper(res,false);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Department getDepartmentById(BigInteger Id) {
        try {
            ResultSet res = getResultset("select * from department where id = " + Id);
            assert res != null;
            return getDepartmentListByResultSet(res).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Department departmentRowMapper(ResultSet res) {
        try {
            return new Department(
                    new BigInteger(res.getString("ID")),
                    res.getString("NAME"),
                    res.getString("LOCATION")
            );
        } catch (SQLException e) {
            return null;
        }
    }

    private List<Department> getDepartmentListByResultSet(ResultSet res) throws SQLException {
        res.beforeFirst();
        List<Department> ans = new ArrayList<>();
        try {
            while (res.next()) {
                ans.add(departmentRowMapper(res));
            }
        } catch (SQLException ignored) {
        }
        return ans;
    }
    private List<Employee> getAllSortBy(String from, Paging paging) {
        try {
            ResultSet res = getResultset(
                    from + " limit " + paging.itemPerPage +
                            " offset " + paging.itemPerPage * (paging.page - 1)
            );
            assert res != null;
            return getEmployeeListByResultSet(res);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public EmployeeService employeeService() {
        return new EmployeeService() {
            @Override
            public List<Employee> getAllSortByHireDate(Paging paging) {
                return getAllSortBy("select * from employee " +
                        "order by hiredate", paging);
            }

            @Override
            public List<Employee> getAllSortByLastname(Paging paging) {
                return getAllSortBy("select * from employee " +
                        "order by lastname", paging);
            }

            @Override
            public List<Employee> getAllSortBySalary(Paging paging) {
                return getAllSortBy("select * from employee " +
                        "order by salary", paging);
            }

            @Override
            public List<Employee> getAllSortByDepartmentNameAndLastname(Paging paging) {
                return getAllSortBy("select * from employee left join department on " +
                        "employee.department = department.id order by " +
                        "department.name,employee.lastname", paging);
            }

            @Override
            public List<Employee> getByDepartmentSortByHireDate(Department department, Paging paging) {
                return getAllSortBy("select * from employee where department = "+department.getId()+"order by hiredate", paging);
            }

            @Override
            public List<Employee> getByDepartmentSortBySalary(Department department, Paging paging) {
                return getAllSortBy("select * from employee where department = " +
                        department.getId() + "order by salary", paging);
            }

            @Override
            public List<Employee> getByDepartmentSortByLastname(Department department, Paging paging) {
                return getAllSortBy("select * from employee where department = " +
                        department.getId() + "order by lastname", paging);
            }

            @Override
            public List<Employee> getByManagerSortByLastname(Employee manager, Paging paging) {
                return getAllSortBy("select * from employee where manager = " +
                        manager.getId() + "order by lastname", paging);
            }

            @Override
            public List<Employee> getByManagerSortByHireDate(Employee manager, Paging paging) {
                return getAllSortBy("select * from employee where manager = " +
                        manager.getId() + "order by hiredate", paging);
            }

            @Override
            public List<Employee> getByManagerSortBySalary(Employee manager, Paging paging) {
                    return getAllSortBy("select * from employee where manager = " +
                            manager.getId()+"order by salary", paging);
            }

            @Override
            public Employee getWithDepartmentAndFullManagerChain(Employee employee) {
                try {
                    ResultSet resultSet = getResultset("select * from employee where id = " + employee.getId());
                    assert resultSet != null;
                    resultSet.next();
                    return employeeRowMapper(resultSet);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public Employee getTopNthBySalaryByDepartment(int salaryRank, Department department) {
                try {
                    ResultSet res = getResultset(
                            "select * from employee where department = "+department.getId()+"order by salary desc" +
                                    " limit 1" +
                                    " offset " + (salaryRank-1)
                    );
                    assert res != null;
                    return getEmployeeListByResultSet(res).get(0);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }
}

