package et.qrscanner.app;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterJob;

public class Main {

    // UI fields
    private JTextField scanField;
    private JTextField productField;
    private JTextField priceField;
    private JLabel statusLabel;

    // Preview labels (images)
    private JLabel qrPreviewLabel;
    private JLabel labelPreviewLabel;

    // Keep last generated label image (for printing)
    private BufferedImage lastLabelImage;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().createAndShowUI());
    }

    private void createAndShowUI() {
        JFrame frame = new JFrame("QR POS App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Root layout: Left controls + Right previews
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // LEFT: form + buttons + status
        JPanel left = buildLeftPanel();

        // RIGHT: previews
        JPanel right = buildRightPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.35);
        split.setDividerLocation(380);

        root.add(split, BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.setSize(1100, 650);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // UX: cursor starts in Scan QR field
        scanField.requestFocusInWindow();
        setStatus("Ready. Click Scan QR box and scan.", false);
    }

    private JPanel buildLeftPanel() {
        JPanel left = new JPanel(new BorderLayout(10, 10));

        // Form panel
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Input",
                TitledBorder.LEADING,
                TitledBorder.TOP));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        scanField = new JTextField(18);
        productField = new JTextField(18);
        priceField = new JTextField(18);

        // When user presses Enter in Scan QR -> do lookup + preview
        scanField.addActionListener(this::onScanEnter);

        addRow(form, c, 0, "Scan QR:", scanField);
        addRow(form, c, 1, "Product:", productField);
        addRow(form, c, 2, "Price:", priceField);

        // Buttons panel (smaller buttons)
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(), "Actions"),
                BorderFactory.createEmptyBorder(0, 0, 12, 0) // bottom padding INSIDE panel
        ));

        // Adjust button size here
        Dimension btnSize = new Dimension(Integer.MAX_VALUE, 35);

        JButton lookupBtn = new JButton("Lookup (DB)");
        lookupBtn.addActionListener(e -> lookupProduct());

        JButton previewBtn = new JButton("Generate Preview");
        previewBtn.addActionListener(e -> generatePreviews());

        JButton printBtn = new JButton("Print Label");
        printBtn.addActionListener(e -> printLabel());

        JButton saveBtn = new JButton("Save / Update (DB)");
        saveBtn.addActionListener(e -> saveOrUpdate());

        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> clearAll());

        // Apply sizing + center alignment
        for (JButton b : new JButton[] { lookupBtn, previewBtn, printBtn, saveBtn, clearBtn }) {
            b.setMaximumSize(btnSize);
            b.setPreferredSize(btnSize);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        // Add with spacing and keep buttons at top
        buttons.add(Box.createVerticalStrut(8));
        buttons.add(lookupBtn);
        buttons.add(Box.createVerticalStrut(8));
        buttons.add(previewBtn);
        buttons.add(Box.createVerticalStrut(8));
        buttons.add(printBtn);
        buttons.add(Box.createVerticalStrut(8));
        buttons.add(saveBtn);
        buttons.add(Box.createVerticalStrut(8));
        buttons.add(clearBtn);
        buttons.add(Box.createVerticalGlue());

        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Status"));
        statusLabel = new JLabel("Ready");
        statusPanel.add(statusLabel, BorderLayout.CENTER);

        // Left main layout
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.add(form, BorderLayout.NORTH);
        top.add(buttons, BorderLayout.CENTER);

        left.add(top, BorderLayout.NORTH);
        left.add(statusPanel, BorderLayout.SOUTH);

        return left;
    }

    private JPanel buildRightPanel() {
        JPanel right = new JPanel(new GridLayout(2, 1, 10, 10));

        qrPreviewLabel = new JLabel("QR preview will appear here", SwingConstants.CENTER);
        qrPreviewLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        qrPreviewLabel.setHorizontalTextPosition(SwingConstants.CENTER);

        labelPreviewLabel = new JLabel("Label preview will appear here", SwingConstants.CENTER);
        labelPreviewLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        labelPreviewLabel.setHorizontalTextPosition(SwingConstants.CENTER);

        JPanel qrPanel = new JPanel(new BorderLayout());
        qrPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "QR Preview"));
        qrPanel.add(qrPreviewLabel, BorderLayout.CENTER);

        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Label Preview"));
        labelPanel.add(labelPreviewLabel, BorderLayout.CENTER);

        right.add(qrPanel);
        right.add(labelPanel);

        return right;
    }

    private void onScanEnter(ActionEvent e) {
        String qr = scanField.getText().trim();
        if (qr.isEmpty()) {
            setStatus("Scan QR is empty.", true);
            return;
        }

        setStatus("Scanned: " + qr + " | Looking up + generating preview...", false);

        lookupProduct();
        generatePreviews();
    }

    private void lookupProduct() {
        String qr = scanField.getText().trim();
        if (qr.isEmpty()) {
            setStatus("Please scan/enter a QR first.", true);
            return;
        }

        try {
            // If you implement Database.findProductByQr(qr), enable below:
            // Product p = Database.findProductByQr(qr);
            // if (p != null) {
            // productField.setText(p.getName());
            // priceField.setText(p.getPrice());
            // setStatus("Product found in DB.", false);
            // } else {
            // setStatus("Product not found. Enter details and Save/Update.", false);
            // }

            setStatus("Lookup: implement DB lookup to auto-fill Product & Price.", false);

        } catch (Exception ex) {
            setStatus("DB lookup failed: " + ex.getMessage(), true);
        }
    }

    private void generatePreviews() {
        String qr = scanField.getText().trim();
        String name = productField.getText().trim();
        String price = priceField.getText().trim();

        if (qr.isEmpty()) {
            setStatus("Please scan/enter a QR first.", true);
            return;
        }
        if (name.isEmpty() || price.isEmpty()) {
            setStatus("Fill Product + Price (or implement DB lookup) before generating label.", true);
        }

        try {
            // QR preview
            BufferedImage qrImg = LabelPrinter.generateQRCodeImage(qr);
            qrPreviewLabel.setIcon(new ImageIcon(scaleToFit(qrImg, 320, 220)));
            qrPreviewLabel.setText(null);

            // Label preview
            if (!name.isEmpty() && !price.isEmpty()) {
                lastLabelImage = LabelPrinter.generateLabel(qr, name, price);
                labelPreviewLabel.setIcon(new ImageIcon(scaleToFit(lastLabelImage, 600, 260)));
                labelPreviewLabel.setText(null);
                setStatus("Preview generated. Ready to print.", false);
            } else {
                setStatus("QR preview shown. Enter Product+Price to generate label preview.", false);
            }

        } catch (Exception ex) {
            setStatus("Preview generation failed: " + ex.getMessage(), true);
        }
    }

    private void printLabel() {
        if (lastLabelImage == null) {
            setStatus("Nothing to print. Generate label preview first.", true);
            return;
        }

        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("QR POS Label");

            job.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0)
                    return java.awt.print.Printable.NO_SUCH_PAGE;

                Graphics2D g2 = (Graphics2D) graphics;
                g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                double sx = pageFormat.getImageableWidth() / lastLabelImage.getWidth();
                double sy = pageFormat.getImageableHeight() / lastLabelImage.getHeight();
                double scale = Math.min(sx, sy);
                g2.scale(scale, scale);

                g2.drawImage(lastLabelImage, 0, 0, null);
                return java.awt.print.Printable.PAGE_EXISTS;
            });

            boolean ok = job.printDialog();
            if (ok) {
                job.print();
                setStatus("Print job sent.", false);
            } else {
                setStatus("Print cancelled.", false);
            }

        } catch (Exception ex) {
            setStatus("Print failed: " + ex.getMessage(), true);
        }
    }

    private void saveOrUpdate() {
        String qr = scanField.getText().trim();
        String name = productField.getText().trim();
        String price = priceField.getText().trim();

        if (qr.isEmpty() || name.isEmpty() || price.isEmpty()) {
            setStatus("Scan QR + fill Product + Price before saving.", true);
            return;
        }

        try {
            // If you implement Database.saveOrUpdateProduct(...), enable below:
            // Database.saveOrUpdateProduct(new Product(qr, name, price));
            // setStatus("Saved/Updated in DB.", false);

            setStatus("Save/Update: implement DB save method to store product.", false);

        } catch (Exception ex) {
            setStatus("Save failed: " + ex.getMessage(), true);
        }
    }

    private void clearAll() {
        scanField.setText("");
        productField.setText("");
        priceField.setText("");

        qrPreviewLabel.setIcon(null);
        qrPreviewLabel.setText("QR preview will appear here");

        labelPreviewLabel.setIcon(null);
        labelPreviewLabel.setText("Label preview will appear here");

        lastLabelImage = null;
        setStatus("Ready. Click Scan QR box and scan.", false);
        scanField.requestFocusInWindow();
    }

    private void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setForeground(isError ? Color.RED : Color.DARK_GRAY);
    }

    private void addRow(JPanel panel, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0;
        panel.add(new JLabel(label), c);

        c.gridx = 1;
        c.weightx = 1;
        panel.add(field, c);
    }

    private BufferedImage scaleToFit(BufferedImage src, int maxW, int maxH) {
        int w = src.getWidth();
        int h = src.getHeight();

        double scale = Math.min((double) maxW / w, (double) maxH / h);
        if (scale >= 1.0)
            return src;

        int newW = (int) Math.round(w * scale);
        int newH = (int) Math.round(h * scale);

        Image tmp = src.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage out = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.drawImage(tmp, 0, 0, null);
        g2.dispose();
        return out;
    }
}