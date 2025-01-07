package com.example.todolist;

public class Task {
    private int id;
    private String name;
    private String description;
    private boolean isCompleted;
    private boolean isShared;
    private String date;

    public Task(int id, String name, String description, boolean isCompleted, boolean isShared, String date) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isCompleted = isCompleted;
        this.isShared = isShared;
        this.date = date;
    }

    // Getters et setters pour la date
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isShared() {
        return isShared;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setShared(boolean isShared) {
        this.isShared = isShared;
    }
}

