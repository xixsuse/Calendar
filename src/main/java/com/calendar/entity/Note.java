package com.calendar.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

/**
 *
 * @author Aleksandra Borejko
 *
 */
public class Note {

    private static final Color DEFAULT_COLOR = Color.WHITE;

    private Long id;
    private SimpleObjectProperty<LocalDate> date;
    private SimpleObjectProperty<LocalTime> time;
    private SimpleStringProperty location;
    private SimpleStringProperty title;
    private SimpleStringProperty details;
    private SimpleObjectProperty<Color> color;

    public Note() {
        this.date = new SimpleObjectProperty<>();
        this.time = new SimpleObjectProperty<>();
        this.location = new SimpleStringProperty();
        this.title = new SimpleStringProperty();
        this.details = new SimpleStringProperty();
        this.color = new SimpleObjectProperty<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public SimpleObjectProperty<LocalDate> dateProperty(){
        return date;
    }

    public LocalTime getTime() {
        return time.get();
    }

    public void setTime(LocalTime time) {
        this.time.set(time);
    }

    public SimpleObjectProperty<LocalTime> timeProperty(){
        return time;
    }

    public String getLocation() {
        return location.get();
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public SimpleStringProperty locationProperty() {
        return location;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public String getDetails() {
        return details.get();
    }

    public void setDetails(String details) {
        this.details.set(details);
    }

    public SimpleStringProperty detailsProperty() {
        return details;
    }

    public Color getColorOrDefault() {
        return Optional.ofNullable(color.get()).orElse(DEFAULT_COLOR);
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public SimpleObjectProperty<Color> colorProperty() {
        return color;
    }

    @Override
    public String toString() {
        return "Note [id=" + id + ", date=" + date + ", time=" + time + ", location=" + location + ", title=" + title
                + ", details=" + details + ", color=" + color + "]";
    }

}
