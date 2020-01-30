package com.efimchick.ifmo.web.jdbc;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.LocalDate;

public class RowMapperFactory {

    public RowMapper<Employee> employeeRowMapper() {
        return resultSet -> {
            try {
                return new Employee(new BigInteger(resultSet.getString("id")),
                        new FullName(resultSet.getString("firstName"),
                                resultSet.getString("lastName"),
                                resultSet.getString("middleName")),
                        Position.valueOf(resultSet.getString("position")),
                        LocalDate.parse(resultSet.getString("hiredate")),
                        resultSet.getBigDecimal("salary"));
            } catch (SQLException e) {
                throw new UnsupportedOperationException();
            }
        };
    }
}