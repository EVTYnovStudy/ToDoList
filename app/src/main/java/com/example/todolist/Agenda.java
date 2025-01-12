package com.example.todolist;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

public class Agenda {
    private static String id;
    private static String nomAgenda;
    private static String mdp;

    public Agenda() {}

    public Agenda(String id, String nomAgenda, String mdp) {
        this.id = id;
        this.nomAgenda = nomAgenda;
        this.mdp = mdp;
    }

    public static String getId(){
        return Agenda.id;
    }
    public static String getNomAgenda(){
        return Agenda.nomAgenda;
    }
    public static String getMdp(){
        return Agenda.mdp;
    }
}