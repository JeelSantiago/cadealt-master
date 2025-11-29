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
 * Classe principal da aplicação
 *
 * FLUXO CORRETO DE INICIALIZAÇÃO:
 * 1. Abrir e MOSTRAR tela de CONTROLE (main.fxml)
 * 2. Verificar quantidade de monitores (Platform.runLater)
 * 3. Se 2+ monitores: Abrir janela de projeção no secundário
 * 4. Se 1 monitor: Alert + desabilitar botões de projeção (SEM abrir janela de projeção)
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

        System.out.println("=== Iniciando CADEALT MASTER ===");
        System.out.println("JavaFX Version: " + System.getProperty("javafx.version"));
        System.out.println("Java Version: " + System.getProperty("java.version"));

        // ============================================================
        // PASSO 1: CARREGAR E MOSTRAR TELA DE CONTROLE (SEMPRE PRIMEIRO!)
        // ============================================================
        boolean controlScreenLoaded = initRootLayout();

        if (!controlScreenLoaded) {
            System.err.println("ERRO CRÍTICO: Tela de controle NÃO foi carregada!");
            System.err.println("Abortando inicialização...");
            Platform.exit();
            return;
        }

        // ============================================================
        // PASSO 2: VERIFICAR MONITORES (DEPOIS que a tela aparecer)
        // ============================================================
        // Usar Platform.runLater com DELAY para garantir que a tela de controle apareça primeiro
        Platform.runLater(() -> {
            System.out.println("\n=== Verificação de monitores (após tela de controle estar visível) ===");
            checkSecondaryMonitor();
        });
    }

    /**
     * Inicializa o layout principal da aplicação (janela de controle)
     * ESTA É A PRIMEIRA JANELA - SEMPRE APARECE
     * @return true se a tela foi carregada com sucesso, false caso contrário
     */
    private boolean initRootLayout() {
        try {
            System.out.println("\n>>> PASSO 1: Carregando tela de controle (main.fxml)...");

            // Carrega o FXML do layout principal
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CadealtMaster.class.getResource("/fxml/main.fxml"));

            System.out.println("    - FXML path: " + CadealtMaster.class.getResource("/fxml/main.fxml"));

            rootLayout = loader.load();
            System.out.println("    ✓ FXML carregado com sucesso");

            // Obter controller
            mainViewController = loader.getController();
            System.out.println("    ✓ Controller obtido: " + (mainViewController != null ? "OK" : "FALHOU"));

            // Cria a cena com tamanho otimizado para 3 colunas
            Scene scene = new Scene(rootLayout, 1400, 900);
            System.out.println("    ✓ Scene criada (1400x900)");

            // Adiciona CSS dark mode (com tratamento de erro)
            try {
                String cssPath = getClass().getResource("/css/main.css").toExternalForm();
                scene.getStylesheets().add(cssPath);
                System.out.println("    ✓ CSS aplicado: " + cssPath);
            } catch (Exception cssError) {
                System.err.println("    ⚠ Aviso: Erro ao carregar CSS (continuando sem estilo): " + cssError.getMessage());
            }

            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(700);
            System.out.println("    ✓ Scene aplicada ao Stage");

            // Posicionar no monitor primário
            Rectangle2D primaryBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX(primaryBounds.getMinX());
            primaryStage.setY(primaryBounds.getMinY());
            System.out.println("    ✓ Posicionado no monitor primário: " + primaryBounds);

            // Ao fechar a janela principal, fechar tudo
            primaryStage.setOnCloseRequest(event -> {
                System.out.println("\n=== Fechando aplicação... ===");
                if (projectionStage != null) {
                    projectionStage.close();
                }
                System.exit(0);
            });

            // ============================================================
            // MOSTRAR A TELA DE CONTROLE (CRÍTICO!)
            // ============================================================
            primaryStage.show();
            primaryStage.toFront();
            primaryStage.requestFocus();

            System.out.println("    ✓✓✓ TELA DE CONTROLE ABERTA E VISÍVEL ✓✓✓");
            System.out.println("    - Stage showing: " + primaryStage.isShowing());
            System.out.println("    - Stage focused: " + primaryStage.isFocused());
            System.out.println(">>> PASSO 1: CONCLUÍDO COM SUCESSO\n");

            return true;

        } catch (Exception e) {
            System.err.println("\n!!! ERRO CRÍTICO ao carregar tela de controle !!!");
            System.err.println("Mensagem: " + e.getMessage());
            System.err.println("Tipo: " + e.getClass().getName());
            System.err.println("Stack trace:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica se há monitor secundário conectado
     * - Se SIM (2+ monitores): Abre janela de projeção no secundário
     * - Se NÃO (1 monitor): Mostra Alert + desabilita botões (NÃO abre janela de projeção)
     */
    private void checkSecondaryMonitor() {
        ObservableList<Screen> screens = Screen.getScreens();

        System.out.println(">>> PASSO 2: Verificando monitores conectados...");
        System.out.println("    - Total de monitores detectados: " + screens.size());

        // Listar todos os monitores
        for (int i = 0; i < screens.size(); i++) {
            Screen screen = screens.get(i);
            Rectangle2D bounds = screen.getBounds();
            System.out.println("    - Monitor " + (i + 1) + ": " +
                             (int)bounds.getWidth() + "x" + (int)bounds.getHeight() +
                             " @ (" + (int)bounds.getMinX() + ", " + (int)bounds.getMinY() + ")" +
                             (screen == Screen.getPrimary() ? " [PRIMÁRIO]" : ""));
        }

        if (screens.size() < 2) {
            // ========================================
            // APENAS 1 MONITOR - NÃO ABRIR PROJEÇÃO
            // ========================================
            System.out.println("\n    ⚠ CENÁRIO: Apenas 1 monitor detectado");
            System.out.println("    ✗ Janela de projeção NÃO será criada");
            System.out.println("    ✓ Tela de controle permanece funcionando");

            // Mostrar Alert informativo
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
                System.out.println("    ✓ Botões de projeção desabilitados");
            }

            System.out.println(">>> PASSO 2: CONCLUÍDO - Aplicação iniciada SEM projeção (modo controle apenas)\n");

        } else {
            // ========================================
            // 2+ MONITORES - ABRIR JANELA DE PROJEÇÃO
            // ========================================
            System.out.println("\n    ✓ CENÁRIO: Monitor secundário detectado");
            Screen secondaryScreen = screens.get(1); // Monitor secundário
            Rectangle2D secondaryBounds = secondaryScreen.getBounds();
            System.out.println("    - Monitor secundário selecionado: " +
                             (int)secondaryBounds.getWidth() + "x" + (int)secondaryBounds.getHeight());

            openProjectionWindow(secondaryScreen);
        }
    }

    /**
     * Abre a janela de projeção no monitor especificado
     * IMPORTANTE: Este método SÓ é chamado quando há 2+ monitores
     */
    private void openProjectionWindow(Screen targetScreen) {
        try {
            System.out.println("\n    >>> Abrindo janela de projeção...");

            // Carrega FXML da projeção
            FXMLLoader projectionLoader = new FXMLLoader();
            projectionLoader.setLocation(CadealtMaster.class.getResource("/fxml/projection.fxml"));
            StackPane projectionRoot = projectionLoader.load();
            projectionController = projectionLoader.getController();
            System.out.println("        ✓ FXML de projeção carregado");

            // Passa referência do controller de projeção para o MainViewController
            if (mainViewController != null) {
                mainViewController.setProjectionController(projectionController);
                System.out.println("        ✓ Controller de projeção vinculado ao controller principal");
            }

            // Cria Stage da projeção
            projectionStage = new Stage();
            projectionStage.setTitle("CADEALT MASTER - Projeção");
            projectionStage.initStyle(StageStyle.UNDECORATED); // Sem bordas

            // Posicionar no monitor secundário
            Rectangle2D bounds = targetScreen.getBounds();
            projectionStage.setX(bounds.getMinX());
            projectionStage.setY(bounds.getMinY());
            projectionStage.setWidth(bounds.getWidth());
            projectionStage.setHeight(bounds.getHeight());
            System.out.println("        ✓ Stage de projeção criado e posicionado: " +
                             (int)bounds.getMinX() + ", " + (int)bounds.getMinY() +
                             " (" + (int)bounds.getWidth() + "x" + (int)bounds.getHeight() + ")");

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

            System.out.println("        ✓✓✓ JANELA DE PROJEÇÃO ABERTA EM FULLSCREEN ✓✓✓");
            System.out.println(">>> PASSO 2: CONCLUÍDO - Janela de projeção aberta no monitor secundário\n");
            System.out.println("=================================================================");
            System.out.println("    CADEALT MASTER INICIADO COM SUCESSO (MODO COMPLETO)");
            System.out.println("    - Tela de controle: ATIVA no monitor primário");
            System.out.println("    - Janela de projeção: ATIVA no monitor secundário");
            System.out.println("=================================================================\n");

        } catch (Exception e) {
            System.err.println("\n!!! ERRO ao abrir janela de projeção !!!");
            System.err.println("Mensagem: " + e.getMessage());
            System.err.println("Tipo: " + e.getClass().getName());
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
                System.out.println("    ✓ Botões de projeção desabilitados (erro ao abrir janela)");
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
