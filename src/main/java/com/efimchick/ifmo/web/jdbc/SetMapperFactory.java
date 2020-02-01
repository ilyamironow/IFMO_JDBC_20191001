package com.efimchick.ifmo.web.jdbc;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class SetMapperFactory {

    public SetMapper<Set<Employee>> employeesSetMapper() {
        return resultSet -> {
            Set<Employee> employees = new HashSet<>();
            try {
                while (resultSet.next()) {
                    Employee cur = getEmployee(resultSet);
                    employees.add(cur);
                }
            } catch (SQLException ignored) {
                //lalala
            }
            return employees;
        };
    }

    private Employee getEmployee(ResultSet rs) {
        try {
            return new Employee(
                    new BigInteger(rs.getString("ID")),
                    new FullName(
                            rs.getString("firstname"),
                            rs.getString("lastname"),
                            rs.getString("middlename")
                    ),
                    Position.valueOf(rs.getString("POSITION")),
                    LocalDate.parse(rs.getString("HIREDATE")),
                    new BigDecimal(rs.getString("SALARY")),
                    Manager(rs)
            );
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
    }

    private Employee Manager(ResultSet rs) throws SQLException {
        int currentRowID = rs.getRow();
        Employee manager = null;
        int managerID = rs.getInt("manager");
        if (managerID == 0) return null;
        rs.beforeFirst();
        while (rs.next()) {
            if (rs.getInt("id") == managerID) {
                manager = getEmployee(rs);break;
            }
        }
        rs.absolute(currentRowID);
        return manager;
    }
}
/* public class SetMapperFactory {

    public SetMapper<Set<Employee>> employeesSetMapper() {
        throw new UnsupportedOperationException();
    }
}*/
