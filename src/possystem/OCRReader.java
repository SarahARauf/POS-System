/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package possystem;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;


import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
/**
 *
 * @author Sarah
 */
public class OCRReader {
    public static void main(String[] args) {
        // Create a Tesseract instance
        ITesseract tesseract = new Tesseract();

        // Set the path to tessdata folder
        tesseract.setDatapath("C:\\Tess4J"); // Update path
        tesseract.setLanguage("eng"); // Language for OCR (English)

        try {
            // Provide an image file for OCR
            File imageFile = new File("C:\\Tess4J\\tessdata\\sample.png"); // Replace with your image path
            String text = tesseract.doOCR(imageFile);

            // Display the extracted text
            System.out.println("Extracted Text: ");
            System.out.println(text);
        } catch (TesseractException e) {
            System.err.println("Error while performing OCR: " + e.getMessage());
        }
    }

    void setVisible(boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
}
