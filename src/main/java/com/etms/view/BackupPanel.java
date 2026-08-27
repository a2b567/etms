package com.etms.view;

import com.etms.config.DatabaseConfig;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BackupPanel extends JPanel {

    private JTextArea logArea;
    private JProgressBar progressBar;
    private JButton backupBtn, restoreBtn;
    private JFileChooser fileChooser;

    public BackupPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));

        JLabel title = new JLabel("Backup & Restore");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(title, "wrap, gapbottom 20");

        // Buttons
        JPanel btnPanel = new JPanel(new MigLayout("fillx", "[grow][grow]", "[]"));
        backupBtn = new JButton("💾 Backup Now");
        backupBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backupBtn.addActionListener(e -> startBackup());
        btnPanel.add(backupBtn, "growx");

        restoreBtn = new JButton("📂 Restore from File");
        restoreBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        restoreBtn.addActionListener(e -> startRestore());
        btnPanel.add(restoreBtn, "growx");

        add(btnPanel, "growx, wrap, gapbottom 10");

        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        add(progressBar, "growx, wrap, gapbottom 10");

        // Log area
        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Log"));
        add(scrollPane, "grow");

        fileChooser = new JFileChooser();
    }

    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    /** Starts the backup process in a background thread. */
    private void startBackup() {
        backupBtn.setEnabled(false);
        restoreBtn.setEnabled(false);
        progressBar.setValue(0);
        log("Starting backup...");

        SwingWorker<File, String> worker = new SwingWorker<>() {
            @Override
            protected File doInBackground() throws Exception {
                // Create backup directory
                File backupDir = new File("backups");
                if (!backupDir.exists()) backupDir.mkdirs();

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                File backupFile = new File(backupDir, "etms_backup_" + timestamp + ".sql");

                try (Connection conn = DatabaseConfig.getInstance().getConnection();
                     PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                             new FileOutputStream(backupFile), StandardCharsets.UTF_8))) {

                    // Write header
                    writer.println("-- ETMS Backup created at " + LocalDateTime.now());
                    writer.println("-- Database: " + conn.getCatalog());

                    DatabaseMetaData meta = conn.getMetaData();
                    // Get all tables in the public schema (PostgreSQL)
                    List<String> tables = new ArrayList<>();
                    try (ResultSet rs = meta.getTables(null, "public", "%", new String[]{"TABLE"})) {
                        while (rs.next()) {
                            String tableName = rs.getString("TABLE_NAME");
                            tables.add(tableName);
                        }
                    }

                    publish("Found " + tables.size() + " tables.");

                    int totalTables = tables.size();
                    int processed = 0;

                    for (String table : tables) {
                        // Skip system tables if any, but we only have our own.
                        publish("Backing up table: " + table);
                        int rowCount = 0;
                        // Get column names
                        List<String> columns = new ArrayList<>();
                        try (Statement stmt = conn.createStatement();
                             ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + " LIMIT 0")) {
                            ResultSetMetaData rsmd = rs.getMetaData();
                            int colCount = rsmd.getColumnCount();
                            for (int i = 1; i <= colCount; i++) {
                                columns.add(rsmd.getColumnName(i));
                            }
                        }

                        // Now fetch all rows
                        try (Statement stmt = conn.createStatement();
                             ResultSet rs = stmt.executeQuery("SELECT * FROM " + table)) {
                            while (rs.next()) {
                                StringBuilder sb = new StringBuilder("INSERT INTO ");
                                sb.append(table).append(" (");
                                for (int i = 0; i < columns.size(); i++) {
                                    if (i > 0) sb.append(", ");
                                    sb.append(columns.get(i));
                                }
                                sb.append(") VALUES (");
                                for (int i = 0; i < columns.size(); i++) {
                                    if (i > 0) sb.append(", ");
                                    Object val = rs.getObject(i + 1);
                                    if (val == null) {
                                        sb.append("NULL");
                                    } else if (val instanceof String || val instanceof java.sql.Timestamp || val instanceof java.sql.Date || val instanceof java.time.LocalDateTime) {
                                        sb.append("'").append(val.toString().replace("'", "''")).append("'");
                                    } else if (val instanceof Boolean) {
                                        sb.append(((Boolean) val) ? "TRUE" : "FALSE");
                                    } else {
                                        sb.append(val.toString());
                                    }
                                }
                                sb.append(");");
                                writer.println(sb.toString());
                                rowCount++;
                            }
                        }
                        publish("Table " + table + ": " + rowCount + " rows backed up.");
                        processed++;
                        setProgress((int) ((processed / (double) totalTables) * 100));
                    }

                    writer.flush();
                    publish("Backup completed successfully: " + backupFile.getAbsolutePath());
                    return backupFile;
                }
            }

            @Override
            protected void process(List<String> chunks) {
                for (String msg : chunks) {
                    log(msg);
                }
            }

            @Override
            protected void done() {
                try {
                    File result = get();
                    log("Backup file saved: " + result.getAbsolutePath());
                    JOptionPane.showMessageDialog(BackupPanel.this,
                            "Backup created successfully:\n" + result.getAbsolutePath(),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException | ExecutionException e) {
                    log("Backup failed: " + e.getMessage());
                    JOptionPane.showMessageDialog(BackupPanel.this,
                            "Backup failed: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    backupBtn.setEnabled(true);
                    restoreBtn.setEnabled(true);
                    progressBar.setValue(0);
                }
            }
        };
        worker.execute();
    }

    /** Starts the restore process from a selected .sql file. */
    private void startRestore() {
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) return;

        File selectedFile = fileChooser.getSelectedFile();
        backupBtn.setEnabled(false);
        restoreBtn.setEnabled(false);
        progressBar.setValue(0);
        log("Starting restore from: " + selectedFile.getAbsolutePath());

        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (Connection conn = DatabaseConfig.getInstance().getConnection();
                     BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {

                    // We need to execute statements. We'll read line by line and execute.
                    // But SQL statements may span multiple lines. We'll assume each INSERT is on a single line.
                    String line;
                    int totalLines = 0;
                    int executedLines = 0;

                    // First count lines for progress (approximate)
                    while ((line = reader.readLine()) != null) totalLines++;
                    reader.close();

                    // Re-open to read again
                    BufferedReader reader2 = new BufferedReader(new FileReader(selectedFile));
                    while ((line = reader2.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("--")) continue; // skip comments
                        // Execute statement
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute(line);
                        } catch (SQLException ex) {
                            // Log error but continue
                            publish("Error executing: " + line.substring(0, Math.min(80, line.length())) + "... -> " + ex.getMessage());
                        }
                        executedLines++;
                        setProgress((int) ((executedLines / (double) totalLines) * 100));
                    }
                    reader2.close();
                    publish("Restore completed. Executed " + executedLines + " statements.");
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String msg : chunks) {
                    log(msg);
                }
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(BackupPanel.this,
                            "Restore completed successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException | ExecutionException e) {
                    log("Restore failed: " + e.getMessage());
                    JOptionPane.showMessageDialog(BackupPanel.this,
                            "Restore failed: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    backupBtn.setEnabled(true);
                    restoreBtn.setEnabled(true);
                    progressBar.setValue(0);
                }
            }
        };
        worker.execute();
    }
}