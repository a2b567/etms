package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.dao.FinancialDAO;
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import net.miginfocom.swing.MigLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

public class FinancialPanel extends JPanel {

    private final FinancialDAO financialDAO = new FinancialDAO();
    private JLabel revenueLabel, expenseLabel, profitLabel;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> typeCombo;
    private JTextField amountField, descField;
    private JComboBox<String> tournamentCombo;
    private ChartPanel chartPanel;

    public FinancialPanel(DashboardController controller) {
        setBackground(ThemeManager.getBackground());
        initComponents();
        loadTransactions();
        updateSummary();
        updateChart();
    }

    private void initComponents() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));

        JLabel title = new JLabel("Financial Management");
        title.setFont(Typography.PAGE_TITLE);
        title.setForeground(ThemeManager.getText());
        add(title, "wrap, gapbottom 20");

        JPanel summaryPanel = new JPanel(new MigLayout("fillx", "[grow][grow][grow]", "[]"));
        summaryPanel.setOpaque(false);
        revenueLabel = createSummaryLabel();
        expenseLabel = createSummaryLabel();
        profitLabel = createSummaryLabel();
        summaryPanel.add(createSummaryCard("Total Revenue", revenueLabel, ThemeManager.getSuccess()), "grow");
        summaryPanel.add(createSummaryCard("Total Expenses", expenseLabel, ThemeManager.getDanger()), "grow");
        summaryPanel.add(createSummaryCard("Net Profit", profitLabel, ThemeManager.getAccent()), "grow");
        add(summaryPanel, "growx, wrap, gapbottom 20");

        JPanel middle = new JPanel(new MigLayout("fill", "[grow][grow]", "[grow]"));
        middle.setOpaque(false);
        chartPanel = createChartPanel();
        middle.add(chartPanel, "grow");
        middle.add(createTransactionFormAndTable(), "grow");
        add(middle, "grow");
    }

    private JLabel createSummaryLabel() {
        JLabel lbl = new JLabel("$0.00");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setForeground(ThemeManager.getText());
        return lbl;
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new MigLayout("fill, insets 15", "[center]", "[center][center]"));
        card.setBackground(ThemeManager.getSurface());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorder()),
                BorderFactory.createEmptyBorder(5,5,5,5)));
        JLabel t = new JLabel(title);
        t.setFont(Typography.LABEL);
        t.setForeground(ThemeManager.getTextSecondary());
        valueLabel.setForeground(accent);
        card.add(t, "wrap, align center");
        card.add(valueLabel, "align center");
        return card;
    }

    private ChartPanel createChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            Map<String, Double[]> data = financialDAO.getMonthlyFinancials();
            for (Map.Entry<String, Double[]> e : data.entrySet()) {
                dataset.addValue(e.getValue()[0], "Revenue", e.getKey());
                dataset.addValue(e.getValue()[1], "Expenses", e.getKey());
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        if (dataset.getRowCount() == 0) {
            dataset.addValue(0, "Revenue", "No Data");
            dataset.addValue(0, "Expenses", "No Data");
        }
        JFreeChart chart = ChartFactory.createBarChart("Monthly Revenue vs Expenses", "Month", "Amount ($)", dataset,
                PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint(ThemeManager.getSurface());
        chart.getTitle().setPaint(ThemeManager.getText());
        chart.getLegend().setItemPaint(ThemeManager.getText());
        ChartPanel cp = new ChartPanel(chart);
        cp.setBackground(ThemeManager.getSurface());
        cp.setPreferredSize(new Dimension(400, 300));
        return cp;
    }

    private JPanel createTransactionFormAndTable() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[]10[grow]"));

        JPanel form = new JPanel(new MigLayout("wrap 2, fillx", "[right][grow]", "[]5[]"));
        form.setBackground(ThemeManager.getSurface());
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            "Add Transaction",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            Typography.CARD_TITLE,
            ThemeManager.getText()
        ));

        form.add(new JLabel("Type:"));
        typeCombo = new JComboBox<>(new String[]{"REGISTRATION_FEE", "SPONSORSHIP", "PRIZE_PAYOUT", "EXPENSE"});
        typeCombo.setBackground(ThemeManager.getSurface());
        typeCombo.setForeground(ThemeManager.getText());
        form.add(typeCombo, "growx");

        form.add(new JLabel("Amount ($):"));
        amountField = new JTextField(10);
        amountField.setBackground(ThemeManager.getSurface());
        amountField.setForeground(ThemeManager.getText());
        amountField.setCaretColor(ThemeManager.getAccent());
        amountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            BorderFactory.createEmptyBorder(Spacing.SM, Spacing.MD, Spacing.SM, Spacing.MD)
        ));
        form.add(amountField, "growx");

        form.add(new JLabel("Description:"));
        descField = new JTextField(20);
        descField.setBackground(ThemeManager.getSurface());
        descField.setForeground(ThemeManager.getText());
        descField.setCaretColor(ThemeManager.getAccent());
        descField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            BorderFactory.createEmptyBorder(Spacing.SM, Spacing.MD, Spacing.SM, Spacing.MD)
        ));
        form.add(descField, "growx");

        form.add(new JLabel("Tournament:"));
        tournamentCombo = new JComboBox<>();
        tournamentCombo.addItem("None");
        tournamentCombo.setBackground(ThemeManager.getSurface());
        tournamentCombo.setForeground(ThemeManager.getText());
        form.add(tournamentCombo, "growx");

        JButton addBtn = new JButton("Add Transaction");
        addBtn.setFont(Typography.BUTTON);
        addBtn.setBackground(ThemeManager.getAccent());
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> addTransaction());
        form.add(addBtn, "span 2, growx, gaptop 10");

        panel.add(form, "growx, wrap");

        String[] cols = {"ID", "Type", "Amount", "Description", "Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        transactionTable = new JTable(tableModel);
        transactionTable.setBackground(ThemeManager.getSurface());
        transactionTable.setForeground(ThemeManager.getText());
        transactionTable.setFont(Typography.BODY);
        transactionTable.getTableHeader().setBackground(ThemeManager.getSurface());
        transactionTable.getTableHeader().setForeground(ThemeManager.getTextSecondary());
        transactionTable.setGridColor(ThemeManager.getBorder());
        transactionTable.setRowHeight(Spacing.XXL + Spacing.SM);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            "Transactions",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            Typography.CARD_TITLE,
            ThemeManager.getText()
        ));
        scrollPane.getViewport().setBackground(ThemeManager.getSurface());
        panel.add(scrollPane, "grow");

        JPanel btnPanel = new JPanel(new MigLayout("", "[grow][grow]", "[]"));
        btnPanel.setOpaque(false);
        JButton editBtn = new JButton("Edit Selected");
        editBtn.setFont(Typography.BUTTON);
        editBtn.setBackground(ThemeManager.getSurface());
        editBtn.setForeground(ThemeManager.getText());
        editBtn.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
        editBtn.addActionListener(e -> editTransaction());
        btnPanel.add(editBtn, "growx");
        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setFont(Typography.BUTTON);
        deleteBtn.setBackground(ThemeManager.getDanger());
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> deleteTransaction());
        btnPanel.add(deleteBtn, "growx");
        panel.add(btnPanel, "growx, gaptop 5");

        return panel;
    }

    private void addTransaction() {
        try {
            String type = (String) typeCombo.getSelectedItem();
            double amount = Double.parseDouble(amountField.getText().trim());
            String desc = descField.getText().trim();
            Integer tournamentId = null;
            if (tournamentCombo.getSelectedIndex() > 0) {
                // If we had tournaments loaded, we'd extract ID
            }
            financialDAO.addTransaction(type, amount, desc, tournamentId);
            loadTransactions();
            updateSummary();
            updateChart();
            clearForm();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error.");
        }
    }

    private void editTransaction() {
        int row = transactionTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a transaction"); return; }
        JOptionPane.showMessageDialog(this, "Edit not implemented – delete and re-add.");
    }

    private void deleteTransaction() {
        int row = transactionTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a transaction"); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            financialDAO.deleteTransaction(id);
            loadTransactions();
            updateSummary();
            updateChart();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadTransactions() {
        tableModel.setRowCount(0);
        try {
            List<Map<String, Object>> list = financialDAO.getAllTransactions();
            for (Map<String, Object> m : list) {
                tableModel.addRow(new Object[]{
                    m.get("id"), m.get("type"), "$" + String.format("%.2f", (Double)m.get("amount")),
                    m.get("description"), m.get("date").toString()
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void updateSummary() {
        try {
            double rev = financialDAO.getTotalRevenue();
            double exp = financialDAO.getTotalExpenses();
            double profit = rev - exp;
            revenueLabel.setText("$" + NumberFormat.getInstance().format(rev));
            expenseLabel.setText("$" + NumberFormat.getInstance().format(exp));
            profitLabel.setText("$" + NumberFormat.getInstance().format(profit));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateChart() {
        if (chartPanel != null) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            try {
                Map<String, Double[]> data = financialDAO.getMonthlyFinancials();
                for (Map.Entry<String, Double[]> e : data.entrySet()) {
                    dataset.addValue(e.getValue()[0], "Revenue", e.getKey());
                    dataset.addValue(e.getValue()[1], "Expenses", e.getKey());
                }
            } catch (SQLException ex) { ex.printStackTrace(); }
            if (dataset.getRowCount() == 0) {
                dataset.addValue(0, "Revenue", "No Data");
                dataset.addValue(0, "Expenses", "No Data");
            }
            JFreeChart chart = ChartFactory.createBarChart("Monthly Revenue vs Expenses", "Month", "Amount ($)", dataset,
                    PlotOrientation.VERTICAL, true, true, false);
            chart.setBackgroundPaint(ThemeManager.getSurface());
            chart.getTitle().setPaint(ThemeManager.getText());
            chart.getLegend().setItemPaint(ThemeManager.getText());
            chartPanel.setChart(chart);
            chartPanel.setBackground(ThemeManager.getSurface());
        }
    }

    private void clearForm() {
        amountField.setText("");
        descField.setText("");
        typeCombo.setSelectedIndex(0);
    }

    public void refreshTheme() {
        setBackground(ThemeManager.getBackground());

        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                panel.setBackground(ThemeManager.getBackground());
                if (panel.getBorder() instanceof TitledBorder) {
                    panel.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(ThemeManager.getBorder()),
                        ((TitledBorder) panel.getBorder()).getTitle(),
                        TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION,
                        Typography.CARD_TITLE,
                        ThemeManager.getText()
                    ));
                }
            }
            if (comp instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) comp;
                scroll.setBackground(ThemeManager.getBackground());
                scroll.getViewport().setBackground(ThemeManager.getSurface());
                if (scroll.getBorder() instanceof TitledBorder) {
                    scroll.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(ThemeManager.getBorder()),
                        ((TitledBorder) scroll.getBorder()).getTitle(),
                        TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION,
                        Typography.CARD_TITLE,
                        ThemeManager.getText()
                    ));
                }
            }
            if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(ThemeManager.getText());
            }
            if (comp instanceof JComboBox) {
                JComboBox<?> cb = (JComboBox<?>) comp;
                cb.setBackground(ThemeManager.getSurface());
                cb.setForeground(ThemeManager.getText());
            }
            if (comp instanceof JTextField) {
                JTextField tf = (JTextField) comp;
                tf.setBackground(ThemeManager.getSurface());
                tf.setForeground(ThemeManager.getText());
                tf.setCaretColor(ThemeManager.getAccent());
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.getBorder()),
                    BorderFactory.createEmptyBorder(Spacing.SM, Spacing.MD, Spacing.SM, Spacing.MD)
                ));
            }
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                String text = btn.getText();
                if (text != null) {
                    if (text.equals("Add Transaction") || text.equals("Load QR Image & Check‑In")) {
                        btn.setBackground(ThemeManager.getAccent());
                        btn.setForeground(Color.WHITE);
                    } else if (text.equals("Delete Selected")) {
                        btn.setBackground(ThemeManager.getDanger());
                        btn.setForeground(Color.WHITE);
                    } else {
                        btn.setBackground(ThemeManager.getSurface());
                        btn.setForeground(ThemeManager.getText());
                        btn.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
                    }
                }
            }
        }

        if (transactionTable != null) {
            transactionTable.setBackground(ThemeManager.getSurface());
            transactionTable.setForeground(ThemeManager.getText());
            transactionTable.setGridColor(ThemeManager.getBorder());
            transactionTable.getTableHeader().setBackground(ThemeManager.getSurface());
            transactionTable.getTableHeader().setForeground(ThemeManager.getTextSecondary());
        }

        if (chartPanel != null) {
            chartPanel.setBackground(ThemeManager.getSurface());
            updateChart();
        }

        revalidate();
        repaint();
    }
}