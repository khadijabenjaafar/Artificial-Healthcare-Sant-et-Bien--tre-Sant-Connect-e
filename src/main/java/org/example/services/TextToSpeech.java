package org.example.services;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class TextToSpeech {

  // private static final  String apiKey = "sk_c62d37e42e7c41317589fe56edf866a01fc56ed7b6d675b3";  // 🔐 Remplace par ta clé ElevenLabs
    private static final String voiceId = "TxGEqnHWrfWFTfGW9XjX"; // 🔊 ID de la voix choisie
       // String textToConvert = "My Wonderful Family";
       public static void speak(String textToConvert, String outputFileName) {
           try {
               // Nettoyage du texte
               String safeText = escapeJson(textToConvert);
               URL url = new URL("https://api.elevenlabs.io/v1/text-to-speech/" + voiceId);
               HttpURLConnection connection = (HttpURLConnection) url.openConnection();

               connection.setRequestMethod("POST");
               connection.setRequestProperty("Accept", "audio/mpeg");
               connection.setRequestProperty("Content-Type", "application/json");
              // connection.setRequestProperty("xi-api-key", apiKey);
               connection.setDoOutput(true);

               String jsonInput = "{"
                       + "\"text\": \"" + safeText + "\","
                       + "\"model_id\": \"eleven_monolingual_v1\","
                       + "\"voice_settings\": {"
                       + "    \"stability\": 0.5,"
                       + "    \"similarity_boost\": 0.75"
                       + "  }"
                       + "}";

               try (OutputStream os = connection.getOutputStream()) {
                   byte[] input = jsonInput.getBytes("utf-8");
                   os.write(input, 0, input.length);
               }

               int code = connection.getResponseCode();
               if (code == 200) {
                   try (InputStream in = connection.getInputStream();
                        BufferedInputStream bis = new BufferedInputStream(in);
                        FileOutputStream fos = new FileOutputStream(outputFileName)) {

                       byte[] buffer = new byte[8192];
                       int bytesRead;
                       while ((bytesRead = bis.read(buffer)) != -1) {
                           fos.write(buffer, 0, bytesRead);
                       }
                   }
                   System.out.println("✅ Audio sauvegardé dans " + outputFileName);
               } else {
                   BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                   String line;
                   while ((line = br.readLine()) != null) {
                       System.err.println(line);
                   }
               }

           } catch (Exception e) {
               e.printStackTrace();
           }
       }


    public static String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

