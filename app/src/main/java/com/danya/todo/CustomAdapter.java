package com.danya.todo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private Context context;
    private ArrayList noteId, noteTitle, noteText, noteEditTime;
    int position;

    Activity activity;

    CustomAdapter(Activity activity, Context context,
                  ArrayList noteId,
                  ArrayList noteTitle,
                  ArrayList noteText,
                  ArrayList noteEditTime) {
        this.activity = activity;
        this.context = context;
        this.noteId = noteId;
        this.noteTitle = noteTitle;
        this.noteText = noteText;
        this.noteEditTime = noteEditTime;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_note_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.noteTitle_txt.setText(String.valueOf(noteTitle.get(position)));
        holder.noteText_txt.setText(String.valueOf(noteText.get(position)));
        holder.noteEditText_txt.setText(String.valueOf(noteEditTime.get(position)));//add "last edit ${time}"
        holder.linLayNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChangeNoteActivity.class);
                intent.putExtra("id", String.valueOf(noteId.get(position)));
                intent.putExtra("title", String.valueOf(noteTitle.get(position)));
                intent.putExtra("text", String.valueOf(noteText.get(position)));
                activity.startActivityForResult(intent, 1);
                //OverridePending... needs to animate transition to new activity
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteTitle.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle_txt, noteText_txt, noteEditText_txt;
        LinearLayout linLayNote;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle_txt = itemView.findViewById(R.id.tvNoteTitle);
            noteText_txt = itemView.findViewById(R.id.tvNoteText);
            noteEditText_txt = itemView.findViewById(R.id.tvNoteEditTime);
            linLayNote = itemView.findViewById(R.id.linLayNote);
        }
    }
}
