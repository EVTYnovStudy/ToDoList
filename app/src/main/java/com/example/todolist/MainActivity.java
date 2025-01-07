package com.example.todolist;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();
    private DatabaseHelper db;
    private Button btnAddTask;
    private TextView tvEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des vues
        recyclerView = findViewById(R.id.recycler_view);
        btnAddTask = findViewById(R.id.btn_add_task);
        tvEmptyMessage = findViewById(R.id.tv_empty_message);

        db = new DatabaseHelper(this);

        // Initialiser le RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this, taskList, db);
        recyclerView.setAdapter(taskAdapter);

        // Charger les tâches depuis la base de données
        loadTasks();

        // Ajouter une nouvelle tâche lorsque le bouton est cliqué
        btnAddTask.setOnClickListener(v -> showAddTaskDialog());
    }

    // Charger les tâches depuis la base de données
    private void loadTasks() {
        taskList.clear();  // Vider la liste actuelle des tâches

        // Utiliser la méthode qui retourne directement une liste de tâches
        List<Task> tasksFromDb = db.getAllTasks();  // Appel à ta méthode getAllTasks()

        if (tasksFromDb != null && !tasksFromDb.isEmpty()) {
            taskList.addAll(tasksFromDb);  // Ajouter toutes les tâches à taskList
        } else {
            Log.e("LOAD_TASKS", "Aucune tâche trouvée dans la base de données");
        }

        // Vérifier si la liste est vide et afficher un message si nécessaire
        if (taskList.isEmpty()) {
            tvEmptyMessage.setVisibility(View.VISIBLE);  // Affiche le message "Aucune tâche"
        } else {
            tvEmptyMessage.setVisibility(View.GONE);  // Cache le message si des tâches existent
        }

        taskAdapter.notifyDataSetChanged();  // Notifier l'adaptateur que les données ont changé
    }


    // Afficher un dialog pour ajouter une nouvelle tâche
    private void showAddTaskDialog() {
        // Créer un EditText pour le titre
        EditText editTextTitle = new EditText(this);
        editTextTitle.setHint("Titre de la tâche");

        // Créer un EditText pour la description
        EditText editTextDescription = new EditText(this);
        editTextDescription.setHint("Description de la tâche");

        // Créer un calendrier pour la date
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Créer un EditText pour la date
        EditText editTextDate = new EditText(this);
        editTextDate.setHint("Date (JJ/MM/AAAA)");
        editTextDate.setFocusable(false);
        editTextDate.setClickable(true);

        editTextDate.setOnClickListener(v -> {
            // Ouvrir un DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Afficher la date sélectionnée dans le format "JJ/MM/AAAA"
                        editTextDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Créer un LinearLayout pour contenir les champs
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 20, 30, 20);

        // Ajouter les champs au LinearLayout
        layout.addView(editTextTitle);
        layout.addView(editTextDescription);
        layout.addView(editTextDate);

        // Créer le bouton pour ajouter la tâche
        new AlertDialog.Builder(this)
                .setTitle("Ajouter une tâche")
                .setView(layout)
                .setPositiveButton("Ajouter", (dialog, which) -> {
                    String title = editTextTitle.getText().toString();
                    String description = editTextDescription.getText().toString();
                    String date = editTextDate.getText().toString();

                    // Validation des champs
                    if (title.isEmpty() || description.isEmpty() || date.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
                    } else {
                        // Ajouter la tâche à la base de données
                        Task newTask = new Task(0, title, description, false, false, date);
                        long id = db.addTask(newTask);  // Ajout dans la base de données
                        newTask.setId((int) id);  // Assigner l'ID généré à la tâche

                        // Ajouter la tâche à la liste et mettre à jour le RecyclerView
                        taskList.add(newTask);

                        // Informer l'adaptateur que la liste a changé
                        taskAdapter.notifyItemInserted(taskList.size() - 1);  // Met à jour l'affichage du RecyclerView

                        // Optionnel: Recharger les tâches depuis la base de données
                        loadTasks();  // Si tu veux toujours recharger les tâches après ajout
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}
