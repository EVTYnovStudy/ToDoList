package com.example.todolist;

import static android.app.ProgressDialog.show;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();
    private LocalDatabaseHelper db;
    private Button btnAddTask;
    private TextView tvEmptyMessage;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My To Do List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recycler_view);
        btnAddTask = findViewById(R.id.btn_add_task);
        tvEmptyMessage = findViewById(R.id.tv_empty_message);
        db = new LocalDatabaseHelper(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this, taskList, db, this);
        recyclerView.setAdapter(taskAdapter);
        loadTasks();
        btnAddTask.setOnClickListener(v -> showAddTaskDialog());
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home){
                Toast.makeText(this, "Vous etes déjà dans l'accueil", Toast.LENGTH_SHORT).show();
            } else if (item.getItemId() == R.id.joinagenda){
                Toast.makeText(this, "Rejoindre un agenda sélectionné", Toast.LENGTH_SHORT).show();
                showJoinAgendaDialog();
            } else if (item.getItemId() == R.id.createagenda){
                Toast.makeText(this, "Creer un agenda séléctionné", Toast.LENGTH_SHORT).show();
                showCreateAgendaDialog();
            } else if (item.getItemId() == R.id.agendaview){
                Toast.makeText(this, "Mode calendrier séléctionné", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
            else {
                Log.e("MainActivity", "NavigationView est nul ! Vérifiez l'ID dans le XML.");
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void loadTasks() {
        taskList.clear();
        List<Task> tasksFromDb = db.getAllTasks();
        if (tasksFromDb != null && !tasksFromDb.isEmpty()) {taskList.addAll(tasksFromDb);}
        else {Log.e("LOAD_TASKS", "Aucune tâche trouvée dans la base de données");}
        if (taskList.isEmpty()) {tvEmptyMessage.setVisibility(View.VISIBLE);}
        else {tvEmptyMessage.setVisibility(View.GONE);}
        taskAdapter.notifyDataSetChanged();
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
                    MainActivity.this,
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
                    if (title.isEmpty() || description.isEmpty() || date.isEmpty()) {Toast.makeText(MainActivity.this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();}
                    else {
                        Task newTask = new Task(0, title, description, false, false, date);
                        long id = db.addTask(newTask);
                        newTask.setId((int) id);
                        taskList.add(newTask);
                        taskAdapter.notifyItemInserted(taskList.size() - 1);
                        loadTasks();
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
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
            db.updateTask(task);
            taskList.set(position, task);
            taskAdapter.notifyItemChanged(position);
        });
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());
        builder.show();
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
                        Toast.makeText(MainActivity.this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
                    } else {
                            Boolean exists = false;
                            if (exists == false) {
                                Log.d("AGENDA_CHECK", "L'agenda existe !");
                            } else {
                                Log.d("AGENDA_CHECK", "L'agenda n'existe pas.");
                            }
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
    public void onBackPressed() {
        if (doitAfficherConfirmation()) {
            showExitConfirmationDialog();
        } else {
            super.onBackPressed();
        }
    }

    private boolean doitAfficherConfirmation() {
        return true;
    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Quitter l'application")
                .setMessage("Êtes-vous sûr de vouloir quitter l'application ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    finishAffinity();
                })
                .setNegativeButton("Non", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(true)
                .show();
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
                        Toast.makeText(MainActivity.this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
                    } else {
                        //Check
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}
