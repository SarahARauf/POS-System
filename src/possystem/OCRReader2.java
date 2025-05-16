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

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OCRReader2 {

    static {
        // Load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final AtomicBoolean running;
    private Mat capturedFrame;
    private UUID productIDRead;
    public boolean hasScanned = false;
    
    private final BlockingQueue<UUID> productQueue;


    public OCRReader2() {
        running = new AtomicBoolean(true); // Initialize to true
        
        productQueue = new LinkedBlockingQueue<>(); // Thread-safe queue

    }

    public void startScan() {
        VideoCapture capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.err.println("Cannot open camera");
            JOptionPane.showMessageDialog(null, "Scanning Error: Unable to open the camera.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame frame = new JFrame("Real-Time OCR");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label, BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    running.set(false);
                } else if (e.getKeyCode() == KeyEvent.VK_C) {
                    capturedFrame = new Mat();
                    if (capture.read(capturedFrame)) {
                        processCapturedFrame(capturedFrame);
                        performOCR();
                        hasScanned = true;
                    } else {
                        System.err.println("Failed to capture frame.");
                    }
                }
            }
        });

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                running.set(false);
                if (capture.isOpened()) {
                    capture.release();
                }
            }
        });

        // Background thread for capturing and displaying video frames
        SwingWorker<Void, BufferedImage> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                Mat mat = new Mat();
                while (running.get()) {
                    if (!capture.read(mat)) {
                        System.err.println("Cannot read frame");
                        break;
                    }
                    BufferedImage image = matToBufferedImage(mat);
                    if (image != null) {
                        publish(image);
                    }
                }
                return null;
            }

            @Override
            protected void process(java.util.List<BufferedImage> chunks) {
                // Update the JLabel with the latest frame
                BufferedImage image = chunks.get(chunks.size() - 1);
                label.setIcon(new ImageIcon(image));
            }

            @Override
            protected void done() {
                capture.release();
                frame.dispose();
                running.set(false);
            }
        };

        worker.execute();
    }
    
    
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
    
    public void performOCR() {

        try {

            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "src\\ocr\\run_ocr.bat");

            pb.redirectErrorStream(true); // Merge standard error with output

            // Start the process
            Process process = pb.start();

            // Capture the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

//            String productIDReadString = "Product ID not found";
            String productIDReadString = "00000000-0000-0000-0000-000000000000";

            while ((line = reader.readLine()) != null) {
                if (line.contains("Code pattern found:")) {
                    productIDReadString = line.replace("Code pattern found:", "").trim();

                }
            }

            System.out.println("Python Output: " + productIDReadString);

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Python script executed successfully.");
            } else {
                System.err.println("Python script execution failed with exit code: " + exitCode);
            }
            
            
//            this.productIDRead = UUID.fromString(productIDReadString);
            UUID productId = UUID.fromString(productIDReadString);
            productQueue.put(productId);
        } catch (Exception e) {
            e.printStackTrace();

//            return "Error during OCR: " + e.getMessage();
//            this.productIDRead = UUID.fromString("00000000-0000-0000-0000-000000000000");

        }
    }
    
    public UUID getNextProductID() throws InterruptedException
    {
        return  productQueue.take();
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
