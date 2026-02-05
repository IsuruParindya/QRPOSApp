package et.qrscanner.app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Map;

public class QRPOSApp extends JFrame {
    private final JTextField scanField = new JTextField(20);
    private final JTextField nameField = new JTextField(20);
    private final JTextField priceField = new JTextField(10);
    private final JLabel status = new JLabel("Ready");
    private final JPanel previewPanel = new JPanel();
    private final Map<String, Product> products;

    private BufferedImage lastLabel;

    public QRPOSApp() {
        super("QR POS App");

        products = Database.loadProducts();

        nameField.setEditable(false);
        priceField.setEditable(false);

        JPanel left = new JPanel(new GridBagLayout());
        left.setBorder(new EmptyBorder(10,10,10,10));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        int row = 0;
        c.gridx=0; c.gridy=row; left.add(new JLabel("Scan QR:"), c);
        c.gridx=1; left.add(scanField,c);

        row++;
        c.gridx=0; c.gridy=row; left.add(new JLabel("Product:"), c);
        c.gridx=1; left.add(nameField,c);

        row++;
        c.gridx=0; c.gridy=row; left.add(new JLabel("Price:"), c);
        c.gridx=1; left.add(priceField,c);

        row++;
        c.gridx=0; c.gridy=row; c.gridwidth=2; left.add(status,c);
        c.gridwidth=1;

        setLayout(new GridLayout(1,2));
        add(left);
        add(previewPanel);

        scanField.addActionListener(e -> onScan(scanField.getText()));
        scanField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) onScan(scanField.getText());
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800,400);
        setLocationRelativeTo(null);
    }

    private void onScan(String code) {
        scanField.setText("");

        if(code.isEmpty()) {
            status.setText("Empty scan");
            return;
        }

        Product p = products.get(code);
        if(p != null) {
            nameField.setText(p.name);
            priceField.setText(p.price);
            status.setText("Found: "+p.name);
        } else {
            nameField.setText("(NOT FOUND)");
            priceField.setText("");
            status.setText("Product not found");
        }

        try {
            lastLabel = LabelPrinter.generateLabel(code, p != null ? p.name : "", p != null ? p.price : "");
            previewPanel.getGraphics().drawImage(lastLabel, 0, 0, previewPanel.getWidth(), previewPanel.getHeight(), null);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}