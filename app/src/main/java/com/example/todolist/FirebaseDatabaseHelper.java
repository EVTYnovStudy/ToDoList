package com.example.todolist;

import android.util.Log;

import com.example.todolist.modele.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {

    private static final String TAG = "FirebaseDatabaseHelper";
    private DatabaseReference databaseReference;

    // Constructeur pour initialiser Firebase
    public FirebaseDatabaseHelper() {
        // Initialisation de la référence à Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("tasks");
    }

    // Ajouter une tâche dans Firebase
    public void addTask(Task task, DatabaseCallback callback) {
        String taskId = databaseReference.push().getKey(); // Générer un ID unique pour la tâche
        if (taskId != null) {
            task.setId(taskId); // Assigner l'ID généré à la tâche
            databaseReference.child(taskId).setValue(task)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Tâche ajoutée avec succès.");
                        callback.onSuccess("Tâche ajoutée avec succès.");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Erreur lors de l'ajout de la tâche : " + e.getMessage());
                        callback.onFailure("Erreur lors de l'ajout de la tâche.");
                    });
        } else {
            callback.onFailure("Échec de la génération de l'ID de la tâche.");
        }
    }

    // Récupérer toutes les tâches depuis Firebase
    public void getAllTasks(DatabaseCallback callback) {
        List<Task> taskList = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                taskList.clear(); // Nettoyer la liste avant de la remplir à nouveau
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null) {
                        task.setId(snapshot.getKey()); // Associer l'ID de la tâche
                        taskList.add(task);
                    }
                }
                callback.onSuccess(taskList); // Retourner la liste des tâches au callback
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Erreur lors de la récupération des tâches : " + databaseError.getMessage());
                callback.onFailure("Erreur lors de la récupération des tâches.");
            }
        });
    }

    // Mettre à jour le statut de complétion de la tâche dans Firebase
    public void updateTaskCompletionStatus(Task task, DatabaseCallback callback) {
        if (task.getId() != null) {
            databaseReference.child(task.getId()).child("completed")
                    .setValue(task.isCompleted())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Statut de complétion mis à jour.");
                        callback.onSuccess("Statut de complétion mis à jour.");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Erreur lors de la mise à jour du statut : " + e.getMessage());
                        callback.onFailure("Erreur lors de la mise à jour du statut.");
                    });
        } else {
            callback.onFailure("ID de tâche manquant.");
        }
    }

    // Supprimer une tâche de Firebase
    public void deleteTask(Task task, DatabaseCallback callback) {
        if (task.getId() != null) {
            databaseReference.child(task.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Tâche supprimée avec succès.");
                        callback.onSuccess("Tâche supprimée avec succès.");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Erreur lors de la suppression de la tâche : " + e.getMessage());
                        callback.onFailure("Erreur lors de la suppression de la tâche.");
                    });
        } else {
            callback.onFailure("ID de tâche manquant.");
        }
    }

    // Interface de retour pour les opérations Firebase
    public interface DatabaseCallback {
        void onSuccess(Object result);
        void onFailure(String error);
    }
}
