package com.example.todolist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

public class AgendaActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OnlineTaskAdapter onlineTaskAdapter;
    private List<Task> taskList = new ArrayList<>();
    private FirebaseFirestore odb;
    private Button btnAddTask;
    private TextView tvEmptyMessage;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private List<OnlineTask> OnlineTaskList = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        odb = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My To Do List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        btnAddTask = findViewById(R.id.btn_add_task);
        tvEmptyMessage = findViewById(R.id.tv_empty_message);
        odb = OnlineDataBaseHelper.odb;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        onlineTaskAdapter = new OnlineTaskAdapter(this, taskList);
        recyclerView.setAdapter(onlineTaskAdapter);

        loadTasks();
        btnAddTask.setOnClickListener(v -> showAddTaskDialog());

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home){
                Toast.makeText(this, "Rejoindre l'accueil sélectionné", Toast.LENGTH_SHORT).show();
            } else if (item.getItemId() == R.id.joinagenda){
                Toast.makeText(this, "Rejoindre un agenda sélectionné", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AgendaActivity.this, MainActivity.class);
                showJoinAgendaDialog();
            } else if (item.getItemId() == R.id.createagenda){
                Toast.makeText(this, "Creer un agenda séléctionné", Toast.LENGTH_SHORT).show();
                showCreateAgendaDialog();
            } else if (item.getItemId() == R.id.agendaview){
                Toast.makeText(this, "Mode calendrier séléctionné", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AgendaActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void loadTasks() {
        taskList.clear();  // Réinitialise la liste des tâches

        // Appel pour récupérer les tâches depuis la base de données
        OnlineDataBaseHelper.getOnlineTask(Agenda.getId(), new OnlineDataBaseHelper.OnlineTaskListener() {

            @Override
            public void onTasksLoaded(List<Task> tasks) {
                if (tasks != null && !tasks.isEmpty()) {
                    taskList.addAll(tasks);  // Ajoute les tâches récupérées à la liste
                    tvEmptyMessage.setVisibility(View.GONE);  // Cache le message "vide"
                } else {
                    tvEmptyMessage.setVisibility(View.VISIBLE);  // Affiche le message si aucune tâche
                }
                onlineTaskAdapter.notifyDataSetChanged();  // Met à jour l'adaptateur
            }

            @Override
            public void onTaskAdded(String taskId, OnlineTask task) {
                // Cette méthode peut être implémentée si vous devez gérer l'ajout d'une tâche
            }

            @Override
            public void onTaskAddFailed(Exception e) {
                // Gestion des erreurs d'ajout
            }
        });
    }







    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddTaskDialog() {
        EditText editTextTitle = new EditText(this);
        editTextTitle.setHint("Titre de la tâche");
        EditText editTextDescription = new EditText(this);
        editTextDescription.setHint("Description de la tâche");

        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        EditText editTextDate = new EditText(this);
        editTextDate.setHint("Date (JJ/MM/AAAA)");
        editTextDate.setFocusable(false);
        editTextDate.setClickable(true);
        editTextDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AgendaActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        editTextDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);},
                    year, month, day
            );
            datePickerDialog.show();
        });

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 20, 30, 20);
        layout.addView(editTextTitle);
        layout.addView(editTextDescription);
        layout.addView(editTextDate);

        new AlertDialog.Builder(this)
                .setTitle("Ajouter une tâche")
                .setView(layout)
                .setPositiveButton("Ajouter", (dialog, which) -> {
                    String title = editTextTitle.getText().toString();
                    String description = editTextDescription.getText().toString();
                    String date = editTextDate.getText().toString();
                    if (title.isEmpty() || description.isEmpty() || date.isEmpty()) {
                        Toast.makeText(AgendaActivity.this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
                    } else {
                        // Crée une nouvelle tâche OnlineTask
                        String agendaId = Agenda.getId(); // Utilise l'ID de l'agenda actuel
                        OnlineTask newTask = new OnlineTask(
                                "", // ID (le Firestore générera un ID unique, donc laisse-le vide ici)
                                title,
                                description,
                                false, // Statut de complétion
                                agendaId
                        );

                        // Appel de la méthode asynchrone pour ajouter la tâche en ligne
                        new OnlineDataBaseHelper.OnlineTaskListener() {
                            @Override
                            public void onTasksLoaded(List<Task> tasks) {
                                // Cette méthode est nécessaire, même si vous n'en avez pas besoin
                                // Vous pouvez la laisser vide si vous ne l'utilisez pas
                            }

                            @Override
                            public void onTaskAdded(String taskId, OnlineTask task) {
                                // Ajoute l'ID Firestore à la tâche
                                OnlineTask.setOnlineId(taskId);  // Le Firestore génère l'ID du document

                                // Ajoute la tâche à la liste locale
                                OnlineTaskList.add(task);

                                // Notifie l'adaptateur de l'ajout de l'élément
                                onlineTaskAdapter.notifyItemInserted(taskList.size() - 1);

                                // Recharge les tâches ou met à jour l'interface
                                loadTasks();
                            }

                            @Override
                            public void onTaskAddFailed(Exception e) {
                                // Gestion des erreurs d'ajout
                                Toast.makeText(AgendaActivity.this, "Échec de l'ajout de la tâche", Toast.LENGTH_SHORT).show();
                            }
                        };
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
                        Toast.makeText(AgendaActivity.this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
                    } else {
                        // Firebase check to see if the agenda exists
                        checkAgendaInFirebase(agendaId, password);
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void checkAgendaInFirebase(String agendaId, String password) {
        DocumentReference agendaRef = odb.collection("Agenda").document(agendaId);
        agendaRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String storedPassword = documentSnapshot.getString("mdp");
                        if (storedPassword.equals(password)) {
                            Toast.makeText(AgendaActivity.this, "Agenda rejoint avec succès", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AgendaActivity.this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AgendaActivity.this, "Agenda non trouvé", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("FIREBASE_ERROR", "Erreur de connexion à Firestore", e));
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
                        Toast.makeText(AgendaActivity.this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
                    } else {
                        createAgendaInFirebase(agendaName, password);
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void createAgendaInFirebase(String agendaName, String password) {
        Agenda newAgenda = new Agenda(agendaName, agendaName, password);
        odb.collection("Agenda").document(agendaName)
                .set(newAgenda)
                .addOnSuccessListener(aVoid -> Log.d("FIREBASE", "Agenda créé avec succès"))
                .addOnFailureListener(e -> Log.e("FIREBASE_ERROR", "Erreur lors de la création de l'agenda", e));
    }
}
