package com.example.todolist;

import com.google.firebase.firestore.FirebaseFirestore;

public class OnlineTask extends Task {
    private static String id;
    private static String name;
    private static String description;
    private static boolean completed;
    private static String date;
    private static String idAgenda;
    private static FirebaseFirestore odb;
    private static String onlineId;


    public OnlineTask() {}

    public OnlineTask(String id, String name, String description, boolean completed, String idAgenda) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.completed = completed;
        this.idAgenda = idAgenda;
    }

    public static String getOnlineId() { return id; }
    public static void setOnlineId(String id) { OnlineTask.id = id; }

    public static String getOnlineName() { return name; }
    public void setOnlineName(String name) { OnlineTask.name = name; }

    public static String getOnlineDescription() { return description; }
    public void setOnlineDescription(String description) { OnlineTask.description = description; }

    public static boolean isOnlineCompleted() { return completed; }
    public void setOnlineCompleted(boolean completed) { OnlineTask.completed = completed; }

    public static String getOnlineIdAgenda() { return idAgenda; }
    public void setOnlineIdAgenda(String idAgenda) { OnlineTask.idAgenda = idAgenda; }


}