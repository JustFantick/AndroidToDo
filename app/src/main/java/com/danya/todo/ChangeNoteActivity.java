package com.danya.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChangeNoteActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView saveNoteBtn;
    EditText noteTitle, noteText;
    String noteId, noteTitle_text, noteText_text;
    ImageView deleteNotebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_note);

        saveNoteBtn = findViewById(R.id.saveNoteBtn);
        noteText = findViewById(R.id.notesText2);
        noteTitle = findViewById(R.id.notesTitle2);
        deleteNotebtn = findViewById(R.id.deleteNoteBtn);

        getAndSetIntentData();
        saveNoteBtn.setOnClickListener(this);
        deleteNotebtn.setOnClickListener(this);
    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra(("id")) && getIntent().hasExtra(("title")) && getIntent().hasExtra(("text"))) {
            //get data
            noteTitle_text = getIntent().getStringExtra("title");
            noteText_text = getIntent().getStringExtra("text");
            noteId = getIntent().getStringExtra("id");
            //Toast.makeText(this, noteId, Toast.LENGTH_SHORT).show();
            Log.d("Need to enter TAG", noteId);

            //set data
            noteTitle.setText(noteTitle_text);
            noteText.setText(noteText_text);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveNoteBtn:
                DataBaseHelper db = new DataBaseHelper(ChangeNoteActivity.this);

                //get current time data
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                String editTime = formatter.format(date);

                noteTitle_text = noteTitle.getText().toString();
                noteText_text = noteText.getText().toString();

                db.updateData(noteId, noteTitle_text, noteText_text, editTime);
                finish();
                break;
            case R.id.deleteNoteBtn:
                DataBaseHelper db1 = new DataBaseHelper(ChangeNoteActivity.this);
                db1.deleteOneRow(noteId);
                finish();//hide ChangeNoteActivity after click on "X"
                break;
        }

    }
}