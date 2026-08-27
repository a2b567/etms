package com.etms.service;

import com.etms.dao.UserDAO;
import com.etms.model.User;
import com.etms.util.PasswordUtil;

public class AuthenticationService {
    private final UserDAO userDAO = new UserDAO();

    public boolean login(String username, String password) {
        try {
            User user = userDAO.findByUsername(username);
            if (user != null && user.isActive() &&
                PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                UserSession.setCurrentUser(user);
                userDAO.updateLastLogin(user.getUserId());
                return true;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean register(String username, String password, String email, String role) {
        try {
            if (userDAO.findByUsername(username) != null) return false;
            if (userDAO.findByEmail(email) != null) return false;
            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(PasswordUtil.hashPassword(password));
            user.setEmail(email);
            user.setRole(role);
            user.setActive(true);
            return userDAO.createUser(user);
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public void logout() {
        UserSession.logout();
    }
}