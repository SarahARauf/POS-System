/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package possystem;

/**
 *
 * @author Sarah
 */
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

public class OCRReader2 {
    public static void main(String[] args) {
        // Load the OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Check if OpenCV is properly loaded
        System.out.println("OpenCV version: " + Core.VERSION);

        // Create a VideoCapture object to access the webcam
        VideoCapture capture = new VideoCapture(0); // 0 for default camera

        // Check if the camera opened successfully
        if (!capture.isOpened()) {
            System.out.println("Error: Cannot open the camera!");
            return;
        }

        // Create a matrix to hold the frames
        Mat frame = new Mat();

        System.out.println("Press 'ESC' to exit...");

        // Continuously capture and display frames
        while (true) {
            // Read the frame from the camera
            if (capture.read(frame)) {
                // Display the frame
                HighGui.imshow("Camera Feed", frame);

                // Exit on 'ESC' key press
                int key = HighGui.waitKey(1); // Wait for 1ms for a key press
                if (key == 27) { // 27 is the ASCII code for ESC
                    System.out.println("ESC key pressed. Exiting...");
                    break;
                }
            } else {
                System.out.println("Error: Cannot read the frame!");
                break;
            }
        }

        // Release resources
        capture.release();
        HighGui.destroyAllWindows();
        System.exit(0); // Ensure the program exits completely

        System.out.println("ESC key pressed. Exiting...");
    }
}
