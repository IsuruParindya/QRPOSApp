package et.qrscanner.app;

import java.sql.*;
import java.util.Map;

public class Database {

    // Place pos.db in project root (next to pom.xml)
    private static final String DB_URL = "jdbc:sqlite:pos.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Find a single product by QR code
    public static Product findProductByQr(String qrCode) {
        String sql = "SELECT name, price FROM products WHERE qr_code = ?";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, qrCode);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Product(
                        qrCode,
                        rs.getString("name"),
                        rs.getString("price"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Save or update product
    public static void saveOrUpdateProduct(Product product) {
        String sql = """
                    INSERT INTO products (qr_code, name, price)
                    VALUES (?, ?, ?)
                    ON CONFLICT(qr_code) DO UPDATE SET
                        name = excluded.name,
                        price = excluded.price
                """;

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getQrCode());
            ps.setString(2, product.getName());
            ps.setString(3, product.getName());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Product> loadProducts() {
        // To Do Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadProducts'");
    }
}