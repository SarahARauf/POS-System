/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package possystem;

/**
 *
 * @author Sarah
 */
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelfServicePOSUI {

    private JFrame frame;
    private JPanel productDisplayArea;
    private JList<CartItem> cartDisplayArea;
    private JTextField totalAmountField;
    private JTextField quantityTextField;
    private JButton addToCartButton;
    private JButton removeFromCartButton;
    private JButton scanItemButton;
    private JButton checkoutButton;
    private POSController controller;
    private UUID selectedProductId;
    private OCRReader ocrReader;

    /* 
    Initializing the UI
    Displaying available products
    Display cart (sale)
    Prompts payment
    Displays receipt
     */
    protected class CartItem {

        private String displayText; // Text to display in the UI
        private UUID productId;     // The associated product ID

        public CartItem(String displayText, UUID productId) {
            this.displayText = displayText;
            this.productId = productId;
        }

        public UUID getProductId() {
            return productId;
        }

        @Override
        public String toString() {
            return displayText; // This is what will be shown in the JList
        }
    }

    public SelfServicePOSUI() {
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

        // Product display area
        productDisplayArea = new JPanel();
        productDisplayArea.setLayout(new GridLayout(0, 3, 10, 10));
        JScrollPane productScrollPane = new JScrollPane(productDisplayArea);
        frame.add(productScrollPane, BorderLayout.CENTER);

        // Cart display area with fixed size
        cartDisplayArea = new JList<>();
        cartDisplayArea.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartDisplayArea.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                CartItem selectedItem = cartDisplayArea.getSelectedValue();
                if (selectedItem != null) {
                    selectedProductId = selectedItem.getProductId(); // Access the productId
                    JOptionPane.showMessageDialog(frame, "Selected Product ID: " + selectedProductId, "Cart Item Selected", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        JScrollPane cartScrollPane = new JScrollPane(cartDisplayArea);
        // Set a preferred size for the cart pane
        cartScrollPane.setPreferredSize(new Dimension(200, frame.getHeight())); // Adjust the width as needed
        frame.add(cartScrollPane, BorderLayout.EAST);


        // Total amount field
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Add to cart button -> Trigger addItem(UUID productId, int quantity) in POSController
//        addToCartButton = new JButton("Add to Cart");
//        bottomPanel.add(addToCartButton);
        // Remove from cart button -> Trigger removeItem(UUID productId, int quantity) in POSController
        removeFromCartButton = new JButton("Remove from Cart");
        bottomPanel.add(removeFromCartButton);
        removeFromCartButton.addActionListener(new RemoveFromCartListener());

//        // Will work alongside addToCartButton and removeFromCartButton to add/remove quantities from cart
//        quantityTextField = new JTextField("Loyalty ID", 10);
//        quantityTextField.setEditable(true);
//        bottomPanel.add(quantityTextField);

        // Leave this here, will implement once I have figured out how to use computer vision for scanning
        scanItemButton = new JButton("Scan Item");
        bottomPanel.add(scanItemButton);
        scanItemButton.addActionListener(new ScanItemListener());
        
        bottomPanel.add(new JLabel("Total Amount:"));
        totalAmountField = new JTextField(10);
        totalAmountField.setEditable(false);
        bottomPanel.add(totalAmountField);

        //Trigger processPayment() in POSController
        checkoutButton = new JButton("Checkout");
        bottomPanel.add(checkoutButton);
        checkoutButton.addActionListener(new CheckOutListener());

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Display the frame
        frame.setVisible(true);

    }

    // Display available products, called in POSController
    public void displayProducts(List<Product> products) {
        StringBuilder productList = new StringBuilder("Available Products:\n");
        for (Product product : products) {
            JButton productButton = new JButton();
            productButton.setLayout(new BorderLayout());

            //Set product image
//            Image image = new ImageIcon(this.getClass().getResource("MoleIMG.png")).getImage();
            Image image = new ImageIcon(product.getImgPath()).getImage();
            Image scaledImage = image.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
            productButton.setIcon(new ImageIcon(scaledImage));

            //Add product name and price as label below the img
            JLabel productLabel = new JLabel("<html>" + product.getName() + "<br>$" + product.getPrice() + "</html>");
            productLabel.setHorizontalAlignment(SwingConstants.CENTER);
            productButton.add(productLabel, BorderLayout.SOUTH);

            //Store product ID in the button for selection handling
            productButton.putClientProperty("productId", product.getProductId());

            // Add action listener to handle button clicks
            productButton.addActionListener(e -> {
                selectedProductId = (UUID) productButton.getClientProperty("productId");
                controller.addItem(selectedProductId,1);
                JOptionPane.showMessageDialog(frame, "Selected Product ID: " + selectedProductId, "Product Selected", JOptionPane.INFORMATION_MESSAGE);
            });

            productDisplayArea.add(productButton);
        }
        
        
    }


    // Display the current cart called in POSController
    public void showCart(Sale sale) {
        DefaultListModel<CartItem> cartListModel = new DefaultListModel<>();
        for (SaleItem item : sale.getItems()) {
            String displayText = item.getProduct().getName() + " - $" + item.getUnitPrice() + " x " + item.getQuantity();
            CartItem cartItem = new CartItem(displayText, item.getProduct().getProductId()); // Store display text and productId
            cartListModel.addElement(cartItem);
        }
        sale.calculateTotal();
        cartDisplayArea.setModel(cartListModel);
        totalAmountField.setText("$" + sale.getTotalAmount().toString()); 
    }
    
    

    // Prompt for payment information called in PosController
    public PaymentDetails promptPayment() {

        //Popup panel for entering payment info
        JPanel paymentPanel = new JPanel(new GridLayout(4, 2));
        JTextField cardNumberField = new JTextField();
        JTextField expiryDateField = new JTextField();
        JTextField cvvField = new JTextField();
        JTextField loyaltyMemberField = new JTextField();

        paymentPanel.add(new JLabel("Card Number:"));
        paymentPanel.add(cardNumberField);
        paymentPanel.add(new JLabel("Expiry Date (MM/YY):"));
        paymentPanel.add(expiryDateField);
        paymentPanel.add(new JLabel("CVV:"));
        paymentPanel.add(cvvField);
        paymentPanel.add(new JLabel("Loyalty Member No:"));
        paymentPanel.add(loyaltyMemberField);
        

        int result = JOptionPane.showConfirmDialog(
                frame,
                paymentPanel,
                "Enter Payment Details",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        //If user presses OK, then create a new PaymentDetail Object
        if (result == JOptionPane.OK_OPTION) {

            if (!loyaltyMemberField.getText().isBlank()){
                controller.applyLoyaltyDiscount(UUID.fromString(loyaltyMemberField.getText()));
            }
            
            //Clear cart after purchasing
            
//            cartDisplayArea.repaint();
//            cartDisplayArea.revalidate();
//            cartDisplayArea.
            
            return new PaymentDetails(
                    cardNumberField.getText(),
                    expiryDateField.getText(),
                    cvvField.getText()
            );
        }
        return null; // User canceled the payment
    }

    // Display the receipt, called in the POSController
    public void showReceipt(Sale sale, String customerName) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("Receipt\n");
        receipt.append("Customer: ").append(customerName).append("\n");
        receipt.append("Date/Time: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        receipt.append("\nItems Purchased:\n");

        for (SaleItem item : sale.getItems()) {
            receipt.append(item.getProduct().getName())
                    .append(" - $")
                    .append(item.getUnitPrice())
                    .append(" x ")
                    .append(item.getQuantity())
                    .append("\n");
        }

        receipt.append("\nTotal Amount: $").append(sale.getTotalAmount()).append("\n");
        receipt.append("Thank you for your purchase!");

        JOptionPane.showMessageDialog(frame, receipt.toString(), "Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    // Getter for the main frame
    public JFrame getFrame() {
        return frame;
    }

//    //Not being used
//    public UUID getSelectedProductId(String selectedText) {
//        // Retrieve the selected text from the provided text area
////        String selectedText = textArea.getSelectedText();
////        System.out.println(selectedText);
//        if (selectedText != null) {
//            try {
//                // Define the regex pattern to extract UUID
//                String regex = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
//                Pattern pattern = Pattern.compile(regex);
//                Matcher matcher = pattern.matcher(selectedText);
//
//                // Check if a match is found
//                if (matcher.find()) {
//                    // Extract and return the UUID
//                    return UUID.fromString(matcher.group(0));
//                } else {
//                    // Show an error message if no valid UUID is found
//                    JOptionPane.showMessageDialog(null, "No valid product ID found in the selection.", "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            } catch (IllegalArgumentException ex) {
//                // Show an error message if the UUID format is invalid
//                JOptionPane.showMessageDialog(null, "Invalid product selection.", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        } else {
//            // Show an error message if no text is selected
//            JOptionPane.showMessageDialog(null, "No text selected.", "Error", JOptionPane.ERROR_MESSAGE);
//        }
//        return null; // No valid selection
//    }

//    private class AddToCartListener implements ActionListener {
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            try {
//                int quantity = Integer.parseInt(quantityTextField.getText());
//                controller.addItem(selectedProductId, quantity);
//                System.out.println(selectedProductId);
//            } catch (NumberFormatException ex) {
//                JOptionPane.showMessageDialog(frame, "Invalid quantity", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//
//        }
//    }
    
    private class ScanItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            try{
                
                
            } catch(Exception ex) {
   
                JOptionPane.showMessageDialog(frame, "Scanning Error", "Error", JOptionPane.ERROR_MESSAGE);

            }
        }
    }

    private class RemoveFromCartListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                System.out.println("RemoveFromCart Listener");
                System.out.println(selectedProductId);
                controller.removeItem(selectedProductId, 1);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid quantity", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private class CheckOutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            controller.processPayment();
        }
    }

    public static void main(String[] args) {

        SelfServicePOSUI test = new SelfServicePOSUI();

    }
}
