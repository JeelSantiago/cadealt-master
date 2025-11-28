package com.cadealt.model;

import javafx.beans.property.*;

/**
 * Model para Louvor
 */
public class Louvor {
    private final IntegerProperty id;
    private final StringProperty titulo;
    private final StringProperty autor;
    private final StringProperty letra;

    public Louvor() {
        this(0, "", "", "");
    }

    public Louvor(int id, String titulo, String autor, String letra) {
        this.id = new SimpleIntegerProperty(id);
        this.titulo = new SimpleStringProperty(titulo);
        this.autor = new SimpleStringProperty(autor);
        this.letra = new SimpleStringProperty(letra);
    }

    // ID
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    // Título
    public String getTitulo() { return titulo.get(); }
    public void setTitulo(String titulo) { this.titulo.set(titulo); }
    public StringProperty tituloProperty() { return titulo; }

    // Autor
    public String getAutor() { return autor.get(); }
    public void setAutor(String autor) { this.autor.set(autor); }
    public StringProperty autorProperty() { return autor; }

    // Letra
    public String getLetra() { return letra.get(); }
    public void setLetra(String letra) { this.letra.set(letra); }
    public StringProperty letraProperty() { return letra; }

    @Override
    public String toString() {
        if (autor.get() != null && !autor.get().isEmpty()) {
            return titulo.get() + " - " + autor.get();
        }
        return titulo.get();
    }

    /**
     * Retorna apenas o título (para louvores NÃO mostramos referência embaixo)
     */
    public String getReferencia() {
        return null; // Louvores não mostram referência
    }
}
