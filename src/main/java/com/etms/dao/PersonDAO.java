package com.etms.dao;

import com.etms.config.DatabaseConfig;
import com.etms.model.GenericPerson;
import com.etms.model.Person;
import java.sql.*;
import java.util.*;

public class PersonDAO {

    private final DatabaseConfig db = DatabaseConfig.getInstance();

    public int createPerson(Person person) throws SQLException {
        String sql = "INSERT INTO persons (first_name, last_name, date_of_birth, phone, country, email) VALUES (?,?,?,?,?,?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, person.getFirstName());
            ps.setString(2, person.getLastName());
            if (person.getDateOfBirth() != null && !person.getDateOfBirth().isEmpty()) {
                ps.setDate(3, java.sql.Date.valueOf(person.getDateOfBirth()));
            } else {
                ps.setNull(3, Types.DATE);
            }
            ps.setString(4, person.getPhone());
            ps.setString(5, person.getCountry());
            ps.setString(6, person.getEmail());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        }
    }

    public boolean updatePerson(Person person) throws SQLException {
        String sql = "UPDATE persons SET first_name=?, last_name=?, date_of_birth=?, phone=?, country=?, email=? WHERE person_id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, person.getFirstName());
            ps.setString(2, person.getLastName());
            if (person.getDateOfBirth() != null && !person.getDateOfBirth().isEmpty()) {
                ps.setDate(3, java.sql.Date.valueOf(person.getDateOfBirth()));
            } else {
                ps.setNull(3, Types.DATE);
            }
            ps.setString(4, person.getPhone());
            ps.setString(5, person.getCountry());
            ps.setString(6, person.getEmail());
            ps.setInt(7, person.getPersonId());
            return ps.executeUpdate() > 0;
        }
    }

    public Person getPersonById(int id) throws SQLException {
        String sql = "SELECT * FROM persons WHERE person_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Person> getAllPersons() throws SQLException {
        List<Person> list = new ArrayList<>();
        String sql = "SELECT * FROM persons ORDER BY last_name, first_name";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean deletePerson(int id) throws SQLException {
        String sql = "DELETE FROM persons WHERE person_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Map<String, Integer> getMonthlyPlayerCount() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT TO_CHAR(created_at, 'YYYY-MM') AS month, COUNT(*) AS cnt " +
                     "FROM persons WHERE person_id IN (SELECT person_id FROM players) " +
                     "GROUP BY month ORDER BY month";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("month"), rs.getInt("cnt"));
            }
        }
        return map;
    }

    private Person mapRow(ResultSet rs) throws SQLException {
        GenericPerson person = new GenericPerson();
        person.setPersonId(rs.getInt("person_id"));
        person.setFirstName(rs.getString("first_name"));
        person.setLastName(rs.getString("last_name"));
        java.sql.Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            person.setDateOfBirth(dob.toString());
        }
        person.setPhone(rs.getString("phone"));
        person.setCountry(rs.getString("country"));
        person.setEmail(rs.getString("email")); // Now included
        return person;
    }
}