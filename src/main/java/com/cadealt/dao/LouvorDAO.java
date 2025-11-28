package com.cadealt.dao;

import com.cadealt.model.Louvor;
import com.cadealt.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gerenciar Louvores
 */
public class LouvorDAO {

    public List<Louvor> findAll() {
        List<Louvor> louvores = new ArrayList<>();
        String sql = "SELECT * FROM louvores ORDER BY titulo";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                louvores.add(extractLouvorFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar louvores: " + e.getMessage());
            e.printStackTrace();
        }

        return louvores;
    }

    public Louvor findById(int id) {
        String sql = "SELECT * FROM louvores WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractLouvorFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar louvor: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<Louvor> search(String query) {
        List<Louvor> louvores = new ArrayList<>();
        String sql = "SELECT * FROM louvores WHERE LOWER(titulo) LIKE ? OR LOWER(autor) LIKE ? ORDER BY titulo";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + query.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                louvores.add(extractLouvorFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar louvores: " + e.getMessage());
            e.printStackTrace();
        }

        return louvores;
    }

    public int insert(Louvor louvor) {
        String sql = "INSERT INTO louvores (titulo, autor, letra) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, louvor.getTitulo());
            pstmt.setString(2, louvor.getAutor());
            pstmt.setString(3, louvor.getLetra());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir louvor: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    public boolean update(Louvor louvor) {
        String sql = "UPDATE louvores SET titulo = ?, autor = ?, letra = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, louvor.getTitulo());
            pstmt.setString(2, louvor.getAutor());
            pstmt.setString(3, louvor.getLetra());
            pstmt.setInt(4, louvor.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar louvor: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM louvores WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar louvor: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private Louvor extractLouvorFromResultSet(ResultSet rs) throws SQLException {
        return new Louvor(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getString("autor"),
            rs.getString("letra")
        );
    }
}
