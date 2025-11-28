package com.cadealt.model;

import javafx.beans.property.*;

/**
 * Model para Versículo Bíblico
 */
public class Versiculo {
    private final IntegerProperty id;
    private final StringProperty livro;
    private final IntegerProperty capitulo;
    private final IntegerProperty versiculo;
    private final StringProperty texto;
    private final StringProperty versao;

    public Versiculo() {
        this(0, "", 0, 0, "", "ARC");
    }

    public Versiculo(int id, String livro, int capitulo, int versiculo, String texto, String versao) {
        this.id = new SimpleIntegerProperty(id);
        this.livro = new SimpleStringProperty(livro);
        this.capitulo = new SimpleIntegerProperty(capitulo);
        this.versiculo = new SimpleIntegerProperty(versiculo);
        this.texto = new SimpleStringProperty(texto);
        this.versao = new SimpleStringProperty(versao);
    }

    // ID
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    // Livro
    public String getLivro() { return livro.get(); }
    public void setLivro(String livro) { this.livro.set(livro); }
    public StringProperty livroProperty() { return livro; }

    // Capítulo
    public int getCapitulo() { return capitulo.get(); }
    public void setCapitulo(int capitulo) { this.capitulo.set(capitulo); }
    public IntegerProperty capituloProperty() { return capitulo; }

    // Versículo
    public int getVersiculo() { return versiculo.get(); }
    public void setVersiculo(int versiculo) { this.versiculo.set(versiculo); }
    public IntegerProperty versiculoProperty() { return versiculo; }

    // Texto
    public String getTexto() { return texto.get(); }
    public void setTexto(String texto) { this.texto.set(texto); }
    public StringProperty textoProperty() { return texto; }

    // Versão
    public String getVersao() { return versao.get(); }
    public void setVersao(String versao) { this.versao.set(versao); }
    public StringProperty versaoProperty() { return versao; }

    /**
     * Retorna a referência formatada: "João 3:16"
     */
    public String getReferencia() {
        return livro.get() + " " + capitulo.get() + ":" + versiculo.get();
    }

    @Override
    public String toString() {
        return getReferencia();
    }

    /**
     * Formata uma string de entrada para o padrão "Livro Capítulo:Versículo"
     * Exemplos: "joao 3 16" -> "João 3:16"
     *           "1corintios 13 4" -> "1 Coríntios 13:4"
     */
    public static String formatarReferencia(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        // Remove pontos, vírgulas e normaliza espaços
        input = input.trim()
                .replace(".", " ")
                .replace(",", " ")
                .replace(":", " ")
                .replaceAll("\\s+", " ");

        String[] partes = input.split(" ");
        if (partes.length < 3) {
            return input; // Retorna original se formato inválido
        }

        // Primeira parte é o livro
        String livro = capitalizarLivro(partes[0]);

        // Penúltima parte é capítulo, última é versículo
        String capitulo = partes[partes.length - 2];
        String versiculo = partes[partes.length - 1];

        return livro + " " + capitulo + ":" + versiculo;
    }

    /**
     * Capitaliza o nome do livro corretamente
     */
    private static String capitalizarLivro(String livro) {
        livro = livro.toLowerCase();

        // Casos especiais
        if (livro.startsWith("1") || livro.startsWith("2") || livro.startsWith("3")) {
            String numero = livro.substring(0, 1);
            String nome = livro.substring(1);
            return numero + " " + capitalize(nome);
        }

        return capitalize(livro);
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
