package edu.univ.erp.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DbConnectionAuth {
    private static HikariDataSource ds;
    private static boolean initialized = false;
    private static String initError = null;

    static {
        initialize();
    }

    private static synchronized void initialize() {
        if (initialized) return;
        
        try (InputStream in = DbConnectionAuth.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties props = new Properties();
            if (in == null) {
                initError = "config.properties not found on classpath";
                System.err.println("ERROR: " + initError);
                return;
            }
            props.load(in);

            HikariConfig config = new HikariConfig();
            
            String url = props.getProperty("db.url");
            if (url != null && url.contains("university_erp")) {
                url = url.replace("university_erp", "auth_db");
            } else {
                url = "jdbc:mysql://localhost:3306/auth_db?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
            }
            
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            
            if (user == null || password == null) {
                initError = "Missing database credentials in config.properties";
                System.err.println("ERROR: " + initError);
                return;
            }
            
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);

            String max = props.getProperty("db.maximumPoolSize");
            if (max != null && !max.trim().isEmpty()) {
                try {
                    config.setMaximumPoolSize(Integer.parseInt(max.trim()));
                } catch (NumberFormatException e) {
                    System.err.println("WARNING: Invalid maximumPoolSize, using default");
                }
            }
            
            String min = props.getProperty("db.minimumIdle");
            if (min != null && !min.trim().isEmpty()) {
                try {
                    config.setMinimumIdle(Integer.parseInt(min.trim()));
                } catch (NumberFormatException e) {
                    System.err.println("WARNING: Invalid minimumIdle, using default");
                }
            }

            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            
            config.setConnectionTimeout(10000); 
            config.setValidationTimeout(5000); 
            
            
            config.setConnectionTestQuery("SELECT 1");

            ds = new HikariDataSource(config);
            
            
            try (Connection testConn = ds.getConnection()) {
                if (testConn != null && testConn.isValid(5)) {
                    initialized = true;
                    System.out.println("Auth database connection pool initialized successfully");
                } else {
                    initError = "Auth database connection test failed";
                    System.err.println("ERROR: " + initError);
                }
            } catch (SQLException e) {
                initError = "Failed to connect to auth database: " + e.getMessage();
                System.err.println("ERROR: " + initError);
                if (ds != null) {
                    ds.close();
                    ds = null;
                }
            }
        } catch (IOException e) {
            initError = "Failed to load Auth DB config: " + e.getMessage();
            System.err.println("ERROR: " + initError);
        } catch (Exception e) {
            initError = "Unexpected error initializing auth database: " + e.getMessage();
            System.err.println("ERROR: " + initError);
            e.printStackTrace();
        }
    }

    private DbConnectionAuth() {}

    public static DataSource getDataSource() {
        if (!initialized || ds == null) {
            if (initError != null) {
                throw new RuntimeException("Auth database not initialized: " + initError);
            }
            
            initialize();
            if (!initialized || ds == null) {
                throw new RuntimeException("Auth database connection pool not available. Please check your database configuration.");
            }
        }
        return ds;
    }
    
    public static boolean isInitialized() {
        return initialized && ds != null;
    }
    
    public static String getInitError() {
        return initError;
    }
}

