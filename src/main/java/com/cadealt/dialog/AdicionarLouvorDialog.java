package com.cadealt.dialog;

import com.cadealt.dao.LouvorDAO;
import com.cadealt.model.Louvor;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * Dialog para adicionar novo louvor
 */
public class AdicionarLouvorDialog extends Dialog<Louvor> {

    private TextField tituloField;
    private TextField autorField;
    private TextArea letraArea;

    public AdicionarLouvorDialog() {
        setTitle("Adicionar Novo Louvor");
        setHeaderText("Preencha as informações do louvor");

        // Criar grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Campos
        tituloField = new TextField();
        tituloField.setPromptText("Título do louvor");
        tituloField.setPrefWidth(400);

        autorField = new TextField();
        autorField.setPromptText("Autor (opcional)");
        autorField.setPrefWidth(400);

        letraArea = new TextArea();
        letraArea.setPromptText("Letra do louvor (use linha em branco para separar estrofes)");
        letraArea.setPrefHeight(300);
        letraArea.setPrefWidth(400);

        // Adicionar ao grid
        grid.add(new Label("Título:"), 0, 0);
        grid.add(tituloField, 1, 0);
        grid.add(new Label("Autor:"), 0, 1);
        grid.add(autorField, 1, 1);
        grid.add(new Label("Letra:"), 0, 2);
        grid.add(letraArea, 1, 2);

        getDialogPane().setContent(grid);

        // Botões
        ButtonType salvarButtonType = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

        // Validação
        Button salvarButton = (Button) getDialogPane().lookupButton(salvarButtonType);
        salvarButton.setDisable(true);

        // Habilitar botão apenas se título preenchido
        tituloField.textProperty().addListener((observable, oldValue, newValue) -> {
            salvarButton.setDisable(newValue.trim().isEmpty());
        });

        // Converter resultado
        setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                return criarLouvor();
            }
            return null;
        });

        // Focar no campo título
        tituloField.requestFocus();
    }

    private Louvor criarLouvor() {
        String titulo = tituloField.getText().trim();
        String autor = autorField.getText().trim();
        String letra = letraArea.getText().trim();

        if (titulo.isEmpty()) {
            return null;
        }

        Louvor louvor = new Louvor();
        louvor.setTitulo(titulo);
        louvor.setAutor(autor.isEmpty() ? "Desconhecido" : autor);
        louvor.setLetra(letra.isEmpty() ? titulo : letra);

        // Salvar no banco
        try {
            LouvorDAO louvorDAO = new LouvorDAO();
            louvorDAO.save(louvor);
            System.out.println("✓ Louvor salvo: " + louvor.getTitulo());
        } catch (Exception e) {
            System.err.println("Erro ao salvar louvor: " + e.getMessage());
            e.printStackTrace();
        }

        return louvor;
    }
}
