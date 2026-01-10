package edu.univ.erp.util;

import org.slf4j.LoggerFactory;


public class Logger {
    private final org.slf4j.Logger logger;
    private static boolean slf4jAvailable = true;

    static {
        try {
            
            LoggerFactory.getLogger(Logger.class);
        } catch (Exception e) {
            slf4jAvailable = false;
            System.err.println("WARNING: SLF4J not available, logging will be disabled: " + e.getMessage());
        }
    }

    private Logger(Class<?> clazz) {
        org.slf4j.Logger tempLogger = null;
        if (slf4jAvailable) {
            try {
                tempLogger = LoggerFactory.getLogger(clazz);
            } catch (Exception e) {
                
                System.err.println("WARNING: Failed to initialize logger for " + clazz.getName() + ": " + e.getMessage());
            }
        }
        this.logger = tempLogger;
    }

    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz);
    }
    
    private void logIfAvailable(String level, String message, Object... args) {
        if (logger != null) {
            try {
                switch (level) {
                    case "info": logger.info(message, args); break;
                    case "warn": logger.warn(message, args); break;
                    case "error": logger.error(message, args); break;
                    case "debug": logger.debug(message, args); break;
                }
            } catch (Exception e) {
                
            }
        }
    }

    public void info(String message) {
        logIfAvailable("info", message);
    }

    public void info(String message, Object... args) {
        logIfAvailable("info", message, args);
    }

    public void error(String message) {
        logIfAvailable("error", message);
    }

    public void error(String message, Throwable throwable) {
        if (logger != null) {
            try {
                logger.error(message, throwable);
            } catch (Exception e) {
                
            }
        }
    }

    public void error(String message, Object... args) {
        logIfAvailable("error", message, args);
    }

    public void warn(String message) {
        logIfAvailable("warn", message);
    }

    public void warn(String message, Object... args) {
        logIfAvailable("warn", message, args);
    }

    public void debug(String message) {
        logIfAvailable("debug", message);
    }

    public void debug(String message, Object... args) {
        logIfAvailable("debug", message, args);
    }
}

