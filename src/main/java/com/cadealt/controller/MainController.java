package com.cadealt.controller;

import com.cadealt.dao.SongDAO;
import com.cadealt.model.Song;
import com.cadealt.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

/**
 * Controlador principal da aplicação
 */
public class MainController {

    @FXML
    private TextField searchField;

    @FXML
    private ListView<Song> songListView;

    @FXML
    private StackPane previewPane;

    @FXML
    private Label previewLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label connectionLabel;

    private SongDAO songDAO;
    private ObservableList<Song> songs;

    /**
     * Inicialização do controlador
     */
    @FXML
    public void initialize() {
        // Inicializa o banco de dados
        DatabaseConnection.initializeDatabase();

        // Inicializa o DAO
        songDAO = new SongDAO();

        // Carrega as músicas
        loadSongs();

        // Configura listener para seleção de música
        songListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    showSongPreview(newValue);
                }
            }
        );

        // Configura busca
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSongs(newValue);
        });

        updateStatus("Aplicação iniciada com sucesso");
    }

    /**
     * Carrega as músicas do banco de dados
     */
    private void loadSongs() {
        songs = FXCollections.observableArrayList(songDAO.findAll());
        songListView.setItems(songs);
    }

    /**
     * Filtra músicas com base no texto de busca
     */
    private void filterSongs(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            songListView.setItems(songs);
        } else {
            ObservableList<Song> filtered = FXCollections.observableArrayList();
            for (Song song : songs) {
                if (song.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                    song.getArtist().toLowerCase().contains(searchText.toLowerCase())) {
                    filtered.add(song);
                }
            }
            songListView.setItems(filtered);
        }
    }

    /**
     * Mostra preview da música selecionada
     */
    private void showSongPreview(Song song) {
        previewLabel.setText(song.getTitle() + "\n" + song.getArtist());
        updateStatus("Música selecionada: " + song.getTitle());
    }

    /**
     * Atualiza a barra de status
     */
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    // ==================== Handlers de Menu ====================

    @FXML
    private void handleNew() {
        updateStatus("Novo projeto");
    }

    @FXML
    private void handleOpen() {
        updateStatus("Abrir projeto");
    }

    @FXML
    private void handleExit() {
        DatabaseConnection.closeConnection();
        System.exit(0);
    }

    @FXML
    private void handleAddSong() {
        updateStatus("Adicionar música - Funcionalidade em desenvolvimento");
        // TODO: Abrir diálogo para adicionar música
    }

    @FXML
    private void handleAddVerse() {
        updateStatus("Adicionar versículo - Funcionalidade em desenvolvimento");
        // TODO: Abrir diálogo para adicionar versículo
    }

    @FXML
    private void handleStartProjection() {
        updateStatus("Iniciando projeção...");
        // TODO: Implementar janela de projeção
    }

    @FXML
    private void handleStopProjection() {
        updateStatus("Projeção parada");
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sobre");
        alert.setHeaderText("CADEALT MASTER");
        alert.setContentText("Sistema de Projeção para Igrejas\nVersão 1.0.0\n\n© 2024 CADEALT Development Team");
        alert.showAndWait();
    }

    // ==================== Handlers de Botões ====================

    @FXML
    private void handleEditSong() {
        Song selected = songListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            updateStatus("Editar música: " + selected.getTitle());
            // TODO: Abrir diálogo de edição
        } else {
            showAlert("Nenhuma música selecionada");
        }
    }

    @FXML
    private void handleRemoveSong() {
        Song selected = songListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar remoção");
            confirm.setHeaderText("Remover música");
            confirm.setContentText("Deseja realmente remover a música '" + selected.getTitle() + "'?");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (songDAO.delete(selected.getId())) {
                        songs.remove(selected);
                        updateStatus("Música removida com sucesso");
                    } else {
                        showAlert("Erro ao remover música");
                    }
                }
            });
        } else {
            showAlert("Nenhuma música selecionada");
        }
    }

    @FXML
    private void handlePrevious() {
        updateStatus("Slide anterior");
        // TODO: Implementar navegação entre slides
    }

    @FXML
    private void handleProject() {
        Song selected = songListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            updateStatus("Projetando: " + selected.getTitle());
            // TODO: Enviar para tela de projeção
        } else {
            showAlert("Nenhuma música selecionada");
        }
    }

    @FXML
    private void handleNext() {
        updateStatus("Próximo slide");
        // TODO: Implementar navegação entre slides
    }

    @FXML
    private void handleClear() {
        previewLabel.setText("Nenhum conteúdo selecionado");
        updateStatus("Tela limpa");
        // TODO: Limpar tela de projeção
    }

    /**
     * Mostra um alerta simples
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
