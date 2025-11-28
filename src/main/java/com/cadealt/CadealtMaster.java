package com.cadealt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * CADEALT MASTER - Sistema de Projeção para Igrejas
 * Classe principal da aplicação
 */
public class CadealtMaster extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("CADEALT MASTER - Sistema de Projeção");

        initRootLayout();
    }

    /**
     * Inicializa o layout principal da aplicação
     */
    private void initRootLayout() {
        try {
            // Carrega o FXML do layout principal
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CadealtMaster.class.getResource("/fxml/main.fxml"));
            rootLayout = loader.load();

            // Cria a cena com tamanho otimizado para 3 colunas
            Scene scene = new Scene(rootLayout, 1400, 900);

            // Adiciona CSS dark mode
            scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(700);
            primaryStage.show();

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
     * Método principal
     * @param args argumentos da linha de comando
     */
    public static void main(String[] args) {
        launch(args);
    }
}
