package com.cadealt;

import com.cadealt.controller.MainViewController;
import com.cadealt.controller.ProjectionController;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Rectangle2D;

/**
 * CADEALT MASTER - Sistema de Projeção para Igrejas
 * Classe principal da aplicação
 *
 * FLUXO CORRETO DE INICIALIZAÇÃO:
 * 1. Abrir tela de CONTROLE (main.fxml)
 * 2. Mostrar tela de controle
 * 3. Verificar quantidade de monitores
 * 4. Se 2+ monitores: Abrir janela de projeção no secundário
 * 5. Se 1 monitor: Alert + desabilitar botões de projeção
 */
public class CadealtMaster extends Application {

    private Stage primaryStage;
    private Stage projectionStage;
    private BorderPane rootLayout;
    private MainViewController mainViewController;
    private ProjectionController projectionController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("CADEALT MASTER - Sistema de Projeção");

        // ============================================================
        // CORREÇÃO 1: ABRIR TELA DE CONTROLE PRIMEIRO
        // ============================================================
        System.out.println("=== Iniciando CADEALT MASTER ===");

        // 1. Abrir tela de CONTROLE primeiro
        initRootLayout();

        // 2. MOSTRAR a tela de controle
        primaryStage.show();
        System.out.println("✓ Tela de controle aberta");

        // 3. Verificar monitores e abrir projeção (se houver monitor secundário)
        checkSecondaryMonitor();
    }

    /**
     * Inicializa o layout principal da aplicação (janela de controle)
     * ESTA É A PRIMEIRA JANELA A ABRIR
     */
    private void initRootLayout() {
        try {
            System.out.println("Carregando tela de controle (main.fxml)...");

            // Carrega o FXML do layout principal
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CadealtMaster.class.getResource("/fxml/main.fxml"));
            rootLayout = loader.load();

            // Obter controller
            mainViewController = loader.getController();

            // Cria a cena com tamanho otimizado para 3 colunas
            Scene scene = new Scene(rootLayout, 1400, 900);

            // Adiciona CSS dark mode
            scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(700);

            // Posicionar no monitor primário
            Rectangle2D primaryBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX(primaryBounds.getMinX());
            primaryStage.setY(primaryBounds.getMinY());

            // Ao fechar a janela principal, fechar tudo
            primaryStage.setOnCloseRequest(event -> {
                System.out.println("Fechando aplicação...");
                if (projectionStage != null) {
                    projectionStage.close();
                }
                System.exit(0);
            });

            System.out.println("✓ Tela de controle carregada");

        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO ao carregar tela de controle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ============================================================
     * CORREÇÃO 2: VERIFICAÇÃO DE MONITOR SECUNDÁRIO
     * ============================================================
     * Verifica se há monitor secundário conectado
     * - Se SIM (2+ monitores): Abre janela de projeção no secundário
     * - Se NÃO (1 monitor): Mostra Alert + desabilita botões de projeção
     */
    private void checkSecondaryMonitor() {
        ObservableList<Screen> screens = Screen.getScreens();

        System.out.println("Verificando monitores conectados...");
        System.out.println("Total de monitores: " + screens.size());

        if (screens.size() < 2) {
            // ========================================
            // APENAS 1 MONITOR - MOSTRAR ALERTA
            // ========================================
            System.out.println("⚠ Apenas 1 monitor detectado");

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Monitor Secundário Não Detectado");
            alert.setHeaderText("Para usar a projeção, é necessário conectar um monitor secundário.");
            alert.setContentText(
                "Como estender a tela:\n\n" +
                "1. Conecte o monitor/projetor\n" +
                "2. Pressione Win + P\n" +
                "3. Selecione \"Estender\"\n" +
                "4. Reinicie o programa\n\n" +
                "Obs: A tela de controle funcionará normalmente."
            );

            alert.showAndWait();

            // Desabilitar botões de projeção no MainViewController
            if (mainViewController != null) {
                mainViewController.disableProjectionMode();
            }

            System.out.println("✓ Aplicação iniciada sem projeção (modo controle apenas)");

        } else {
            // ========================================
            // 2+ MONITORES - ABRIR JANELA DE PROJEÇÃO
            // ========================================
            System.out.println("✓ Monitor secundário detectado");
            Screen secondaryScreen = screens.get(1); // Monitor secundário

            openProjectionWindow(secondaryScreen);
        }
    }

    /**
     * Abre a janela de projeção no monitor especificado
     * COMPORTAMENTO CRÍTICO:
     * - NUNCA fecha (ESC desabilitado)
     * - Fullscreen
     * - Always on top
     */
    private void openProjectionWindow(Screen targetScreen) {
        try {
            System.out.println("Abrindo janela de projeção...");

            // Carrega FXML da projeção
            FXMLLoader projectionLoader = new FXMLLoader();
            projectionLoader.setLocation(CadealtMaster.class.getResource("/fxml/projection.fxml"));
            StackPane projectionRoot = projectionLoader.load();
            projectionController = projectionLoader.getController();

            // Passa referência do controller de projeção para o MainViewController
            if (mainViewController != null) {
                mainViewController.setProjectionController(projectionController);
            }

            // Cria Stage da projeção
            projectionStage = new Stage();
            projectionStage.setTitle("CADEALT MASTER - Projeção");
            projectionStage.initStyle(StageStyle.UNDECORATED); // Sem bordas

            // Posicionar no monitor escolhido
            Rectangle2D bounds = targetScreen.getBounds();
            projectionStage.setX(bounds.getMinX());
            projectionStage.setY(bounds.getMinY());
            projectionStage.setWidth(bounds.getWidth());
            projectionStage.setHeight(bounds.getHeight());

            // Criar cena
            Scene projectionScene = new Scene(projectionRoot);

            // CRÍTICO: Desabilitar ESC para não fechar
            projectionScene.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case ESCAPE:
                    case F11:
                        event.consume(); // Bloqueia
                        System.out.println("ESC bloqueado na janela de projeção");
                        break;
                }
            });

            projectionStage.setScene(projectionScene);

            // SEMPRE fullscreen
            projectionStage.setFullScreen(true);
            projectionStage.setFullScreenExitHint(""); // Remove hint de "Press ESC to exit"

            // SEMPRE no topo
            projectionStage.setAlwaysOnTop(true);

            // Bloquear fechamento da janela
            projectionStage.setOnCloseRequest(event -> {
                event.consume(); // Impede fechar
                System.out.println("Tentativa de fechar janela de projeção bloqueada");
            });

            // Mostrar janela
            projectionStage.show();

            System.out.println("✓ Janela de projeção aberta em fullscreen no monitor secundário");
            System.out.println("=== CADEALT MASTER iniciado com sucesso ===");

        } catch (Exception e) {
            System.err.println("ERRO ao abrir janela de projeção: " + e.getMessage());
            e.printStackTrace();

            // Se falhar, informar ao usuário
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro ao Abrir Projeção");
            alert.setHeaderText("Não foi possível abrir a janela de projeção");
            alert.setContentText("Erro: " + e.getMessage() + "\n\nA tela de controle funcionará normalmente.");
            alert.showAndWait();

            // Desabilitar projeção
            if (mainViewController != null) {
                mainViewController.disableProjectionMode();
            }
        }
    }

    /**
     * Retorna o Stage principal
     * @return Stage principal
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Retorna o Stage de projeção
     * @return Stage de projeção
     */
    public Stage getProjectionStage() {
        return projectionStage;
    }

    /**
     * Método principal
     * @param args argumentos da linha de comando
     */
    public static void main(String[] args) {
        launch(args);
    }
}
