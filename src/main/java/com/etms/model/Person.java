package com.etms.model;

public abstract class Person {
    private int personId;
    private int userId;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String phone;
    private String email;        // NEW
    private String country;

    public Person() {}

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public abstract String getRole();

    // Getters and Setters
    public int getPersonId() { return personId; }
    public void setPersonId(int personId) { this.personId = personId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (email != null && !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    @Override
    public String toString() {
        return getFullName();
    }
}