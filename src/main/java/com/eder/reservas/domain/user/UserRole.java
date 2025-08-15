package com.eder.reservas.domain.user;

public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    final private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getUserRole() {
        return this.role;
    }
}
