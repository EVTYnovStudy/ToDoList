package com.example.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Button;

public class OnlineTaskAdapter extends RecyclerView.Adapter<OnlineTaskAdapter.TaskViewHolder> {

    private final Context context;
    private final List<Task> taskList;
    private final FirebaseFirestore odb;

    // Constructor
    public OnlineTaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
        this.odb = FirebaseFirestore.getInstance();
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
            updateTaskCompletionStatus(task);
        });

        holder.btnDelete.setOnClickListener(v -> {
            deleteTask(OnlineTask.getOnlineId());
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

    private void updateTaskCompletionStatus(Task task) {
        odb.collection("tasks")
                .document(OnlineTask.getOnlineId())
                .update("completed", task.isCompleted())
                .addOnSuccessListener(aVoid -> {
                    // Handle success (optional)
                })
                .addOnFailureListener(e -> {
                    // Handle failure (optional)
                });
    }

    private void deleteTask(String taskId) {
        odb.collection("tasks")
                .document(taskId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Handle success (optional)
                })
                .addOnFailureListener(e -> {
                    // Handle failure (optional)
                });
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
