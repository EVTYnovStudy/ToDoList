package com.example.todolist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.todolist.Task;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final Context context;
    private final List<com.example.todolist.Task> taskList;
    private final DatabaseHelper db;

    // Constructeur
    public TaskAdapter(Context context, List<com.example.todolist.Task> taskList, DatabaseHelper db, MainActivity mainActivity) {
        this.context = context;
        this.taskList = taskList;
        this.db = db;
    }

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
        holder.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            db.updateTaskCompletionStatus(task);
        });

        holder.btnDelete.setOnClickListener(v -> {
            db.deleteTask(task.getId());
            taskList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, taskList.size());
        });

        holder.itemView.setOnClickListener(v -> {
            ((MainActivity) context).showUpdateTaskDialog(task, position);
        });

    }

    @Override
    public int getItemCount() {
        return taskList.size();
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
