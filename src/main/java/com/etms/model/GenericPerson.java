package com.etms.model;

public class GenericPerson extends Person {
    public GenericPerson() {
        super();
    }

    public GenericPerson(String firstName, String lastName) {
        super(firstName, lastName);
    }

    @Override
    public String getRole() {
        return "PERSON";
    }
}