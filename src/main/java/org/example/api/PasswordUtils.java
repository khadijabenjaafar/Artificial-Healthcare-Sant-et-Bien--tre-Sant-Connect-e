package org.example.api;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Hachage du mot de passe
    public static String hashPassword(String plainPassword) {
        // 13 = le "cost" utilisé par Symfony (cf. ton hash $2y$13$...)
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(13));
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        // Conversion de $2y$ (Symfony) en $2a$ (compatible jBCrypt)
        if (hashedPassword.startsWith("$2y$")) {
            hashedPassword = "$2a$" + hashedPassword.substring(4);
        }

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

