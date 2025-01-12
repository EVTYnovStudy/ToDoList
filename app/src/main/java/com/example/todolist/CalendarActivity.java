package com.example.todolist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;


public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private LocalDatabaseHelper db;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agenda);

        Toolbar toolbar = findViewById(R.id.toolbar);
        calendarView = findViewById(R.id.agenda_view);
        db = new LocalDatabaseHelper(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        Set<String> taskDates = loadTaskDates();
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            if (taskDates.contains(selectedDate)) {
                Toast.makeText(this, "Tâche prévue le " + selectedDate, Toast.LENGTH_SHORT).show();
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home){
                Toast.makeText(this, "Accueil sélectionné", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.joinagenda){
                Toast.makeText(this, "Rejoindre un agenda sélectionné", Toast.LENGTH_SHORT).show();
                showJoinAgendaDialog();
            } else if (item.getItemId() == R.id.createagenda){
                Toast.makeText(this, "Creer un agenda séléctionné", Toast.LENGTH_SHORT).show();
                showCreateAgendaDialog();
            } else if (item.getItemId() == R.id.agendaview){
                Toast.makeText(this, "Vous etes déjà en mode calendrier", Toast.LENGTH_SHORT).show();
            }
            else {
                Log.e("MainActivity", "NavigationView est nul ! Vérifiez l'ID dans le XML.");
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private Set<String> loadTaskDates() {
        Set<String> taskDates = new HashSet<>();
        for (Task task : db.getAllTasks()) {
            taskDates.add(task.getDate());
        }
        return taskDates;
    }

    void showCreateAgendaDialog() {
        EditText editTextAgendaName = new EditText(this);
        editTextAgendaName.setHint("Nom de l'agenda");
        EditText editTextPassword = new EditText(this);
        editTextPassword.setHint("Mot de passe");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 20, 30, 20);
        layout.addView(editTextAgendaName);
        layout.addView(editTextPassword);
        new AlertDialog.Builder(this)
                .setTitle("Créer un agenda")
                .setView(layout)
                .setPositiveButton("Créer", (dialog, which) -> {
                    String agendaName = editTextAgendaName.getText().toString();
                    String password = editTextPassword.getText().toString();
                    if (agendaName.isEmpty() || password.isEmpty()) {
                        Toast.makeText(CalendarActivity.this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
                    } else {
                        OnlineDataBaseHelper.createOnlineAgenda(agendaName, password);
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    void showJoinAgendaDialog() {
        EditText editTextAgendaId = new EditText(this);
        editTextAgendaId.setHint("Identifiant de l'agenda");
        EditText editTextPassword = new EditText(this);
        editTextPassword.setHint("Mot de passe");
        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 20, 30, 20);
        layout.addView(editTextAgendaId);
        layout.addView(editTextPassword);
        new AlertDialog.Builder(this)
                .setTitle("Rejoindre un agenda")
                .setView(layout)
                .setPositiveButton("Rejoindre", (dialog, which) -> {
                    String agendaId = editTextAgendaId.getText().toString();
                    String password = editTextPassword.getText().toString();
                    if (agendaId.isEmpty() || password.isEmpty()) {
                        Toast.makeText(CalendarActivity.this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
                    } else {
                        OnlineDataBaseHelper dbHelper = new OnlineDataBaseHelper();
                        dbHelper.checkAgenda("MonAgenda", "MonMotDePasse", exists -> {
                            if (exists) {
                                Log.d("AGENDA_CHECK", "L'agenda existe !");
                            } else {
                                Log.d("AGENDA_CHECK", "L'agenda n'existe pas.");
                            }
                        });
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}

