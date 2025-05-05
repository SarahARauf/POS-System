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
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OCRReader3 {
    static {
        // Load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final ITesseract tesseract;
    private final ExecutorService executorService;
    private volatile boolean running = true;
    private Mat capturedFrame;

    public OCRReader3() {
        // Initialize Tesseract
        tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Tess4J"); // Adjust the path
        tesseract.setLanguage("eng");

        // Thread pool for processing frames
        executorService = Executors.newSingleThreadExecutor();
    }

    public void startOCR() {
        VideoCapture capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.err.println("Cannot open camera");
            return;
        }

        JFrame frame = new JFrame("Real-Time OCR");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label, BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setVisible(true);

        // Add a key listener for capture and exit functionality
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    running = false;
                } else if (e.getKeyCode() == KeyEvent.VK_C) { // Press 'C' to capture frame
                    capturedFrame = new Mat();
                    if (capture.read(capturedFrame)) {
                        processCapturedFrame(capturedFrame);
                    } else {
                        System.err.println("Failed to capture frame.");
                    }
                }
            }
        });

        Mat mat = new Mat();
        while (running) {
            if (!capture.read(mat)) {
                System.err.println("Cannot read frame");
                break;
            }

            // Convert the Mat frame to BufferedImage
            BufferedImage image = matToBufferedImage(mat);

            // Display the image
            label.setIcon(new ImageIcon(image));
        }

        capture.release();
        frame.dispose();
        executorService.shutdown();
    }

    private void processCapturedFrame(Mat mat) {
        BufferedImage image = matToBufferedImage(mat);

        // Save the captured frame as an image (optional)
        File outputfile = new File("captured_frame.png");
        try {
            javax.imageio.ImageIO.write(image, "png", outputfile);
            System.out.println("Frame saved as: " + outputfile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Perform OCR on the captured image
        executorService.submit(() -> performOCR(image));
    }

    private void performOCR(BufferedImage image) {
        try {
            String text = tesseract.doOCR(image);
            System.out.println("Detected Text: " + text);
        } catch (TesseractException e) {
            System.err.println("Error during OCR: " + e.getMessage());
        }
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        try (InputStream in = new ByteArrayInputStream(byteArray)) {
            return javax.imageio.ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        new OCRReader3().startOCR();
    }
}
