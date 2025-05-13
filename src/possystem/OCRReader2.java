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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class OCRReader2 {

    static {
        // Load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final AtomicBoolean running;
    private Mat capturedFrame;

    public OCRReader2() {
        running = new AtomicBoolean(true); // Initialize to true
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
