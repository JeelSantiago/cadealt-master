package com.cadealt.model;

import javafx.beans.property.*;

/**
 * Model para Hino da Harpa
 */
public class Hino {
    private final IntegerProperty id;
    private final StringProperty numero;
    private final StringProperty titulo;
    private final StringProperty letra;
    private final StringProperty tipo;

    public Hino() {
        this(0, "", "", "", "harpa");
    }

    public Hino(int id, String numero, String titulo, String letra, String tipo) {
        this.id = new SimpleIntegerProperty(id);
        this.numero = new SimpleStringProperty(numero);
        this.titulo = new SimpleStringProperty(titulo);
        this.letra = new SimpleStringProperty(letra);
        this.tipo = new SimpleStringProperty(tipo);
    }

    // ID
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    // Número
    public String getNumero() { return numero.get(); }
    public void setNumero(String numero) { this.numero.set(numero); }
    public StringProperty numeroProperty() { return numero; }

    // Título
    public String getTitulo() { return titulo.get(); }
    public void setTitulo(String titulo) { this.titulo.set(titulo); }
    public StringProperty tituloProperty() { return titulo; }

    // Letra
    public String getLetra() { return letra.get(); }
    public void setLetra(String letra) { this.letra.set(letra); }
    public StringProperty letraProperty() { return letra; }

    // Tipo
    public String getTipo() { return tipo.get(); }
    public void setTipo(String tipo) { this.tipo.set(tipo); }
    public StringProperty tipoProperty() { return tipo; }

    @Override
    public String toString() {
        return numero.get() + " - " + titulo.get();
    }

    /**
     * Retorna a referência completa do hino para exibição
     */
    public String getReferencia() {
        return numero.get() + " - " + titulo.get();
    }
}
