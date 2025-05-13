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

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class OCRReader {

    static {
        // Load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final ExecutorService executorService;
//    private volatile boolean running = true;
    private final AtomicBoolean running;
    private Mat capturedFrame;

    public OCRReader() {

        // Thread pool for processing frames
        executorService = Executors.newSingleThreadExecutor();

        running = new AtomicBoolean(true); // Initialize to true
    }

    public void startScan() {
        VideoCapture capture = new VideoCapture(0);
        System.out.println("OCRReader - print 1");
        if (!capture.isOpened()) {
            System.err.println("Cannot open camera");
            return;
        }
        System.out.println("OCRReader - print 2");

        JFrame frame = new JFrame("Real-Time OCR");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label, BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setVisible(true);
        
        
        

        System.out.println("OCRReader - print 3");

        // Add a key listener for capture and exit functionality
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.out.println("OCRReader - print ESCAPE");

//                    running = false;
                    running.set(false); // Safely update the flag
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

        System.out.println("OCRReader - print 4");

        Mat mat = new Mat();
        while (running.get()) {
            if (!capture.read(mat)) {
                System.err.println("Cannot read frame");
                break;
            }

            System.out.println("OCRReader - print 5");

            // Convert the Mat frame to BufferedImage
            BufferedImage image = matToBufferedImage(mat);

            System.out.println("OCRReader - print 6");

            // Display the image
//            label.setIcon(new ImageIcon(image));

            SwingUtilities.invokeLater(() -> {
                label.setIcon(new ImageIcon(image));
            });

            System.out.println("OCRReader - print 7");

        }

        System.out.println("OCRReader - print 8");

        capture.release();
        frame.dispose();
        executorService.shutdown();

        System.out.println("OCRReader - print 9");

    }

    private void processCapturedFrame(Mat mat) {
        BufferedImage image = matToBufferedImage(mat);

        File outputfile = new File("src\\ocr\\captured_frame.png");
        try {
            javax.imageio.ImageIO.write(image, "png", outputfile);
            System.out.println("Frame saved as: " + outputfile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        performOCR();
    }

    public UUID performOCR() {

        try {

            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "src\\ocr\\run_ocr.bat");

            pb.redirectErrorStream(true); // Merge standard error with output

            // Start the process
            Process process = pb.start();

            // Capture the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            String productIDRead = "Product ID not found";
            while ((line = reader.readLine()) != null) {
                if (line.contains("Code pattern found:")) {
                    productIDRead = line.replace("Code pattern found:", "").trim();

                }
            }

            System.out.println("Python Output: " + productIDRead);

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Python script executed successfully.");
            } else {
                System.err.println("Python script execution failed with exit code: " + exitCode);
            }

            return UUID.fromString(productIDRead);
        } catch (Exception e) {
            e.printStackTrace();

//            return "Error during OCR: " + e.getMessage();
            return UUID.fromString("00000000-0000-0000-0000-000000000000");

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
        new OCRReader().startScan();
    }
}









///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package possystem;
//
///**
// *
// * @author Sarah
// */
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfByte;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.videoio.VideoCapture;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.KeyAdapter;
//import java.awt.event.KeyEvent;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.InputStream;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.UUID;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//public class OCRReader {
//
//    static {
//        // Load the OpenCV native library
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//    }
//
//    private final ExecutorService executorService;
////    private volatile boolean running = true;
//    private final AtomicBoolean running;
//    private Mat capturedFrame;
//
//    public OCRReader() {
//
//        // Thread pool for processing frames
//        executorService = Executors.newSingleThreadExecutor();
//
//        running = new AtomicBoolean(true); // Initialize to true
//    }
//
//    public void startScan(JPanel frame) {
//        VideoCapture capture = new VideoCapture(0);
//        System.out.println("OCRReader - print 1");
//        if (!capture.isOpened()) {
//            System.err.println("Cannot open camera");
//            return;
//        }
//        System.out.println("OCRReader - print 2");
//
////        JFrame frame = new JFrame("Real-Time OCR");
////        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        JLabel label = new JLabel();
//        label.setHorizontalAlignment(SwingConstants.CENTER);
//        frame.add(label, BorderLayout.CENTER);
//        frame.setSize(800, 600);
//        frame.setVisible(true);
//        
//        
//        
//
//        System.out.println("OCRReader - print 3");
//
//        // Add a key listener for capture and exit functionality
//        frame.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
//                    System.out.println("OCRReader - print ESCAPE");
//
////                    running = false;
//                    running.set(false); // Safely update the flag
//                } else if (e.getKeyCode() == KeyEvent.VK_C) { // Press 'C' to capture frame
//                    capturedFrame = new Mat();
//                    if (capture.read(capturedFrame)) {
//                        processCapturedFrame(capturedFrame);
//                    } else {
//                        System.err.println("Failed to capture frame.");
//                    }
//                }
//            }
//        });
//
//        System.out.println("OCRReader - print 4");
//
//        Mat mat = new Mat();
//        while (running.get()) {
//            if (!capture.read(mat)) {
//                System.err.println("Cannot read frame");
//                break;
//            }
//
//            System.out.println("OCRReader - print 5");
//
//            // Convert the Mat frame to BufferedImage
//            BufferedImage image = matToBufferedImage(mat);
//
//            System.out.println("OCRReader - print 6");
//
//            // Display the image
////            label.setIcon(new ImageIcon(image));
//
//            SwingUtilities.invokeLater(() -> {
//                label.setIcon(new ImageIcon(image));
//            });
//
//            System.out.println("OCRReader - print 7");
//
//        }
//
//        System.out.println("OCRReader - print 8");
//
//        capture.release();
////        frame.dispose();
//        executorService.shutdown();
//
//        System.out.println("OCRReader - print 9");
//
//    }
//
//    private void processCapturedFrame(Mat mat) {
//        BufferedImage image = matToBufferedImage(mat);
//
//        File outputfile = new File("src\\ocr\\captured_frame.png");
//        try {
//            javax.imageio.ImageIO.write(image, "png", outputfile);
//            System.out.println("Frame saved as: " + outputfile.getAbsolutePath());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
////        performOCR();
//    }
//
//    public UUID performOCR() {
//
//        try {
//
//            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "src\\ocr\\run_ocr.bat");
//
//            pb.redirectErrorStream(true); // Merge standard error with output
//
//            // Start the process
//            Process process = pb.start();
//
//            // Capture the output
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//
//            String productIDRead = "Product ID not found";
//            while ((line = reader.readLine()) != null) {
//                if (line.contains("Code pattern found:")) {
//                    productIDRead = line.replace("Code pattern found:", "").trim();
//
//                }
//            }
//
//            System.out.println("Python Output: " + productIDRead);
//
//            // Wait for the process to complete
//            int exitCode = process.waitFor();
//            if (exitCode == 0) {
//                System.out.println("Python script executed successfully.");
//            } else {
//                System.err.println("Python script execution failed with exit code: " + exitCode);
//            }
//
//            return UUID.fromString(productIDRead);
//        } catch (Exception e) {
//            e.printStackTrace();
//
////            return "Error during OCR: " + e.getMessage();
//            return UUID.fromString("00000000-0000-0000-0000-000000000000");
//
//        }
//    }
//
//    private BufferedImage matToBufferedImage(Mat mat) {
//        MatOfByte matOfByte = new MatOfByte();
//        Imgcodecs.imencode(".jpg", mat, matOfByte);
//        byte[] byteArray = matOfByte.toArray();
//        try (InputStream in = new ByteArrayInputStream(byteArray)) {
//            return javax.imageio.ImageIO.read(in);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
////    public static void main(String[] args) {
////        new OCRReader().startScan();
////    }
//}
