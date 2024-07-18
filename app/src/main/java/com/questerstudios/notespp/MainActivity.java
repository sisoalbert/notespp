package com.questerstudios.notespp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTask;
    private Button buttonAdd;
    private ListView listViewTasks;
    private ArrayList<Task> tasks;
    private TaskAdapter adapter;
    private SharedPreferences sharedPreferences;

    private static final String SHARED_PREFS_NAME = "MyPrefs";
    private static final String TASKS_KEY = "tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTask = findViewById(R.id.editTextTask);
        buttonAdd = findViewById(R.id.buttonAdd);
        listViewTasks = findViewById(R.id.listViewTasks);

        sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        tasks = loadTasks();

        adapter = new TaskAdapter(this, tasks);
        listViewTasks.setAdapter(adapter);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskDescription = editTextTask.getText().toString().trim();
                if (!taskDescription.isEmpty()) {
                    String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    Task task = new Task(taskDescription, currentDateTime);
                    tasks.add(0, task); // Add the new task at the beginning
                    adapter.notifyDataSetChanged();
                    editTextTask.setText("");
                    saveTasks();
                    Toast.makeText(MainActivity.this, "Task added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a task", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listViewTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Task removedTask = tasks.remove(position);
                adapter.notifyDataSetChanged();
                saveTasks();
                Toast.makeText(MainActivity.this, "Deleted: " + removedTask.getDescription(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void saveTasks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> tasksSet = new HashSet<>();
        for (Task task : tasks) {
            tasksSet.add(task.getDescription() + "::" + task.getDateTime());
        }
        editor.putStringSet(TASKS_KEY, tasksSet);
        editor.apply();
    }

    private ArrayList<Task> loadTasks() {
        Set<String> tasksSet = sharedPreferences.getStringSet(TASKS_KEY, new HashSet<>());
        ArrayList<Task> tasks = new ArrayList<>();
        for (String taskString : tasksSet) {
            String[] parts = taskString.split("::");
            if (parts.length == 2) {
                tasks.add(0, new Task(parts[0], parts[1])); // Add loaded tasks in reverse order to maintain newest first
            }
        }
        return tasks;
    }

    private static class Task {
        private String description;
        private String dateTime;

        public Task(String description, String dateTime) {
            this.description = description;
            this.dateTime = dateTime;
        }

        public String getDescription() {
            return description;
        }

        public String getDateTime() {
            return dateTime;
        }
    }

    private static class TaskAdapter extends ArrayAdapter<Task> {
        private Context context;
        private ArrayList<Task> tasks;

        public TaskAdapter(Context context, ArrayList<Task> tasks) {
            super(context, 0, tasks);
            this.context = context;
            this.tasks = tasks;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
            }

            Task task = tasks.get(position);

            TextView textViewTask = convertView.findViewById(R.id.textViewTask);
            TextView textViewDateTime = convertView.findViewById(R.id.textViewDateTime);

            textViewTask.setText(task.getDescription());
            textViewDateTime.setText(task.getDateTime());

            return convertView;
        }
    }
}
