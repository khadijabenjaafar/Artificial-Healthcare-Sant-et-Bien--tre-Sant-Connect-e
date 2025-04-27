package org.example.api;

import java.io.*;

public class FaceID {

    public static boolean verifyFace(String newImagePath, String storedImagePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "faceid.py", newImagePath, storedImagePath);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder jsonResult = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonResult.append(line);
            }

            process.waitFor();

            // Analyser la sortie JSON
            String result = jsonResult.toString();
            return result.contains("\"match\": true");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

