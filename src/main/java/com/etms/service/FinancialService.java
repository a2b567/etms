package com.etms.service;

import com.etms.dao.FinancialDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class FinancialService {

    private final FinancialDAO financialDAO = new FinancialDAO();

    public void addTransaction(String type, double amount, String description, Integer tournamentId) throws SQLException {
        financialDAO.addTransaction(type, amount, description, tournamentId);
    }

    public void updateTransaction(int id, String type, double amount, String description, Integer tournamentId) throws SQLException {
        financialDAO.updateTransaction(id, type, amount, description, tournamentId);
    }

    public void deleteTransaction(int id) throws SQLException {
        financialDAO.deleteTransaction(id);
    }

    public List<Map<String, Object>> getAllTransactions() throws SQLException {
        return financialDAO.getAllTransactions();
    }

    public double getTotalRevenue() throws SQLException {
        return financialDAO.getTotalRevenue();
    }

    public double getTotalExpenses() throws SQLException {
        return financialDAO.getTotalExpenses();
    }

    public double getNetProfit() throws SQLException {
        return getTotalRevenue() - getTotalExpenses();
    }

    public Map<String, Double[]> getMonthlyFinancials() throws SQLException {
        return financialDAO.getMonthlyFinancials();
    }
}