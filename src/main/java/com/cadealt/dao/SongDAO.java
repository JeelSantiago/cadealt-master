package com.cadealt.dao;

import com.cadealt.model.Song;
import com.cadealt.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para a entidade Song
 */
public class SongDAO {

    /**
     * Insere uma nova música no banco de dados
     * @param song Música a ser inserida
     * @return ID da música inserida ou -1 em caso de erro
     */
    public int insert(Song song) {
        String sql = "INSERT INTO songs (title, artist, lyrics, category) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, song.getTitle());
            pstmt.setString(2, song.getArtist());
            pstmt.setString(3, song.getLyrics());
            pstmt.setString(4, song.getCategory());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir música: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Atualiza uma música existente
     * @param song Música a ser atualizada
     * @return true se atualizado com sucesso, false caso contrário
     */
    public boolean update(Song song) {
        String sql = "UPDATE songs SET title = ?, artist = ?, lyrics = ?, category = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, song.getTitle());
            pstmt.setString(2, song.getArtist());
            pstmt.setString(3, song.getLyrics());
            pstmt.setString(4, song.getCategory());
            pstmt.setInt(5, song.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar música: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deleta uma música do banco de dados
     * @param id ID da música a ser deletada
     * @return true se deletado com sucesso, false caso contrário
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM songs WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar música: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Busca uma música pelo ID
     * @param id ID da música
     * @return Song ou null se não encontrado
     */
    public Song findById(int id) {
        String sql = "SELECT * FROM songs WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractSongFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar música: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca todas as músicas
     * @return Lista de músicas
     */
    public List<Song> findAll() {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT * FROM songs ORDER BY title";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                songs.add(extractSongFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar músicas: " + e.getMessage());
            e.printStackTrace();
        }
        return songs;
    }

    /**
     * Busca músicas por categoria
     * @param category Categoria a buscar
     * @return Lista de músicas da categoria
     */
    public List<Song> findByCategory(String category) {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT * FROM songs WHERE category = ? ORDER BY title";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                songs.add(extractSongFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar músicas por categoria: " + e.getMessage());
            e.printStackTrace();
        }
        return songs;
    }

    /**
     * Extrai um objeto Song do ResultSet
     * @param rs ResultSet
     * @return Song
     * @throws SQLException
     */
    private Song extractSongFromResultSet(ResultSet rs) throws SQLException {
        return new Song(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("artist"),
            rs.getString("lyrics"),
            rs.getString("category")
        );
    }
}
