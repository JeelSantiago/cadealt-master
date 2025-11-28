package com.cadealt.model;

import javafx.beans.property.*;

import java.time.LocalDateTime;

/**
 * Modelo de dados para músicas
 */
public class Song {

    private final IntegerProperty id;
    private final StringProperty title;
    private final StringProperty artist;
    private final StringProperty lyrics;
    private final StringProperty category;
    private final ObjectProperty<LocalDateTime> createdAt;
    private final ObjectProperty<LocalDateTime> updatedAt;

    /**
     * Construtor padrão
     */
    public Song() {
        this(0, "", "", "", "");
    }

    /**
     * Construtor com parâmetros
     */
    public Song(int id, String title, String artist, String lyrics, String category) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.lyrics = new SimpleStringProperty(lyrics);
        this.category = new SimpleStringProperty(category);
        this.createdAt = new SimpleObjectProperty<>(LocalDateTime.now());
        this.updatedAt = new SimpleObjectProperty<>(LocalDateTime.now());
    }

    // Getters e Setters para ID
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    // Getters e Setters para Title
    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    // Getters e Setters para Artist
    public String getArtist() {
        return artist.get();
    }

    public void setArtist(String artist) {
        this.artist.set(artist);
    }

    public StringProperty artistProperty() {
        return artist;
    }

    // Getters e Setters para Lyrics
    public String getLyrics() {
        return lyrics.get();
    }

    public void setLyrics(String lyrics) {
        this.lyrics.set(lyrics);
    }

    public StringProperty lyricsProperty() {
        return lyrics;
    }

    // Getters e Setters para Category
    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public StringProperty categoryProperty() {
        return category;
    }

    // Getters e Setters para CreatedAt
    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    // Getters e Setters para UpdatedAt
    public LocalDateTime getUpdatedAt() {
        return updatedAt.get();
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt.set(updatedAt);
    }

    public ObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return title.get() + (artist.get().isEmpty() ? "" : " - " + artist.get());
    }
}
