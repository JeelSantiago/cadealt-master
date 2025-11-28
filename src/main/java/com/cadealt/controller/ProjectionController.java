package com.cadealt.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Background;

import java.io.File;

/**
 * Controller da Janela de Projeção
 *
 * COMPORTAMENTO CRÍTICO:
 * - NUNCA fecha (ESC desabilitado)
 * - SEMPRE fullscreen
 * - SEMPRE visível (always on top)
 * - Sincronizada com o preview
 */
public class ProjectionController {

    @FXML
    private StackPane rootPane;

    @FXML
    private StackPane backgroundPane;

    @FXML
    private Label contentLabel;

    @FXML
    private Label referenciaLabel;

    private boolean isBlackScreen = false;
    private String lastContent = "";
    private String lastReferencia = "";

    @FXML
    public void initialize() {
        System.out.println("ProjectionController inicializado");

        // Desabilitar ESC e outras teclas que possam fechar a janela
        rootPane.setOnKeyPressed(this::handleKeyPress);

        // Ocultar conteúdo inicialmente
        mostrarTelaPreta();
    }

    /**
     * Handler de teclas - DESABILITA ESC
     */
    private void handleKeyPress(KeyEvent event) {
        // CRÍTICO: ESC não faz NADA
        if (event.getCode() == KeyCode.ESCAPE) {
            event.consume(); // Bloqueia o ESC
            System.out.println("ESC bloqueado na janela de projeção");
        }

        // F11 também bloqueado (não sai do fullscreen)
        if (event.getCode() == KeyCode.F11) {
            event.consume();
        }
    }

    /**
     * Mostra conteúdo na projeção
     * @param content Texto a ser exibido
     * @param referencia Referência (hino/versículo) - null para louvores
     */
    public void mostrarConteudo(String content, String referencia) {
        this.lastContent = content != null ? content : "";
        this.lastReferencia = referencia;

        contentLabel.setText(lastContent);

        // IMPORTANTE: Louvores NÃO mostram referência
        if (referencia != null && !referencia.isEmpty()) {
            referenciaLabel.setText(referencia);
            referenciaLabel.setVisible(true);
        } else {
            referenciaLabel.setText("");
            referenciaLabel.setVisible(false);
        }

        contentLabel.setVisible(true);
        isBlackScreen = false;

        System.out.println("Conteúdo projetado: " + (content != null ? content.substring(0, Math.min(50, content.length())) : "vazio"));
        if (referencia != null) {
            System.out.println("Referência: " + referencia);
        }
    }

    /**
     * Mostra tela preta (oculta conteúdo)
     */
    public void mostrarTelaPreta() {
        contentLabel.setVisible(false);
        referenciaLabel.setVisible(false);
        isBlackScreen = true;
        System.out.println("Tela preta ativada");
    }

    /**
     * Toggle entre mostrar conteúdo e tela preta
     */
    public void toggleConteudo() {
        if (isBlackScreen) {
            // Restaurar último conteúdo
            contentLabel.setText(lastContent);
            contentLabel.setVisible(true);

            if (lastReferencia != null && !lastReferencia.isEmpty()) {
                referenciaLabel.setText(lastReferencia);
                referenciaLabel.setVisible(true);
            }

            isBlackScreen = false;
            System.out.println("Conteúdo restaurado");
        } else {
            mostrarTelaPreta();
        }
    }

    /**
     * Verifica se está em tela preta
     */
    public boolean isBlackScreen() {
        return isBlackScreen;
    }

    /**
     * Aplica imagem de fundo
     * @param imageFile Arquivo de imagem
     */
    public void aplicarFundo(File imageFile) {
        if (imageFile != null && imageFile.exists()) {
            try {
                Image image = new Image(imageFile.toURI().toString());

                BackgroundImage backgroundImage = new BackgroundImage(
                    image,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(
                        BackgroundSize.AUTO,
                        BackgroundSize.AUTO,
                        false,
                        false,
                        true, // Proportional
                        true  // Cover
                    )
                );

                backgroundPane.setBackground(new Background(backgroundImage));
                System.out.println("Fundo aplicado: " + imageFile.getName());

            } catch (Exception e) {
                System.err.println("Erro ao aplicar fundo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Remove imagem de fundo (volta para preto)
     */
    public void removerFundo() {
        backgroundPane.setStyle("-fx-background-color: #000000;");
        backgroundPane.setBackground(null);
        System.out.println("Fundo removido");
    }

    /**
     * Limpa todo conteúdo
     */
    public void limpar() {
        contentLabel.setText("");
        referenciaLabel.setText("");
        lastContent = "";
        lastReferencia = "";
        mostrarTelaPreta();
    }
}
