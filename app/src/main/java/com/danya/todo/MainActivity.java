package com.danya.todo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    DataBaseHelper myDB;
    ArrayList<String> noteId, noteTitle, noteText, noteEditTime;
    CustomAdapter customAdapter;
    RecyclerView recyclerViewContainer;

    //Tasks
    LinearLayout tasksContainer;
    ImageView addTaskBtn;
    private List<View> tasksList;
    ArrayList<String> taskStatus, taskText;

    //Notebook
    EditText notebookEdtTxt;
    TextView notebooksTitle;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tasksList = new ArrayList<>();

        //Set current date into MainSubtitle
        TextView mainSubtitle = findViewById(R.id.tvMainSubtitle);
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EE, MMM d", Locale.ENGLISH);
        String editTime = formatter.format(date);
        mainSubtitle.setText(editTime);

        recyclerViewContainer = findViewById(R.id.recyclerViewContainer);
        tasksContainer = findViewById(R.id.tasksContainer);
        ImageView openNoteEditorBtn = findViewById(R.id.openNoteEditorBtn);
        addTaskBtn = findViewById(R.id.addTaskBtn);
        notebookEdtTxt = findViewById(R.id.etNotebookText);
        notebooksTitle = findViewById(R.id.tvNotebooksTitle);

        notebooksTitle.setOnClickListener(this);
        openNoteEditorBtn.setOnClickListener(this);
        addTaskBtn.setOnClickListener(this);

        notebookEdtTxt.setOnFocusChangeListener((view, b) -> {
            if (notebookEdtTxt.getText().toString() != "") {
                myDB.saveNotebooksTextToDb(notebookEdtTxt.getText().toString());
            }
        });

        //myDB vars
        myDB = new DataBaseHelper(MainActivity.this);
        noteId = new ArrayList<>();
        noteTitle = new ArrayList<>();
        noteText = new ArrayList<>();
        noteEditTime = new ArrayList<>();

        //task vars
        taskText = new ArrayList<>();
        taskStatus = new ArrayList<>();

        storeTasksDataInArrays();
        storeDataInArrays();

        addTasksFromDb();

        //Notes
        customAdapter = new CustomAdapter(MainActivity.this, this, noteId, noteTitle, noteText, noteEditTime);
        recyclerViewContainer.setAdapter(customAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewContainer.setLayoutManager(layoutManager);

        //get and set Notebookstext from DB
        Cursor cursor = myDB.readNotebooksData();
        while(cursor.moveToNext()) {
            if(cursor.getCount() != 0) {notebookEdtTxt.setText(cursor.getString(1));}
        }
    }

    void addTasksFromDb() {
        for (int i = 0; i < taskText.size(); i++) {
            addTaskToLayout(taskText.get(i), taskStatus.get(i), Integer.toString(i));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            recreate();
        }
    }

    void storeDataInArrays() {
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() != 0) {
            while(cursor.moveToNext()) {
                noteId.add(cursor.getString(0));
                noteTitle.add(cursor.getString(1));
                noteText.add(cursor.getString(2));
                noteEditTime.add(cursor.getString(3));
            }
        }
    }
    void storeTasksDataInArrays() {
        Cursor cursor = myDB.readTasksData();
        if(cursor.getCount() != 0) {
            while(cursor.moveToNext()) {
                taskStatus.add(cursor.getString(0));
                taskText.add(cursor.getString(1));
            }
        }
    }

    void addTaskToLayout(String text, String tag, String id) {
        //anflate() with 2 args has bug with copying tag`s parameters, so used it with 3 of them
        final View taskToAdd = getLayoutInflater().inflate(R.layout.custom_task_layout, tasksContainer, false);

        EditText taskTextEdtTxt = taskToAdd.findViewById(R.id.taskTextEdtTxt);
        taskTextEdtTxt.setText(text);

        ImageView statusImage = taskToAdd.findViewById(R.id.taskStatusBtn);
        if (Integer.parseInt(tag) == 1) {
            //by default taskLayout got setTag="0" & background="@drawable/ic_yellow_circle"
            statusImage.setTag(tag);
            statusImage.setBackgroundResource(R.drawable.ic_task_done);
        }

        tasksList.add(taskToAdd);
        tasksContainer.addView(taskToAdd);

        //Listener to delete the task
        ImageButton deleteTaskBtn = taskToAdd.findViewById(R.id.deleteTaskBtn);
        deleteTaskBtn.setTag(id);//needs to identify serial number of task

        statusImage.setOnClickListener(this);
        deleteTaskBtn.setOnClickListener(this);
    }

    void deleteTaskFromLayout(View v) {
        for(int i=0; i < tasksList.size(); i++) {
            View item = tasksList.get(i);
            ImageButton tmpBtn = item.findViewById(R.id.deleteTaskBtn);
            if (tmpBtn.getTag() == v.getTag()) {
                tasksContainer.removeView(item);
                EditText edtText = item.findViewById(R.id.taskTextEdtTxt);

                myDB.deleteTaskRow(edtText.getText().toString());
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.openNoteEditorBtn:
                Intent transitionWithoutData = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivityForResult(transitionWithoutData, 1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.addTaskBtn:
                EditText addTaskEdtTxt = findViewById(R.id.addTaskEdtTxt);
                String text = addTaskEdtTxt.getText().toString();
                addTaskToLayout(text, "0", Integer.toString(tasksList.size()));
                addTaskEdtTxt.setText("");

                //save to db
                myDB.saveTaskToDb(0, text);
                break;
            case R.id.deleteTaskBtn:
                deleteTaskFromLayout(view);
                break;
            case R.id.taskStatusBtn:
                LinearLayout parent = (LinearLayout) view.getParent();
                EditText edtTxt = parent.findViewById(R.id.taskTextEdtTxt);

                if (view.getTag() == "0") {
                    view.setTag("1");
                    view.setBackgroundResource(R.drawable.ic_task_done);
                    myDB.updateTaskInDb(1, edtTxt.getText().toString());
                } else  {
                    view.setTag("0");
                    view.setBackgroundResource(R.drawable.ic_yellow_circle);
                    myDB.updateTaskInDb(0, edtTxt.getText().toString());
                }
                break;
            case R.id.tvNotebooksTitle:
                notebookEdtTxt.clearFocus();
                break;
        }
    }
}