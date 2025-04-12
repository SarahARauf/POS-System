/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package possystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.UUID;

public class SelfServicePOSUI2 {

    private JFrame frame;
    private JPanel productPanel;
    private JPanel cartPanel;
    private JTextField totalAmountField;
    private JTextField quantityTextField;
    private JButton addToCartButton;
    private JButton removeFromCartButton;
    private JButton scanItemButton;
    private JButton checkoutButton;
    private POSController controller;
    private UUID selectedProductId;

    public SelfServicePOSUI2() {
        initializeUI();
    }

    // Method to set the controller
    public void setController(POSController controller) {
        this.controller = controller;
    }

    private void initializeUI() {
        // Create the main frame
        frame = new JFrame("Self-Service POS System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Product display area (replacing JTextArea/JList with JPanel)
        productPanel = new JPanel();
        productPanel.setLayout(new GridLayout(0, 3, 10, 10)); // 3 columns, adjustable spacing
        JScrollPane productScrollPane = new JScrollPane(productPanel);
        frame.add(productScrollPane, BorderLayout.CENTER);

        // Cart display area (as JPanel with buttons for cart items)
        cartPanel = new JPanel();
        cartPanel.setLayout(new GridLayout(0, 1, 10, 10)); // Single column
        JScrollPane cartScrollPane = new JScrollPane(cartPanel);
        cartScrollPane.setPreferredSize(new Dimension(200, frame.getHeight())); // Adjust width
        frame.add(cartScrollPane, BorderLayout.EAST);

        // Bottom panel for controls
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        addToCartButton = new JButton("Add to Cart");
        bottomPanel.add(addToCartButton);

        removeFromCartButton = new JButton("Remove from Cart");
        bottomPanel.add(removeFromCartButton);

        quantityTextField = new JTextField("Quantity", 10);
        quantityTextField.setEditable(true);
        bottomPanel.add(quantityTextField);

        scanItemButton = new JButton("Scan Item");
        bottomPanel.add(scanItemButton);

        bottomPanel.add(new JLabel("Total Amount:"));
        totalAmountField = new JTextField(10);
        totalAmountField.setEditable(false);
        bottomPanel.add(totalAmountField);

        checkoutButton = new JButton("Checkout");
        bottomPanel.add(checkoutButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Display the frame
        frame.setVisible(true);
    }

    // Display available products using buttons with images
    public void displayProducts(List<Product> products) {
        productPanel.removeAll(); // Clear existing buttons

        for (Product product : products) {
            // Create button for each product
            JButton productButton = new JButton();
            productButton.setLayout(new BorderLayout());

            // Set product image (use a placeholder if no image is available)
            ImageIcon productImage = new ImageIcon("MoleIMG.png");
            Image scaledImage = productImage.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            productButton.setIcon(new ImageIcon(scaledImage));

            // Add product name and price as label below the image
            JLabel productLabel = new JLabel("<html>" + product.getName() + "<br>$" + product.getPrice() + "</html>");
            productLabel.setHorizontalAlignment(SwingConstants.CENTER);
            productButton.add(productLabel, BorderLayout.SOUTH);

            // Store product ID in the button for selection handling
            productButton.putClientProperty("productId", product.getProductId());

            // Add action listener to handle button clicks
            productButton.addActionListener(e -> {
                selectedProductId = (UUID) productButton.getClientProperty("productId");
                JOptionPane.showMessageDialog(frame, "Selected Product ID: " + selectedProductId, "Product Selected", JOptionPane.INFORMATION_MESSAGE);
            });

            productPanel.add(productButton);
        }

        productPanel.revalidate();
        productPanel.repaint();
    }

    // Display the current cart using buttons
    public void showCart(Sale sale) {
        cartPanel.removeAll(); // Clear existing items

        for (SaleItem item : sale.getItems()) {
            JButton cartButton = new JButton(item.getProduct().getName() + " - $" + item.getUnitPrice() + " x " + item.getQuantity());
            cartButton.putClientProperty("productId", item.getProduct().getProductId());

            cartButton.addActionListener(e -> {
                selectedProductId = (UUID) cartButton.getClientProperty("productId");
                JOptionPane.showMessageDialog(frame, "Selected Cart Item ID: " + selectedProductId, "Cart Item Selected", JOptionPane.INFORMATION_MESSAGE);
            });

            cartPanel.add(cartButton);
        }

        sale.calculateTotal();
        totalAmountField.setText("$" + sale.getTotalAmount().toString());

        cartPanel.revalidate();
        cartPanel.repaint();
    }

    public static void main(String[] args) {
        SelfServicePOSUI test = new SelfServicePOSUI();
    }
}