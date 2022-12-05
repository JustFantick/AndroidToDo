package com.danya.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AddNoteActivity extends AppCompatActivity {
    ImageView addNoteBtn;
    EditText noteTitle, noteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        addNoteBtn = findViewById(R.id.addNoteBtn);
        noteText = findViewById(R.id.notesText);
        noteTitle = findViewById(R.id.notesTitle);

        addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (noteTitle.getText().toString().equals("") || noteText.getText().toString().equals("")) {
                    Toast.makeText(AddNoteActivity.this, "You didn`t entered all info", Toast.LENGTH_SHORT).show();
                } else {
                    DataBaseHelper DataBaseHelper = new DataBaseHelper(AddNoteActivity.this);
                    DataBaseHelper.addNoteToDb(noteTitle.getText().toString().trim(), noteText.getText().toString().trim());
                    finish();
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}