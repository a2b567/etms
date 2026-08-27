package com.etms.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provides database connections from runtime configuration.
 * Credentials are intentionally never stored in source control.
 *
 * <p>System properties take precedence over environment variables and the
 * classpath {@code etms.properties} file:</p>
 * <ul>
 *   <li>{@code etms.db.url} / {@code ETMS_DB_URL}</li>
 *   <li>{@code etms.db.user} / {@code ETMS_DB_USER}</li>
 *   <li>{@code etms.db.password} / {@code ETMS_DB_PASSWORD}</li>
 * </ul>
 *
 * <p><strong>Important:</strong> URL automatically includes SSL and schema parameters.</p>
 */
public final class DatabaseConfig {
    private static volatile DatabaseConfig instance;

    private final String url;
    private final String user;
    private final String password;
    private final Properties fileProperties;

    private DatabaseConfig() {
        this.fileProperties = loadFileProperties();
        // Base URL – may or may not include parameters
        String baseUrl = readSetting(
            "etms.db.url",
            "ETMS_DB_URL",
            null
        );
        // Ensure SSL and schema parameters are present
        this.url = appendConnectionParams(baseUrl);
        this.user = readSetting("etms.db.user", "ETMS_DB_USER", "postgres");
        this.password = readSetting(
            "etms.db.password",
            "ETMS_DB_PASSWORD",
            null
        );

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("PostgreSQL JDBC driver is not available.", e);
        }
    }

    private static String appendConnectionParams(String url) {
        if (url == null || url.isBlank()) return url;
        String sslParameters = isLocalDatabase(url)
            ? "sslmode=disable&currentSchema=public"
            : "ssl=true&sslmode=require&currentSchema=public";
        // If no '?' already, add one
        if (!url.contains("?")) {
            return url + "?" + sslParameters;
        }
        // If parameters exist but missing required ones, append them
        StringBuilder sb = new StringBuilder(url);
        if (!url.contains("ssl=") && !url.contains("sslmode=")) {
            sb.append(isLocalDatabase(url) ? "&sslmode=disable" : "&ssl=true&sslmode=require");
        } else if (!url.contains("sslmode=")) {
            sb.append(isLocalDatabase(url) ? "&sslmode=disable" : "&sslmode=require");
        }
        if (!url.contains("currentSchema=")) sb.append("&currentSchema=public");
        return sb.toString();
    }

    private static boolean isLocalDatabase(String url) {
        return url.contains("//localhost:") || url.contains("//127.0.0.1:");
    }

    public static DatabaseConfig getInstance() {
        DatabaseConfig local = instance;
        if (local == null) {
            synchronized (DatabaseConfig.class) {
                local = instance;
                if (local == null) {
                    local = new DatabaseConfig();
                    instance = local;
                }
            }
        }
        return local;
    }

    public Connection getConnection() throws SQLException {
        if (!isConfigured()) {
            throw new SQLException(
                "Database is not configured. Set ETMS_DB_URL, ETMS_DB_USER, and ETMS_DB_PASSWORD."
            );
        }
        return DriverManager.getConnection(url, user, password);
    }

    public boolean isConfigured() {
        return isPresent(url) && isPresent(user) && isPresent(password);
    }

    public static boolean testConnection() {
        try (Connection connection = getInstance().getConnection()) {
            return connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }

    private String readSetting(String property, String envVariable, String defaultValue) {
        String value = System.getProperty(property);
        if (!isPresent(value)) {
            value = System.getenv(envVariable);
        }
        if (!isPresent(value)) {
            value = fileProperties.getProperty(property);
        }
        if (!isPresent(value)) {
            value = defaultValue;
        }
        return value == null ? null : value.trim();
    }

    private static Properties loadFileProperties() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("etms.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read classpath etms.properties.", e);
        }
        return properties;
    }

    private static boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }
}