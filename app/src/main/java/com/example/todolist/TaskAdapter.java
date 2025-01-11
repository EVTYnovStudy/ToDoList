package com.example.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.modele.Task;

import java.util.List;
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final Context context;
    private final List<Task> taskList; // Utilisation correcte de Task depuis modele
    private final MainActivity mainActivity; // Référence au MainActivity pour interactions

    // Constructeur
    public TaskAdapter(Context context, List<Task> taskList, MainActivity mainActivity) {
        this.context = context;
        this.taskList = taskList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.tvTaskTitle.setText(task.getName());
        holder.tvTaskDescription.setText(task.getDescription());
        holder.tvTaskDate.setText(task.getDate());
        holder.cbCompleted.setChecked(task.isCompleted());

        // Mise à jour de l'état de la tâche
        holder.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            mainActivity.updateTaskInFirebase(task); // Met à jour Firebase
        });

        // Suppression de la tâche
        holder.btnDelete.setOnClickListener(v -> {
            mainActivity.deleteTaskFromFirebase(task); // Supprime de Firebase
            taskList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, taskList.size());
        });

        // Modification de la tâche via le MainActivity
        holder.itemView.setOnClickListener(v -> mainActivity.showUpdateTaskDialog(task, position));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void addTask(Task newTask) {
        taskList.add(newTask); // Ajoute la nouvelle tâche à la liste locale
        notifyItemInserted(taskList.size() - 1); // Notifie RecyclerView qu'un élément a été ajouté
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskTitle;
        TextView tvTaskDescription;
        TextView tvTaskDate;
        CheckBox cbCompleted;
        Button btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tv_task_title);
            tvTaskDescription = itemView.findViewById(R.id.tv_task_description);
            tvTaskDate = itemView.findViewById(R.id.tv_task_date);
            cbCompleted = itemView.findViewById(R.id.cb_completed);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
