package com.smartdiscover.entity.projection;

public record PersonFullName(String firstName, String lastName) {

    public String getFullName() {
        return lastName + " " + firstName;
    }
}
