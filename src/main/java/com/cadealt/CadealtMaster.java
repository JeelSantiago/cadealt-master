package com.cadealt;

import com.cadealt.controller.MainViewController;
import com.cadealt.controller.ProjectionController;
import javafx.application.Application;
import javafx.application.Platform;
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
 * Versão SIMPLIFICADA e ROBUSTA
 *
 * FLUXO:
 * 1. Abrir tela de controle (main.fxml) - SEMPRE PRIMEIRO
 * 2. Verificar monitores
 * 3. Se 2+ monitores: Abrir projeção no secundário
 * 4. Se 1 monitor: Desabilitar projeção
 */
public class CadealtMaster extends Application {

    private Stage primaryStage;
    private Stage projectionStage;
    private MainViewController mainViewController;
    private ProjectionController projectionController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        try {
            System.out.println("=================================================================");
            System.out.println("   CADEALT MASTER - Sistema de Projeção para Igrejas");
            System.out.println("=================================================================");
            System.out.println("JavaFX: " + System.getProperty("javafx.version"));
            System.out.println("Java: " + System.getProperty("java.version"));
            System.out.println();

            // PASSO 1: CARREGAR TELA DE CONTROLE
            loadControlScreen();

            // PASSO 2: VERIFICAR MONITORES (depois)
            Platform.runLater(this::checkMonitors);

        } catch (Exception e) {
            System.err.println("\n!!! ERRO FATAL na inicialização !!!");
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro Fatal");
            alert.setHeaderText("Não foi possível iniciar o CADEALT MASTER");
            alert.setContentText("Erro: " + e.getMessage());
            alert.showAndWait();

            Platform.exit();
        }
    }

    /**
     * Carrega e exibe a tela de controle (SEMPRE PRIMEIRO)
     */
    private void loadControlScreen() throws Exception {
        System.out.println(">>> PASSO 1: Carregando tela de controle...");

        // Carregar FXML
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/main.fxml"));

        BorderPane root = loader.load();
        mainViewController = loader.getController();

        System.out.println("    ✓ FXML carregado");
        System.out.println("    ✓ Controller: " + (mainViewController != null ? "OK" : "NULL"));

        // Criar cena
        Scene scene = new Scene(root, 1400, 900);

        // Aplicar CSS (com fallback)
        try {
            scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
            System.out.println("    ✓ CSS aplicado");
        } catch (Exception e) {
            System.out.println("    ⚠ CSS não encontrado (continuando sem estilo)");
        }

        // Configurar stage
        primaryStage.setTitle("CADEALT MASTER - Sistema de Projeção");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(700);

        // Posicionar no monitor primário
        Rectangle2D primaryBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(primaryBounds.getMinX());
        primaryStage.setY(primaryBounds.getMinY());

        // Fechar tudo ao fechar janela principal
        primaryStage.setOnCloseRequest(e -> {
            if (projectionStage != null) {
                projectionStage.close();
            }
            System.exit(0);
        });

        // MOSTRAR TELA
        primaryStage.show();
        primaryStage.toFront();

        System.out.println("    ✓✓✓ TELA DE CONTROLE VISÍVEL ✓✓✓");
        System.out.println();
    }

    /**
     * Verifica monitores e decide se abre projeção
     */
    private void checkMonitors() {
        System.out.println(">>> PASSO 2: Verificando monitores...");

        ObservableList<Screen> screens = Screen.getScreens();
        System.out.println("    - Monitores detectados: " + screens.size());

        // Listar monitores
        for (int i = 0; i < screens.size(); i++) {
            Screen screen = screens.get(i);
            Rectangle2D bounds = screen.getBounds();
            System.out.println("    - Monitor " + (i + 1) + ": " +
                    (int) bounds.getWidth() + "x" + (int) bounds.getHeight() +
                    (screen == Screen.getPrimary() ? " [PRIMÁRIO]" : ""));
        }

        if (screens.size() < 2) {
            // APENAS 1 MONITOR
            handleSingleMonitor();
        } else {
            // 2+ MONITORES
            openProjection(screens.get(1));
        }
    }

    /**
     * Trata cenário de 1 monitor apenas
     */
    private void handleSingleMonitor() {
        System.out.println("\n    ⚠ Apenas 1 monitor detectado");
        System.out.println("    ✗ Projeção desabilitada");

        // Desabilitar botões de projeção
        if (mainViewController != null) {
            try {
                mainViewController.disableProjectionMode();
                System.out.println("    ✓ Botões desabilitados");
            } catch (Exception e) {
                System.err.println("    ⚠ Erro ao desabilitar botões: " + e.getMessage());
            }
        }

        // Mostrar alert
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Monitor Secundário Não Detectado");
        alert.setHeaderText("Para usar a projeção, conecte um monitor secundário");
        alert.setContentText(
                "Como estender a tela:\n\n" +
                        "1. Conecte o monitor/projetor\n" +
                        "2. Pressione Win + P\n" +
                        "3. Selecione 'Estender'\n" +
                        "4. Reinicie o programa\n\n" +
                        "A tela de controle funcionará normalmente."
        );
        alert.showAndWait();

        System.out.println(">>> INICIALIZAÇÃO CONCLUÍDA (modo controle apenas)\n");
    }

    /**
     * Abre janela de projeção no monitor secundário
     */
    private void openProjection(Screen secondaryScreen) {
        try {
            System.out.println("\n    ✓ Monitor secundário detectado");
            System.out.println("    >>> Abrindo janela de projeção...");

            // Carregar FXML da projeção
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/projection.fxml"));
            StackPane projectionRoot = loader.load();
            projectionController = loader.getController();

            System.out.println("        ✓ FXML de projeção carregado");

            // Vincular controller
            if (mainViewController != null) {
                mainViewController.setProjectionController(projectionController);
                System.out.println("        ✓ Controllers vinculados");
            }

            // Criar stage de projeção
            projectionStage = new Stage();
            projectionStage.setTitle("CADEALT MASTER - Projeção");
            projectionStage.initStyle(StageStyle.UNDECORATED);

            // Posicionar no monitor secundário
            Rectangle2D bounds = secondaryScreen.getBounds();
            projectionStage.setX(bounds.getMinX());
            projectionStage.setY(bounds.getMinY());
            projectionStage.setWidth(bounds.getWidth());
            projectionStage.setHeight(bounds.getHeight());

            // Criar cena
            Scene scene = new Scene(projectionRoot);

            // Bloquear ESC
            scene.setOnKeyPressed(event -> {
                if (event.getCode().toString().equals("ESCAPE") ||
                        event.getCode().toString().equals("F11")) {
                    event.consume();
                }
            });

            projectionStage.setScene(scene);
            projectionStage.setFullScreen(true);
            projectionStage.setFullScreenExitHint("");
            projectionStage.setAlwaysOnTop(true);

            // Bloquear fechamento
            projectionStage.setOnCloseRequest(event -> event.consume());

            // Mostrar
            projectionStage.show();

            System.out.println("        ✓✓✓ PROJEÇÃO ABERTA EM FULLSCREEN ✓✓✓");
            System.out.println();
            System.out.println("=================================================================");
            System.out.println("    ✓ CADEALT MASTER INICIADO COM SUCESSO");
            System.out.println("    - Tela de controle: ATIVA (monitor primário)");
            System.out.println("    - Janela de projeção: ATIVA (monitor secundário)");
            System.out.println("=================================================================\n");

        } catch (Exception e) {
            System.err.println("\n    !!! ERRO ao abrir janela de projeção !!!");
            e.printStackTrace();

            // Desabilitar projeção em caso de erro
            if (mainViewController != null) {
                try {
                    mainViewController.disableProjectionMode();
                } catch (Exception ex) {
                    // Ignora
                }
            }

            // Mostrar erro ao usuário
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro ao Abrir Projeção");
            alert.setHeaderText("Não foi possível abrir a janela de projeção");
            alert.setContentText("Erro: " + e.getMessage() + "\n\nA tela de controle funcionará normalmente.");
            alert.showAndWait();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Stage getProjectionStage() {
        return projectionStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
