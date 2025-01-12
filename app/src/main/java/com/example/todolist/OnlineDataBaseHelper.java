package com.example.todolist;

import android.content.ContentValues;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnlineDataBaseHelper {

    static FirebaseFirestore odb;


    public OnlineDataBaseHelper() {
        odb = FirebaseFirestore.getInstance();
    }

    public static void createOnlineAgenda(String agendaName, String agendaPassword) {
        Map<String, Object> agenda = new HashMap<>();
        agenda.put("nom_agenda", agendaName);
        agenda.put("mdp_agenda", agendaPassword);

        odb.collection("agenda")
                .add(agenda)
                .addOnSuccessListener(documentReference ->
                        Log.d("FIREBASE", "Agenda ajouté avec succès ! ID: " + documentReference.getId())
                )
                .addOnFailureListener(e ->
                        Log.e("FIREBASE_ERROR", "Erreur lors de l'ajout de l'agenda", e)
                );
    }

    public static void checkAgenda(String agendaName, String agendaPassword, CheckAgendaCallback callback) {
        odb.collection("agenda")
                .whereEqualTo("nom_agenda", agendaName)
                .whereEqualTo("mdp_agenda", agendaPassword)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        callback.onResult(true);
                    } else {
                        callback.onResult(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE_ERROR", "Erreur lors de la vérification de l'agenda", e);
                    callback.onResult(false);
                });
    }

    public interface CheckAgendaCallback {
        void onResult(boolean exists);
    }

    public static void getOnlineTask(String agendaId, final OnlineTaskListener listener) {
        FirebaseFirestore odb = FirebaseFirestore.getInstance();
        odb.collection("tasks")
                .whereEqualTo("agendaId", agendaId) // Filtrer par agendaId
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Task> tasks = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            OnlineTask taskObj = document.toObject(OnlineTask.class); // Assurez-vous que OnlineTask a un constructeur sans argument
                            taskObj.setOnlineId(document.getId()); // Assignez l'ID du document Firestore
                            tasks.add(taskObj);
                        }
                        listener.onTasksLoaded(tasks); // Passe la liste des tâches à l'appelant
                    } else {
                        Log.e("FIREBASE_ERROR", "Erreur lors de la récupération des tâches", task.getException());
                        listener.onTasksLoaded(null);  // Passe null si erreur
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE_ERROR", "Erreur lors de la récupération des tâches", e);
                    listener.onTasksLoaded(null);  // Passe null en cas de failure
                });
    }


    // Interface pour le callback
    public interface TaskListener {
        void onTasksLoaded(List<Task> tasks);

        void onTaskAdded(String taskId, Task task);

        void onTaskAddFailed(Exception e);
    }

    public static void addOnlineTask(OnlineTask task, OnlineTaskListener onlineTaskListener) {
        FirebaseFirestore odb = FirebaseFirestore.getInstance();
        odb.collection("tasks")
                .add(task) // Ajout de la tâche à Firestore
                .addOnSuccessListener(documentReference -> {
                    String taskId = documentReference.getId();  // Obtenir l'ID généré par Firestore
                    onlineTaskListener.onTaskAdded(taskId, task); // Appeler onTaskAdded avec l'ID et la tâche
                })
                .addOnFailureListener(e -> {
                    onlineTaskListener.onTaskAddFailed(e);  // Gestion des erreurs
                });
    }


    public interface OnlineTaskListener {
        void onTasksLoaded(List<Task> tasks);
        void onTaskAdded(String taskId, OnlineTask task);  // Utilise OnlineTask ici
        void onTaskAddFailed(Exception e);  // Méthode pour gérer les erreurs

    }


}




