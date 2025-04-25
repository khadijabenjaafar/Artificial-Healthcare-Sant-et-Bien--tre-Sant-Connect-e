package org.example.api;

import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;

public class CameraCapture {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // charge OpenCV
    }

    public static String captureFace(String fileName) {
        String outputPath = "faces/" + fileName;
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.out.println("Erreur : Webcam non détectée");
            return null;
        }

        Mat frame = new Mat();
        camera.read(frame);
        if (!frame.empty()) {
            Imgcodecs.imwrite(outputPath, frame);
            System.out.println("Image sauvegardée : " + outputPath);
        }

        camera.release();
        return outputPath;
    }
}

