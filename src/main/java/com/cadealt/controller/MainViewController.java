package com.cadealt.controller;

import com.cadealt.dao.SongDAO;
import com.cadealt.model.Song;
import com.cadealt.util.DatabaseConnection;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador principal para a interface main.fxml
 * Gerencia as 3 colunas: Biblioteca, Preview e Funcionalidades
 */
public class MainViewController {

    // ==================== TOPBAR ====================
    @FXML
    private Label statusConnectionLabel;

    // ==================== COLUNA 1: BIBLIOTECA ====================
    @FXML
    private TabPane bibliotecaTabPane;

    @FXML
    private TextField searchHarpaField;

    @FXML
    private ListView<String> harpaListView;

    @FXML
    private TextField searchLouvorField;

    @FXML
    private ListView<String> louvorListView;

    @FXML
    private TextField searchVersiculoField;

    @FXML
    private ListView<String> versiculoListView;

    // ==================== COLUNA 2: PREVIEW ====================
    @FXML
    private StackPane previewPane;

    @FXML
    private Label previewLabel;

    @FXML
    private Button btnPrevSlide;

    @FXML
    private Label slideCountLabel;

    @FXML
    private Button btnNextSlide;

    @FXML
    private Button btnProjetar;

    @FXML
    private Button btnTelaPreta;

    @FXML
    private Button btnFundo;

    // ==================== COLUNA 3: FUNCIONALIDADES ====================
    @FXML
    private TextField versiculoInputField;

    @FXML
    private ComboBox<String> bibliaVersionComboBox;

    @FXML
    private Button btnBuscarVersiculo;

    @FXML
    private ComboBox<String> historicoTipoComboBox;

    @FXML
    private ListView<String> historicoListView;

    @FXML
    private Button btnLimparHistorico;

    @FXML
    private Button btnExportarHistorico;

    @FXML
    private Button btnSalvarHistorico;

    @FXML
    private TextField fundoImagemField;

    @FXML
    private Button btnEscolherFundo;

    @FXML
    private Button btnAplicarFundo;

    @FXML
    private Button btnAdicionarLouvor;

    // ==================== FOOTER ====================
    @FXML
    private Label footerStatusLabel;

    @FXML
    private Label footerTimeLabel;

    @FXML
    private Label footerDateLabel;

    // ==================== VARIÁVEIS DE CONTROLE ====================
    private SongDAO songDAO;
    private ObservableList<String> harpaList;
    private ObservableList<String> louvorList;
    private ObservableList<String> versiculoList;
    private ObservableList<String> historicoList;

    private List<String> currentSlides;
    private int currentSlideIndex;
    private File selectedBackgroundImage;

    /**
     * Inicialização do controlador
     */
    @FXML
    public void initialize() {
        // Inicializa o banco de dados
        DatabaseConnection.initializeDatabase();
        songDAO = new SongDAO();

        // Inicializa as listas
        initializeLists();

        // Configura os componentes
        setupComponents();

        // Inicia o relógio do footer
        startFooterClock();

        // Atualiza status
        updateFooterStatus("Sistema Pronto");
    }

    /**
     * Inicializa as listas de dados
     */
    private void initializeLists() {
        // Lista da Harpa (exemplo com 500 hinos)
        harpaList = FXCollections.observableArrayList();
        for (int i = 1; i <= 500; i++) {
            harpaList.add(String.format("Hino %03d - Título do Hino %d", i, i));
        }
        harpaListView.setItems(harpaList);

        // Lista de Louvores
        louvorList = FXCollections.observableArrayList(
            "Aclame ao Senhor",
            "Agnus Dei",
            "Aleluia",
            "Bondade de Deus",
            "Como é Bom",
            "Deus Proverá",
            "Ele Vive",
            "Fiel é o Senhor",
            "Grandioso és Tu",
            "Há Poder"
        );
        louvorListView.setItems(louvorList);

        // Lista de Versículos Recentes
        versiculoList = FXCollections.observableArrayList(
            "João 3:16 - Porque Deus amou o mundo...",
            "Salmos 23:1 - O Senhor é meu pastor...",
            "Filipenses 4:13 - Tudo posso naquele..."
        );
        versiculoListView.setItems(versiculoList);

        // Lista do Histórico do Culto
        historicoList = FXCollections.observableArrayList();
        historicoListView.setItems(historicoList);

        // Slides atuais
        currentSlides = new ArrayList<>();
        currentSlideIndex = 0;
    }

    /**
     * Configura os componentes da interface
     */
    private void setupComponents() {
        // Configurar seleção padrão do tipo de histórico
        historicoTipoComboBox.getSelectionModel().selectFirst();

        // Configurar seleção padrão da versão da Bíblia
        bibliaVersionComboBox.getSelectionModel().selectFirst();

        // Listeners para busca na Harpa
        searchHarpaField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterHarpaList(newVal);
        });

        // Listeners para busca de Louvores
        searchLouvorField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterLouvorList(newVal);
        });

        // Listener para seleção na Harpa
        harpaListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadHinoToPreview(newVal);
                addToHistorico(newVal);
            }
        });

        // Listener para seleção nos Louvores
        louvorListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadLouvorToPreview(newVal);
                addToHistorico(newVal);
            }
        });

        // Listener para seleção nos Versículos
        versiculoListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadVersiculoToPreview(newVal);
                addToHistorico(newVal);
            }
        });

        // Atualizar contador de slides
        updateSlideCounter();
    }

    /**
     * Filtra a lista da Harpa
     */
    private void filterHarpaList(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            harpaListView.setItems(harpaList);
        } else {
            ObservableList<String> filtered = FXCollections.observableArrayList();
            for (String hino : harpaList) {
                if (hino.toLowerCase().contains(searchText.toLowerCase())) {
                    filtered.add(hino);
                }
            }
            harpaListView.setItems(filtered);
        }
    }

    /**
     * Filtra a lista de Louvores
     */
    private void filterLouvorList(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            louvorListView.setItems(louvorList);
        } else {
            ObservableList<String> filtered = FXCollections.observableArrayList();
            for (String louvor : louvorList) {
                if (louvor.toLowerCase().contains(searchText.toLowerCase())) {
                    filtered.add(louvor);
                }
            }
            louvorListView.setItems(filtered);
        }
    }

    /**
     * Carrega um hino para o preview
     */
    private void loadHinoToPreview(String hino) {
        // Exemplo: dividir letra em slides
        currentSlides = new ArrayList<>();
        currentSlides.add(hino);
        currentSlides.add("Estrofe 1\nLinha 1\nLinha 2\nLinha 3");
        currentSlides.add("Estrofe 2\nLinha 1\nLinha 2\nLinha 3");
        currentSlides.add("Coro\nLinha 1\nLinha 2");

        currentSlideIndex = 0;
        showCurrentSlide();
        updateFooterStatus("Hino carregado: " + hino);
    }

    /**
     * Carrega um louvor para o preview
     */
    private void loadLouvorToPreview(String louvor) {
        currentSlides = new ArrayList<>();
        currentSlides.add(louvor);
        currentSlides.add("Letra do louvor\nVersículo 1\nLinha 1\nLinha 2");
        currentSlides.add("Refrão\nLinha 1\nLinha 2\nLinha 3");

        currentSlideIndex = 0;
        showCurrentSlide();
        updateFooterStatus("Louvor carregado: " + louvor);
    }

    /**
     * Carrega um versículo para o preview
     */
    private void loadVersiculoToPreview(String versiculo) {
        currentSlides = new ArrayList<>();
        currentSlides.add(versiculo);

        currentSlideIndex = 0;
        showCurrentSlide();
        updateFooterStatus("Versículo carregado");
    }

    /**
     * Mostra o slide atual no preview
     */
    private void showCurrentSlide() {
        if (currentSlides.isEmpty()) {
            previewLabel.setText("Nenhum conteúdo selecionado");
        } else {
            previewLabel.setText(currentSlides.get(currentSlideIndex));
        }
        updateSlideCounter();
    }

    /**
     * Atualiza o contador de slides
     */
    private void updateSlideCounter() {
        if (currentSlides.isEmpty()) {
            slideCountLabel.setText("0 / 0");
        } else {
            slideCountLabel.setText((currentSlideIndex + 1) + " / " + currentSlides.size());
        }
    }

    /**
     * Adiciona item ao histórico do culto
     */
    private void addToHistorico(String item) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        String historicoItem = timestamp + " - " + item;

        if (!historicoList.contains(historicoItem)) {
            historicoList.add(historicoItem);
        }
    }

    /**
     * Inicia o relógio do footer
     */
    private void startFooterClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            footerTimeLabel.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            footerDateLabel.setText(now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }), new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    /**
     * Atualiza o status do footer
     */
    private void updateFooterStatus(String message) {
        footerStatusLabel.setText(message);
    }

    // ==================== HANDLERS DE AÇÃO ====================

    /**
     * Slide anterior
     */
    @FXML
    private void handlePreviousSlide() {
        if (currentSlideIndex > 0) {
            currentSlideIndex--;
            showCurrentSlide();
            updateFooterStatus("Slide anterior");
        }
    }

    /**
     * Próximo slide
     */
    @FXML
    private void handleNextSlide() {
        if (currentSlideIndex < currentSlides.size() - 1) {
            currentSlideIndex++;
            showCurrentSlide();
            updateFooterStatus("Próximo slide");
        }
    }

    /**
     * Projetar
     */
    @FXML
    private void handleProjetar() {
        updateFooterStatus("Projetando conteúdo...");
        // TODO: Implementar janela de projeção
        showAlert("Projeção", "Funcionalidade de projeção será implementada", Alert.AlertType.INFORMATION);
    }

    /**
     * Tela preta
     */
    @FXML
    private void handleTelaPreta() {
        previewLabel.setText("");
        updateFooterStatus("Tela preta ativada");
        // TODO: Enviar comando para janela de projeção
    }

    /**
     * Fundo
     */
    @FXML
    private void handleFundo() {
        if (selectedBackgroundImage != null) {
            updateFooterStatus("Exibindo fundo: " + selectedBackgroundImage.getName());
            // TODO: Aplicar fundo na janela de projeção
        } else {
            showAlert("Fundo", "Nenhuma imagem de fundo selecionada", Alert.AlertType.WARNING);
        }
    }

    /**
     * Buscar versículo
     */
    @FXML
    private void handleBuscarVersiculo() {
        String referencia = versiculoInputField.getText();
        String versao = bibliaVersionComboBox.getValue();

        if (referencia == null || referencia.isEmpty()) {
            showAlert("Buscar Versículo", "Digite uma referência bíblica (Ex: João 3:16)", Alert.AlertType.WARNING);
            return;
        }

        // TODO: Implementar busca real de versículos
        String versiculoTexto = referencia + "\n\n\"Porque Deus amou o mundo de tal maneira que deu o seu Filho unigênito...\"";

        currentSlides = new ArrayList<>();
        currentSlides.add(versiculoTexto);
        currentSlideIndex = 0;
        showCurrentSlide();

        addToHistorico(referencia + " (" + versao + ")");
        updateFooterStatus("Versículo encontrado: " + referencia);
    }

    /**
     * Limpar histórico
     */
    @FXML
    private void handleLimparHistorico() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Limpar Histórico");
        confirm.setHeaderText("Confirmar limpeza");
        confirm.setContentText("Deseja realmente limpar o histórico do culto?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                historicoList.clear();
                updateFooterStatus("Histórico limpo");
            }
        });
    }

    /**
     * Exportar histórico
     */
    @FXML
    private void handleExportarHistorico() {
        updateFooterStatus("Exportando histórico...");
        // TODO: Implementar exportação para arquivo
        showAlert("Exportar", "Funcionalidade de exportação será implementada", Alert.AlertType.INFORMATION);
    }

    /**
     * Salvar histórico
     */
    @FXML
    private void handleSalvarHistorico() {
        updateFooterStatus("Salvando histórico...");
        // TODO: Implementar salvamento no banco de dados
        showAlert("Salvar", "Histórico salvo com sucesso!", Alert.AlertType.INFORMATION);
    }

    /**
     * Escolher fundo
     */
    @FXML
    private void handleEscolherFundo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Imagem de Fundo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.bmp"),
            new FileChooser.ExtensionFilter("Todos os Arquivos", "*.*")
        );

        selectedBackgroundImage = fileChooser.showOpenDialog(fundoImagemField.getScene().getWindow());

        if (selectedBackgroundImage != null) {
            fundoImagemField.setText(selectedBackgroundImage.getName());
            updateFooterStatus("Imagem selecionada: " + selectedBackgroundImage.getName());
        }
    }

    /**
     * Aplicar fundo
     */
    @FXML
    private void handleAplicarFundo() {
        if (selectedBackgroundImage != null) {
            updateFooterStatus("Aplicando fundo: " + selectedBackgroundImage.getName());
            // TODO: Aplicar fundo na janela de projeção
            showAlert("Fundo", "Fundo aplicado: " + selectedBackgroundImage.getName(), Alert.AlertType.INFORMATION);
        } else {
            showAlert("Fundo", "Selecione uma imagem primeiro", Alert.AlertType.WARNING);
        }
    }

    /**
     * Adicionar louvor
     */
    @FXML
    private void handleAdicionarLouvor() {
        updateFooterStatus("Abrindo diálogo para adicionar louvor...");
        // TODO: Abrir diálogo para adicionar novo louvor
        showAlert("Adicionar Louvor", "Funcionalidade será implementada", Alert.AlertType.INFORMATION);
    }

    /**
     * Mostra um alerta
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
