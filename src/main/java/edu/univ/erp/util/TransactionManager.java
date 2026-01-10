package edu.univ.erp.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


public class TransactionManager {
    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    
    public static Connection beginTransaction(DataSource dataSource) throws SQLException {
        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        connectionHolder.set(conn);
        return conn;
    }

    
    public static void commit() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn != null && !conn.isClosed()) {
            conn.commit();
            conn.setAutoCommit(true);
            conn.close();
            connectionHolder.remove();
        }
    }

    
    public static void rollback() {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                
                System.err.println("Error during rollback: " + e.getMessage());
            } finally {
                connectionHolder.remove();
            }
        }
    }

    
    public static Connection getCurrentConnection() {
        return connectionHolder.get();
    }

    
    public static boolean hasActiveTransaction() {
        Connection conn = connectionHolder.get();
        return conn != null;
    }
}

