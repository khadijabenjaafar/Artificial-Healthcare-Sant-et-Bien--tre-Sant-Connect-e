package org.example.services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ResumeService {

    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
  //  private static final String API_KEY = "Bearer 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"; // Remplace par ta vraie clé API OpenRouter

    public static String resumerArticle(String texte) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
        //    con.setRequestProperty("Authorization", API_KEY);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Contenu de la requête
            String prompt = "Please summarize the following text in 2-3 sentences:\n\n" + texte;
            String body = "{\n" +
                    "  \"model\": \"gpt-3.5-turbo\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\": \"user\", \"content\": \"" + prompt.replace("\"", "\\\"") + "\"}\n" +
                    "  ]\n" +
                    "}";

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = body.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int status = con.getResponseCode();
            if (status != 200) {
                System.out.println("Erreur HTTP: " + status);
                return "❌ Erreur pendant le résumé.";
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String ligne;
            while ((ligne = in.readLine()) != null) {
                response.append(ligne.trim());
            }
            in.close();

            // Utiliser org.json pour parser proprement
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray choices = jsonResponse.getJSONArray("choices");
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");
            String resume = message.getString("content");

            return resume.trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Erreur pendant le résumé.";
        }
    }





    public static String couperResume(String resume, int maxMots) {
        String[] mots = resume.split("\\s+");
        if (mots.length <= maxMots) {
            return resume;
        }
        StringBuilder court = new StringBuilder();
        for (int i = 0; i < maxMots; i++) {
            court.append(mots[i]).append(" ");
        }
        return court.toString().trim() + "...";
    }











}

