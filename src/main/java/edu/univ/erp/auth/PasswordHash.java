package edu.univ.erp.auth;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHash {
    private static final int ROUNDS = 10;

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(ROUNDS));
    }

    public static boolean verify(String plainPassword, String hash) {
        try {
            return BCrypt.checkpw(plainPassword, hash);
        } catch (Exception e) {
            return false;
        }
    }
}

