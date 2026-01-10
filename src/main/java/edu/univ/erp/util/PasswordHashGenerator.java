package edu.univ.erp.util;
import org.mindrot.jbcrypt.BCrypt;
public class PasswordHashGenerator {
    public static void main(String[] args) {
        String[] passwords = {"admin123", "inst123", "stu123"};
        System.out.println("Password Hashes for seed data:");
        System.out.println("==============================");
        for (String pwd : passwords) {
            String hash = BCrypt.hashpw(pwd, BCrypt.gensalt(10));
            System.out.println("Password: " + pwd + " -> Hash: " + hash);
        }
    }
}

