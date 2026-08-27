package com.etms.controller;

import com.etms.service.AuthenticationService;

public class AuthController {
    private final AuthenticationService authService = new AuthenticationService();

    public boolean login(String username, String password) {
        return authService.login(username, password);
    }

    public boolean register(String username, String password, String email, String role) {
        return authService.register(username, password, email, role);
    }

    public void logout() {
        authService.logout();
    }
}