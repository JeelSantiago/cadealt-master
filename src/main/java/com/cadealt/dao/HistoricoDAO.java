package com.cadealt.dao;

import com.cadealt.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gerenciar Histórico de Cultos
 */
public class HistoricoDAO {

    public int salvar(String tipoCulto, List<String> itens) {
        String sql = "INSERT INTO historico_culto (data, tipo_culto, itens) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String dataAtual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String itensJson = String.join("|||", itens); // Separador simples

            pstmt.setString(1, dataAtual);
            pstmt.setString(2, tipoCulto);
            pstmt.setString(3, itensJson);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao salvar histórico: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    public List<HistoricoRecord> listarTodos() {
        List<HistoricoRecord> historicos = new ArrayList<>();
        String sql = "SELECT * FROM historico_culto ORDER BY data DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String data = rs.getString("data");
                String tipoCulto = rs.getString("tipo_culto");
                String itensStr = rs.getString("itens");

                List<String> itens = new ArrayList<>();
                if (itensStr != null && !itensStr.isEmpty()) {
                    String[] partes = itensStr.split("\\|\\|\\|");
                    for (String parte : partes) {
                        itens.add(parte);
                    }
                }

                historicos.add(new HistoricoRecord(id, data, tipoCulto, itens));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar históricos: " + e.getMessage());
            e.printStackTrace();
        }

        return historicos;
    }

    public boolean deletar(int id) {
        String sql = "DELETE FROM historico_culto WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar histórico: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Record para representar um histórico recuperado do banco
     */
    public record HistoricoRecord(int id, String data, String tipoCulto, List<String> itens) {}
}
