package org.example.services;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TranslationServiceSimple  {

    public static String traduire(String texte, String targetLang) {

        try {
            String encodedText = URLEncoder.encode(texte, "UTF-8");
            String urlStr = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl="
                    + targetLang + "&dt=t&q=" + encodedText;

            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String ligne;
            while ((ligne = in.readLine()) != null) {
                response.append(ligne);
            }
            in.close();

            // Parsing simple pour récupérer le premier élément traduit
            String json = response.toString();
            int start = json.indexOf("\"");
            int end = json.indexOf("\"", start + 1);
            if (start != -1 && end != -1) {
                return json.substring(start + 1, end);
            } else {
                return "❌ No translation found.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error during translation.";
        }
    }

    // 2. Traduire un texte long (plusieurs phrases)
    public static String traduireTexteLong(String texteComplet, String targetLang) {
        String[] phrases = texteComplet.split("(?<=[.!?])\\s+"); // coupe après les points
        StringBuilder resultat = new StringBuilder();

        for (String phrase : phrases) {
            String traduit = traduire(phrase.trim(), targetLang);
            resultat.append(traduit).append(" ");
            try {
                Thread.sleep(300); // pause pour éviter d'être bloqué
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return resultat.toString().trim();
    }



    public static String decodeUnicode(String unicodeText) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < unicodeText.length()) {
            char c = unicodeText.charAt(i++);
            if (c == '\\' && i < unicodeText.length() && unicodeText.charAt(i) == 'u') {
                i++;
                int code = Integer.parseInt(unicodeText.substring(i, i + 4), 16);
                result.append((char) code);
                i += 4;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

}







