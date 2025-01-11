package com.example.todolist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.modele.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();
    private FirebaseDatabaseHelper database; // Référence Firebase
    private Button btnAddTask;
    private TextView tvEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        btnAddTask = findViewById(R.id.btn_add_task);
        tvEmptyMessage = findViewById(R.id.tv_empty_message);

        // Initialisation de Firebase
        database = new FirebaseDatabaseHelper();  // Création de l'instance FirebaseDatabaseHelper

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this, taskList, this);
        recyclerView.setAdapter(taskAdapter);

        loadTasks();

        btnAddTask.setOnClickListener(v -> showAddTaskDialog());
    }

    private void loadTasks() {
        taskList.clear(); // Vider la liste avant de la remplir

        database.getAllTasks(new FirebaseDatabaseHelper.DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                List<Task> tasks = (List<Task>) result;
                taskList.addAll(tasks);

                if (taskList.isEmpty()) {
                    tvEmptyMessage.setVisibility(View.VISIBLE);
                } else {
                    tvEmptyMessage.setVisibility(View.GONE);
                }

                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Log.e("Firebase", "Erreur lors de la récupération des tâches : " + error);
            }
        });
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
                    MainActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> editTextDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1),
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
                        Toast.makeText(MainActivity.this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
                    } else {
                        Task newTask = new Task(null, title, description, false, date);
                        addTask(newTask);
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    public void addTask(Task task) {
        database.addTask(task, new FirebaseDatabaseHelper.DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                taskAdapter.addTask(task); // Ajout local de la tâche à l'adaptateur
                Toast.makeText(MainActivity.this, "Tâche ajoutée avec succès", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(MainActivity.this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateTaskInFirebase(Task task) {
        if (task.getId() != null) {
            database.updateTaskCompletionStatus(task, new FirebaseDatabaseHelper.DatabaseCallback() {
                @Override
                public void onSuccess(Object result) {
                    Toast.makeText(MainActivity.this, "Tâche mise à jour avec succès", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(MainActivity.this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "ID de tâche manquant pour la mise à jour", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteTaskFromFirebase(Task task) {
        if (task.getId() != null) {
            database.deleteTask(task, new FirebaseDatabaseHelper.DatabaseCallback() {
                @Override
                public void onSuccess(Object result) {
                    taskList.remove(task); // Suppression locale de la tâche
                    taskAdapter.notifyDataSetChanged(); // Mise à jour de l'UI
                    Toast.makeText(MainActivity.this, "Tâche supprimée avec succès", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(MainActivity.this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "ID de tâche manquant pour la suppression", Toast.LENGTH_SHORT).show();
        }
    }

    public void showUpdateTaskDialog(Task task, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mettre à jour la tâche");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_task, null);
        EditText etTitle = view.findViewById(R.id.et_task_title);
        EditText etDescription = view.findViewById(R.id.et_task_description);
        EditText etDate = view.findViewById(R.id.et_task_date);

        etTitle.setText(task.getName());
        etDescription.setText(task.getDescription());
        etDate.setText(task.getDate());

        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, (view1, year1, month1, dayOfMonth1) -> {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year1, month1, dayOfMonth1);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = sdf.format(selectedDate.getTime());

                etDate.setText(formattedDate);
            }, year, month, dayOfMonth);
            datePickerDialog.show();
        });

        builder.setView(view);

        builder.setPositiveButton("Enregistrer", (dialog, which) -> {
            String newTitle = etTitle.getText().toString();
            String newDescription = etDescription.getText().toString();
            String newDate = etDate.getText().toString();

            task.setName(newTitle);
            task.setDescription(newDescription);
            task.setDate(newDate);

            updateTaskInFirebase(task);
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}
