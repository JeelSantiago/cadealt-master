package com.cadealt.dao;

import com.cadealt.model.Hino;
import com.cadealt.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gerenciar Hinos
 */
public class HinoDAO {

    public List<Hino> findAll() {
        List<Hino> hinos = new ArrayList<>();
        String sql = "SELECT * FROM hinos ORDER BY CAST(numero AS INTEGER)";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                hinos.add(extractHinoFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar hinos: " + e.getMessage());
            e.printStackTrace();
        }

        return hinos;
    }

    public Hino findById(int id) {
        String sql = "SELECT * FROM hinos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractHinoFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar hino: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public Hino findByNumero(String numero) {
        String sql = "SELECT * FROM hinos WHERE numero = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, numero);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractHinoFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar hino por número: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<Hino> search(String query) {
        List<Hino> hinos = new ArrayList<>();
        String sql = "SELECT * FROM hinos WHERE LOWER(numero) LIKE ? OR LOWER(titulo) LIKE ? ORDER BY CAST(numero AS INTEGER)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + query.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                hinos.add(extractHinoFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar hinos: " + e.getMessage());
            e.printStackTrace();
        }

        return hinos;
    }

    public int insert(Hino hino) {
        String sql = "INSERT INTO hinos (numero, titulo, letra, tipo) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, hino.getNumero());
            pstmt.setString(2, hino.getTitulo());
            pstmt.setString(3, hino.getLetra());
            pstmt.setString(4, hino.getTipo());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir hino: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    public boolean update(Hino hino) {
        String sql = "UPDATE hinos SET numero = ?, titulo = ?, letra = ?, tipo = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hino.getNumero());
            pstmt.setString(2, hino.getTitulo());
            pstmt.setString(3, hino.getLetra());
            pstmt.setString(4, hino.getTipo());
            pstmt.setInt(5, hino.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar hino: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM hinos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar hino: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private Hino extractHinoFromResultSet(ResultSet rs) throws SQLException {
        return new Hino(
            rs.getInt("id"),
            rs.getString("numero"),
            rs.getString("titulo"),
            rs.getString("letra"),
            rs.getString("tipo")
        );
    }
}
