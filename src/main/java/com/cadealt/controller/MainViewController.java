package com.cadealt.controller;

import com.cadealt.dao.*;
import com.cadealt.model.*;
import com.cadealt.util.DatabaseConnection;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller Principal - Implementação COMPLETA de TODAS as funcionalidades
 */
public class MainViewController {

    // ==================== TOPBAR ====================
    @FXML private Label statusConnectionLabel;

    // ==================== COLUNA 1: BIBLIOTECA ====================
    @FXML private TabPane bibliotecaTabPane;
    @FXML private TextField searchHarpaField;
    @FXML private ListView<Hino> harpaListView;
    @FXML private TextField searchLouvorField;
    @FXML private ListView<Louvor> louvorListView;
    @FXML private TextField searchVersiculoField;
    @FXML private ListView<String> versiculoListView;

    // ==================== COLUNA 2: PREVIEW ====================
    @FXML private StackPane previewPane;
    @FXML private Label previewLabel;
    @FXML private Button btnPrevSlide;
    @FXML private Label slideCountLabel;
    @FXML private Button btnNextSlide;
    @FXML private Button btnProjetar;
    @FXML private Button btnTelaPreta;
    @FXML private Button btnFundo;

    // ==================== COLUNA 3: FUNCIONALIDADES ====================
    @FXML private TextField versiculoInputField;
    @FXML private ComboBox<String> bibliaVersionComboBox;
    @FXML private Button btnBuscarVersiculo;
    @FXML private ComboBox<String> historicoTipoComboBox;
    @FXML private ListView<ItemHistorico> historicoListView;
    @FXML private Button btnLimparHistorico;
    @FXML private Button btnExportarHistorico;
    @FXML private Button btnSalvarHistorico;
    @FXML private TextField fundoImagemField;
    @FXML private Button btnEscolherFundo;
    @FXML private Button btnAplicarFundo;
    @FXML private Button btnAdicionarLouvor;

    // ==================== FOOTER ====================
    @FXML private Label footerStatusLabel;
    @FXML private Label footerTimeLabel;
    @FXML private Label footerDateLabel;

    // ==================== DAOs ====================
    private HinoDAO hinoDAO;
    private LouvorDAO louvorDAO;
    private VersiculoDAO versiculoDAO;
    private HistoricoDAO historicoDAO;

    // ==================== LISTAS ====================
    private ObservableList<Hino> hinosList;
    private ObservableList<Hino> hinosFiltered;
    private ObservableList<Louvor> louvoresList;
    private ObservableList<Louvor> louvoresFiltered;
    private ObservableList<String> versiculosRecentesList;
    private ObservableList<ItemHistorico> historicoList;

    // ==================== CONTROLE DE SLIDES ====================
    private List<String> currentSlides;
    private int currentSlideIndex;
    private String currentReferencia; // Referência atual (hino/versículo - null para louvor)
    private Object currentContent; // Hino, Louvor ou Versiculo atual

    // ==================== PROJEÇÃO ====================
    private ProjectionController projectionController;
    private File selectedBackgroundImage;
    private boolean ctrlPressed = false;

    /**
     * Inicialização COMPLETA do sistema
     */
    @FXML
    public void initialize() {
        System.out.println("=== Inicializando MainViewController ===");

        // 1. Inicializar banco de dados
        DatabaseConnection.initializeDatabase();

        // 2. Inicializar DAOs
        hinoDAO = new HinoDAO();
        louvorDAO = new LouvorDAO();
        versiculoDAO = new VersiculoDAO();
        historicoDAO = new HistoricoDAO();

        // 3. Inicializar listas
        initializeLists();

        // 4. Configurar componentes
        setupComponents();

        // 5. Configurar listeners
        setupListeners();

        // 6. Iniciar relógio
        startFooterClock();

        // 7. Carregar dados do banco
        loadDataFromDatabase();

        // 8. Inicializar controle de slides
        currentSlides = new ArrayList<>();
        currentSlideIndex = 0;
        currentReferencia = null;

        updateFooterStatus("Sistema Pronto");
        System.out.println("=== MainViewController inicializado com sucesso ===");
    }

    /**
     * Define o controller de projeção (chamado pelo CadealtMaster)
     */
    public void setProjectionController(ProjectionController projectionController) {
        this.projectionController = projectionController;
        System.out.println("ProjectionController conectado ao MainViewController");
    }

    /**
     * Inicializa as listas Observable
     */
    private void initializeLists() {
        hinosList = FXCollections.observableArrayList();
        hinosFiltered = FXCollections.observableArrayList();
        louvoresList = FXCollections.observableArrayList();
        louvoresFiltered = FXCollections.observableArrayList();
        versiculosRecentesList = FXCollections.observableArrayList();
        historicoList = FXCollections.observableArrayList();

        harpaListView.setItems(hinosFiltered);
        louvorListView.setItems(louvoresFiltered);
        versiculoListView.setItems(versiculosRecentesList);
        historicoListView.setItems(historicoList);
    }

    /**
     * Configura os componentes iniciais
     */
    private void setupComponents() {
        // ComboBox de versões da Bíblia
        bibliaVersionComboBox.setItems(FXCollections.observableArrayList(
            "ARC - Almeida Revista e Corrigida",
            "ARA - Almeida Revista e Atualizada",
            "NVI - Nova Versão Internacional",
            "NTLH - Nova Tradução na Linguagem de Hoje"
        ));
        bibliaVersionComboBox.getSelectionModel().selectFirst();

        // ComboBox de tipos de culto
        historicoTipoComboBox.setItems(FXCollections.observableArrayList(
            "Normal",
            "Congresso",
            "Santa Ceia",
            "Domingo"
        ));
        historicoTipoComboBox.getSelectionModel().selectFirst();

        // Desabilitar botões de navegação inicialmente
        btnPrevSlide.setDisable(true);
        btnNextSlide.setDisable(true);

        updateSlideCounter();
    }

    /**
     * Configura TODOS os listeners
     */
    private void setupListeners() {
        // === BUSCA HARPA ===
        searchHarpaField.textProperty().addListener((obs, oldVal, newVal) -> filterHarpa(newVal));

        // === BUSCA LOUVOR ===
        searchLouvorField.textProperty().addListener((obs, oldVal, newVal) -> filterLouvor(newVal));

        // === CLIQUE SIMPLES HARPA ===
        harpaListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                System.out.println("Hino selecionado: " + newVal.toString());
            }
        });

        // === CLIQUE DUPLO HARPA ===
        harpaListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Hino selected = harpaListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    loadHinoToPreview(selected);
                    addToHistorico(ItemHistorico.TipoItem.HINO, selected.toString(), selected);
                }
            }
        });

        // === CLIQUE DUPLO LOUVOR ===
        louvorListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Louvor selected = louvorListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    loadLouvorToPreview(selected);
                    addToHistorico(ItemHistorico.TipoItem.LOUVOR, selected.toString(), selected);
                }
            }
        });

        // === CLIQUE DUPLO HISTÓRICO ===
        historicoListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                ItemHistorico item = historicoListView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    reloadFromHistorico(item);
                }
            }
        });

        // === ATALHOS DE TECLADO ===
        previewPane.setFocusTraversable(true);
        previewPane.setOnKeyPressed(this::handleKeyPress);
        previewPane.setOnKeyReleased(this::handleKeyRelease);
    }

    /**
     * Carrega dados do banco de dados
     */
    private void loadDataFromDatabase() {
        System.out.println("Carregando dados do banco...");

        // Carregar hinos
        hinosList.setAll(hinoDAO.findAll());
        hinosFiltered.setAll(hinosList);
        System.out.println("Hinos carregados: " + hinosList.size());

        // Carregar louvores
        louvoresList.setAll(louvorDAO.findAll());
        louvoresFiltered.setAll(louvoresList);
        System.out.println("Louvores carregados: " + louvoresList.size());
    }

    /**
     * Filtra lista de hinos
     */
    private void filterHarpa(String query) {
        if (query == null || query.trim().isEmpty()) {
            hinosFiltered.setAll(hinosList);
        } else {
            hinosFiltered.setAll(hinoDAO.search(query));
        }
    }

    /**
     * Filtra lista de louvores
     */
    private void filterLouvor(String query) {
        if (query == null || query.trim().isEmpty()) {
            louvoresFiltered.setAll(louvoresList);
        } else {
            louvoresFiltered.setAll(louvorDAO.search(query));
        }
    }

    /**
     * Carrega hino no preview e divide em slides
     */
    private void loadHinoToPreview(Hino hino) {
        currentContent = hino;
        currentReferencia = hino.getReferencia(); // "001 - Chuvas de Graça"

        // Dividir letra em slides (separar por linha em branco)
        String letra = hino.getLetra();
        if (letra != null && !letra.isEmpty()) {
            currentSlides = dividirEmSlides(letra);
        } else {
            currentSlides = new ArrayList<>();
            currentSlides.add(hino.getTitulo());
        }

        currentSlideIndex = 0;
        showCurrentSlide();
        updateFooterStatus("Hino carregado: " + hino.getTitulo());
    }

    /**
     * Carrega louvor no preview e divide em slides
     */
    private void loadLouvorToPreview(Louvor louvor) {
        currentContent = louvor;
        currentReferencia = null; // LOUVORES NÃO MOSTRAM REFERÊNCIA

        // Dividir letra em slides
        String letra = louvor.getLetra();
        if (letra != null && !letra.isEmpty()) {
            currentSlides = dividirEmSlides(letra);
        } else {
            currentSlides = new ArrayList<>();
            currentSlides.add(louvor.getTitulo());
        }

        currentSlideIndex = 0;
        showCurrentSlide();
        updateFooterStatus("Louvor carregado: " + louvor.getTitulo());
    }

    /**
     * Carrega versículo no preview
     */
    private void loadVersiculoToPreview(Versiculo versiculo) {
        currentContent = versiculo;
        currentReferencia = versiculo.getReferencia(); // "João 3:16"

        // Versículo é um slide único
        currentSlides = new ArrayList<>();
        currentSlides.add(versiculo.getTexto());

        currentSlideIndex = 0;
        showCurrentSlide();
        updateFooterStatus("Versículo carregado: " + versiculo.getReferencia());

        // Adicionar aos versículos recentes
        if (!versiculosRecentesList.contains(versiculo.getReferencia())) {
            versiculosRecentesList.add(0, versiculo.getReferencia());
            if (versiculosRecentesList.size() > 20) {
                versiculosRecentesList.remove(20, versiculosRecentesList.size());
            }
        }
    }

    /**
     * Divide texto em slides (por linha em branco \n\n)
     */
    private List<String> dividirEmSlides(String texto) {
        List<String> slides = new ArrayList<>();
        String[] blocos = texto.split("\n\n");

        for (String bloco : blocos) {
            bloco = bloco.trim();
            if (!bloco.isEmpty()) {
                slides.add(bloco);
            }
        }

        return slides.isEmpty() ? List.of(texto) : slides;
    }

    /**
     * Mostra slide atual no preview (e projeta se não estiver no modo CTRL)
     */
    private void showCurrentSlide() {
        if (currentSlides.isEmpty()) {
            previewLabel.setText("Nenhum conteúdo selecionado");
            btnPrevSlide.setDisable(true);
            btnNextSlide.setDisable(true);
            return;
        }

        // Atualizar preview
        previewLabel.setText(currentSlides.get(currentSlideIndex));

        // Atualizar botões
        btnPrevSlide.setDisable(currentSlideIndex == 0);
        btnNextSlide.setDisable(currentSlideIndex >= currentSlides.size() - 1);

        updateSlideCounter();

        // PROJETAR automaticamente (se não estiver segurando CTRL)
        if (!ctrlPressed && projectionController != null) {
            projetarSlideAtual();
        }
    }

    /**
     * Projeta o slide atual na janela de projeção
     */
    private void projetarSlideAtual() {
        if (projectionController != null && !currentSlides.isEmpty()) {
            String conteudo = currentSlides.get(currentSlideIndex);
            projectionController.mostrarConteudo(conteudo, currentReferencia);
            System.out.println("Slide projetado: " + (currentSlideIndex + 1) + "/" + currentSlides.size());
        }
    }

    /**
     * Atualiza contador de slides
     */
    private void updateSlideCounter() {
        if (currentSlides.isEmpty()) {
            slideCountLabel.setText("0 / 0");
        } else {
            slideCountLabel.setText((currentSlideIndex + 1) + " / " + currentSlides.size());
        }
    }

    /**
     * Adiciona item ao histórico
     */
    private void addToHistorico(ItemHistorico.TipoItem tipo, String descricao, Object conteudo) {
        ItemHistorico item = new ItemHistorico(tipo, descricao, conteudo);
        historicoList.add(item);
        System.out.println("Adicionado ao histórico: " + item.toString());
    }

    /**
     * Recarrega item do histórico
     */
    private void reloadFromHistorico(ItemHistorico item) {
        Object conteudo = item.getConteudo();

        if (conteudo instanceof Hino) {
            loadHinoToPreview((Hino) conteudo);
        } else if (conteudo instanceof Louvor) {
            loadLouvorToPreview((Louvor) conteudo);
        } else if (conteudo instanceof Versiculo) {
            loadVersiculoToPreview((Versiculo) conteudo);
        }

        updateFooterStatus("Recarregado do histórico: " + item.getDescricao());
    }

    // ==================== HANDLERS DE ATALHOS DE TECLADO ====================

    /**
     * Handler de tecla pressionada
     * IMPORTANTE: CTRL + setas = navega SÓ no preview (não projeta)
     */
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.CONTROL) {
            ctrlPressed = true;
            System.out.println("CTRL pressionado - modo navegação preview");
        }

        if (event.getCode() == KeyCode.LEFT) {
            handlePreviousSlide();
            event.consume();
        } else if (event.getCode() == KeyCode.RIGHT) {
            handleNextSlide();
            event.consume();
        } else if (event.getCode() == KeyCode.F9) {
            handleToggleProjecao();
            event.consume();
        } else if (event.getCode() == KeyCode.F10) {
            handleTelaPreta();
            event.consume();
        }
    }

    /**
     * Handler de tecla solta
     * IMPORTANTE: Quando SOLTA CTRL, projeta o slide atual
     */
    private void handleKeyRelease(KeyEvent event) {
        if (event.getCode() == KeyCode.CONTROL) {
            ctrlPressed = false;
            System.out.println("CTRL solto - projetando slide atual");
            projetarSlideAtual();
        }
    }

    // ==================== HANDLERS DE BOTÕES ====================

    @FXML
    private void handlePreviousSlide() {
        if (currentSlideIndex > 0) {
            currentSlideIndex--;
            showCurrentSlide();
            updateFooterStatus("Slide anterior");
        }
    }

    @FXML
    private void handleNextSlide() {
        if (currentSlideIndex < currentSlides.size() - 1) {
            currentSlideIndex++;
            showCurrentSlide();
            updateFooterStatus("Próximo slide");
        }
    }

    @FXML
    private void handleProjetar() {
        if (projectionController != null) {
            projectionController.toggleConteudo();
            updateBtnProjetarText();
            updateFooterStatus(projectionController.isBlackScreen() ? "Projeção oculta" : "Projetando");
        }
    }

    private void handleToggleProjecao() {
        handleProjetar();
    }

    @FXML
    private void handleTelaPreta() {
        if (projectionController != null) {
            projectionController.mostrarTelaPreta();
            updateFooterStatus("Tela preta");
            updateBtnProjetarText();
        }
    }

    @FXML
    private void handleFundo() {
        if (selectedBackgroundImage != null && projectionController != null) {
            projectionController.aplicarFundo(selectedBackgroundImage);
            updateFooterStatus("Fundo aplicado");
        } else {
            showAlert("Fundo", "Selecione uma imagem primeiro", Alert.AlertType.WARNING);
        }
    }

    private void updateBtnProjetarText() {
        if (projectionController != null && projectionController.isBlackScreen()) {
            btnProjetar.setText("▶ PROJETAR");
        } else {
            btnProjetar.setText("⏸ OCULTAR");
        }
    }

    @FXML
    private void handleBuscarVersiculo() {
        String input = versiculoInputField.getText();
        if (input == null || input.trim().isEmpty()) {
            showAlert("Buscar Versículo", "Digite uma referência (ex: João 3:16)", Alert.AlertType.WARNING);
            return;
        }

        // Formatar referência
        String referenciaFormatada = Versiculo.formatarReferencia(input);
        String versaoSelecionada = bibliaVersionComboBox.getValue();
        String versaoSigla = versaoSelecionada.substring(0, 3); // "ARC", "ARA", etc.

        // Buscar no banco
        Versiculo versiculo = versiculoDAO.findByReferencia(referenciaFormatada, versaoSigla);

        if (versiculo != null) {
            loadVersiculoToPreview(versiculo);
            addToHistorico(ItemHistorico.TipoItem.VERSICULO, referenciaFormatada + " (" + versaoSigla + ")", versiculo);
            projetarSlideAtual(); // Projetar imediatamente
        } else {
            showAlert("Versículo Não Encontrado",
                "Versículo '" + referenciaFormatada + "' não encontrado na versão " + versaoSigla,
                Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleLimparHistorico() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Limpar Histórico");
        confirm.setHeaderText("Deseja limpar o histórico do culto?");
        confirm.setContentText("Esta ação não pode ser desfeita.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                historicoList.clear();
                updateFooterStatus("Histórico limpo");
            }
        });
    }

    @FXML
    private void handleExportarHistorico() {
        if (historicoList.isEmpty()) {
            showAlert("Exportar", "Histórico vazio", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar Histórico");
        fileChooser.setInitialFileName("historico_culto_" +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Arquivo de Texto", "*.txt")
        );

        File file = fileChooser.showSaveDialog(historicoListView.getScene().getWindow());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("CADEALT MASTER - Histórico do Culto\n");
                writer.write("Data: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n");
                writer.write("Tipo: " + historicoTipoComboBox.getValue() + "\n\n");

                int contador = 1;
                for (ItemHistorico item : historicoList) {
                    writer.write(item.toExportString(contador++) + "\n");
                }

                updateFooterStatus("Histórico exportado: " + file.getName());
                showAlert("Exportar", "Histórico exportado com sucesso!", Alert.AlertType.INFORMATION);

            } catch (Exception e) {
                showAlert("Erro", "Erro ao exportar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleSalvarHistorico() {
        if (historicoList.isEmpty()) {
            showAlert("Salvar", "Histórico vazio", Alert.AlertType.WARNING);
            return;
        }

        List<String> itens = new ArrayList<>();
        for (ItemHistorico item : historicoList) {
            itens.add(item.toString());
        }

        int id = historicoDAO.salvar(historicoTipoComboBox.getValue(), itens);

        if (id > 0) {
            showAlert("Salvar", "Histórico salvo com sucesso!", Alert.AlertType.INFORMATION);
            updateFooterStatus("Histórico salvo (ID: " + id + ")");
        } else {
            showAlert("Erro", "Erro ao salvar histórico", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleEscolherFundo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Imagem de Fundo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.bmp"),
            new FileChooser.ExtensionFilter("Todos", "*.*")
        );

        selectedBackgroundImage = fileChooser.showOpenDialog(fundoImagemField.getScene().getWindow());

        if (selectedBackgroundImage != null) {
            fundoImagemField.setText(selectedBackgroundImage.getName());
            updateFooterStatus("Imagem selecionada: " + selectedBackgroundImage.getName());
        }
    }

    @FXML
    private void handleAplicarFundo() {
        handleFundo();
    }

    @FXML
    private void handleAdicionarLouvor() {
        // TODO: Abrir dialog de adicionar louvor
        showAlert("Adicionar Louvor", "Funcionalidade em desenvolvimento", Alert.AlertType.INFORMATION);
    }

    // ==================== UTILITÁRIOS ====================

    private void startFooterClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            footerTimeLabel.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            footerDateLabel.setText(now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }), new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void updateFooterStatus(String message) {
        footerStatusLabel.setText(message);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
