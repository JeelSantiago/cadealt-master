package com.cadealt;

import com.cadealt.controller.MainViewController;
import com.cadealt.controller.ProjectionController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Rectangle2D;

/**
 * CADEALT MASTER - Sistema de Projeção para Igrejas
 * Classe principal da aplicação
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

        // CRÍTICO: Abrir janela de projeção ANTES da janela principal
        initProjectionWindow();

        // Depois abrir janela principal
        initRootLayout();
    }

    /**
     * Inicializa a janela de projeção
     * COMPORTAMENTO CRÍTICO:
     * - Abre AUTOMATICAMENTE no início
     * - NUNCA fecha (ESC desabilitado)
     * - Fullscreen no monitor secundário (ou primário se só tiver 1)
     */
    private void initProjectionWindow() {
        try {
            System.out.println("Iniciando janela de projeção...");

            // Carrega FXML da projeção
            FXMLLoader projectionLoader = new FXMLLoader();
            projectionLoader.setLocation(CadealtMaster.class.getResource("/fxml/projection.fxml"));
            StackPane projectionRoot = projectionLoader.load();
            projectionController = projectionLoader.getController();

            // Cria Stage da projeção
            projectionStage = new Stage();
            projectionStage.setTitle("CADEALT MASTER - Projeção");
            projectionStage.initStyle(StageStyle.UNDECORATED); // Sem bordas

            // Detectar monitores
            Screen targetScreen;
            if (Screen.getScreens().size() > 1) {
                // Se tiver mais de 1 monitor, usar o secundário
                targetScreen = Screen.getScreens().get(1);
                System.out.println("Monitor secundário detectado");
            } else {
                // Se tiver apenas 1, usar o primário
                targetScreen = Screen.getPrimary();
                System.out.println("Usando monitor primário");
            }

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

            System.out.println("Janela de projeção aberta em fullscreen");

        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO ao abrir janela de projeção: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inicializa o layout principal da aplicação (janela de controle)
     */
    private void initRootLayout() {
        try {
            // Carrega o FXML do layout principal
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CadealtMaster.class.getResource("/fxml/main.fxml"));
            rootLayout = loader.load();

            // Obter controller e passar referência da projeção
            mainViewController = loader.getController();
            mainViewController.setProjectionController(projectionController);

            // Cria a cena com tamanho otimizado para 3 colunas
            Scene scene = new Scene(rootLayout, 1400, 900);

            // Adiciona CSS dark mode
            scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(700);

            // Posicionar no monitor primário
            if (Screen.getScreens().size() > 1) {
                Rectangle2D primaryBounds = Screen.getPrimary().getVisualBounds();
                primaryStage.setX(primaryBounds.getMinX());
                primaryStage.setY(primaryBounds.getMinY());
            }

            // Ao fechar a janela principal, fechar tudo
            primaryStage.setOnCloseRequest(event -> {
                System.out.println("Fechando aplicação...");
                if (projectionStage != null) {
                    projectionStage.close();
                }
                System.exit(0);
            });

            primaryStage.show();

            System.out.println("Janela principal aberta");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao inicializar a aplicação: " + e.getMessage());
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
