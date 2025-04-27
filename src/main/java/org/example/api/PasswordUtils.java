package org.example.api;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Hachage du mot de passe
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Vérification du mot de passe (saisi vs haché stocké)
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
    public static String generateTempPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#&!";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        return sb.toString();
    }
}

