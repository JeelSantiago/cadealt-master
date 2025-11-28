package com.cadealt.dao;

import com.cadealt.model.Versiculo;
import com.cadealt.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gerenciar Versículos
 */
public class VersiculoDAO {

    public List<Versiculo> findAll() {
        List<Versiculo> versiculos = new ArrayList<>();
        String sql = "SELECT * FROM versiculos ORDER BY livro, capitulo, versiculo";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                versiculos.add(extractVersiculoFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar versículos: " + e.getMessage());
            e.printStackTrace();
        }

        return versiculos;
    }

    public Versiculo findById(int id) {
        String sql = "SELECT * FROM versiculos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractVersiculoFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar versículo: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Busca versículo por referência formatada (ex: "João 3:16")
     */
    public Versiculo findByReferencia(String referencia, String versao) {
        // Parse da referência
        String[] partes = referencia.split(":");
        if (partes.length != 2) {
            return null;
        }

        String[] livroCapitulo = partes[0].trim().split("\\s+");
        if (livroCapitulo.length < 2) {
            return null;
        }

        // Reconstrói o livro (pode ter múltiplas palavras, como "1 Coríntios")
        StringBuilder livroBuilder = new StringBuilder();
        for (int i = 0; i < livroCapitulo.length - 1; i++) {
            if (i > 0) livroBuilder.append(" ");
            livroBuilder.append(livroCapitulo[i]);
        }
        String livro = livroBuilder.toString();

        try {
            int capitulo = Integer.parseInt(livroCapitulo[livroCapitulo.length - 1]);
            int versiculo = Integer.parseInt(partes[1].trim());

            return findByLivroCapituloVersiculo(livro, capitulo, versiculo, versao);
        } catch (NumberFormatException e) {
            System.err.println("Erro ao parsear referência: " + referencia);
            return null;
        }
    }

    public Versiculo findByLivroCapituloVersiculo(String livro, int capitulo, int versiculo, String versao) {
        String sql = "SELECT * FROM versiculos WHERE LOWER(livro) = ? AND capitulo = ? AND versiculo = ? AND versao = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, livro.toLowerCase());
            pstmt.setInt(2, capitulo);
            pstmt.setInt(3, versiculo);
            pstmt.setString(4, versao);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractVersiculoFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar versículo: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<Versiculo> search(String query) {
        List<Versiculo> versiculos = new ArrayList<>();
        String sql = "SELECT * FROM versiculos WHERE LOWER(livro) LIKE ? OR LOWER(texto) LIKE ? ORDER BY livro, capitulo, versiculo LIMIT 20";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + query.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                versiculos.add(extractVersiculoFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar versículos: " + e.getMessage());
            e.printStackTrace();
        }

        return versiculos;
    }

    public int insert(Versiculo versiculo) {
        String sql = "INSERT INTO versiculos (livro, capitulo, versiculo, texto, versao) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, versiculo.getLivro());
            pstmt.setInt(2, versiculo.getCapitulo());
            pstmt.setInt(3, versiculo.getVersiculo());
            pstmt.setString(4, versiculo.getTexto());
            pstmt.setString(5, versiculo.getVersao());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir versículo: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    public boolean update(Versiculo versiculo) {
        String sql = "UPDATE versiculos SET livro = ?, capitulo = ?, versiculo = ?, texto = ?, versao = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, versiculo.getLivro());
            pstmt.setInt(2, versiculo.getCapitulo());
            pstmt.setInt(3, versiculo.getVersiculo());
            pstmt.setString(4, versiculo.getTexto());
            pstmt.setString(5, versiculo.getVersao());
            pstmt.setInt(6, versiculo.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar versículo: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM versiculos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar versículo: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private Versiculo extractVersiculoFromResultSet(ResultSet rs) throws SQLException {
        return new Versiculo(
            rs.getInt("id"),
            rs.getString("livro"),
            rs.getInt("capitulo"),
            rs.getInt("versiculo"),
            rs.getString("texto"),
            rs.getString("versao")
        );
    }
}
