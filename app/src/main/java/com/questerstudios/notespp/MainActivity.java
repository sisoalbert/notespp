package com.questerstudios.notespp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTask;
    private Button buttonAdd;
    private ListView listViewTasks;
    private ArrayList<String> tasks;
    private ArrayAdapter<String> adapter;
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

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
        listViewTasks.setAdapter(adapter);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = editTextTask.getText().toString().trim();
                if (!task.isEmpty()) {
                    tasks.add(task);
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
                String removedTask = tasks.remove(position);
                adapter.notifyDataSetChanged();
                saveTasks();
                Toast.makeText(MainActivity.this, "Deleted: " + removedTask, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void saveTasks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> tasksSet = new HashSet<>(tasks);
        editor.putStringSet(TASKS_KEY, tasksSet);
        editor.apply();
    }

    private ArrayList<String> loadTasks() {
        Set<String> tasksSet = sharedPreferences.getStringSet(TASKS_KEY, new HashSet<>());
        return new ArrayList<>(tasksSet);
    }
}
