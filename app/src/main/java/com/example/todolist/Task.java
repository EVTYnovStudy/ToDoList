package com.example.todolist;

public class Task {
    private int id;
    private String name;
    private String description;
    private boolean isCompleted;
    private boolean isShared;
    private String date;

    public Task() {
    }

    public Task(int id, String name, String description, boolean isCompleted, boolean isShared, String date) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isCompleted = isCompleted;
        this.isShared = isShared;
        this.date = date;
    }

    //Date
    public String getDate() {return date;}
    public void setDate(String date) {this.date = date;}

    //ID
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    //Name
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    //Description
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    //Complete (checkbox)
    public boolean isCompleted() {return isCompleted;}
    public void setCompleted(boolean completed) {this.isCompleted = completed;}
}

