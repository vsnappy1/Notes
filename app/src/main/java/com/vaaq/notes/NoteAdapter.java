package com.vaaq.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    private List<Note> notes = new ArrayList<>();
    OnItemClickListener listener;
    Context context;

    public NoteAdapter(Context context) {
        this.context = context;
    }

    public class NoteHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewPriority;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewPriority = itemView.findViewById(R.id.textViewPriority);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(notes.get(position), getAdapterPosition());
                    }
                }
            });
        }
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    public Note getNoteAt(int position) {
        return notes.get(position);
    }

    public void insertNote(Note note){
        notes.add(note);
        Collections.sort(notes);
        notifyItemInserted(notes.indexOf(note));
    }

    public void removeNote(int position){
        notes.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAllNotes(){
        notifyItemRangeRemoved(0, notes.size());
        notes.clear();
    }


    public void updateNote(int position, Note note){
        if(notes.get(position).getPriority() == note.getPriority()){
            notes.set(position, note);
            Collections.sort(notes);
            notifyItemChanged(notes.indexOf(note));
        }
        else {
            removeNote(position);
            insertNote(note);
        }

    }


    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {

        Note note = notes.get(position);

        holder.textViewTitle.setText(note.getTitle());
        holder.textViewDescription.setText(note.getDescription());
        holder.textViewPriority.setText(String.valueOf(note.getPriority()));

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    interface OnItemClickListener {
        void onItemClick(Note note, int position);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
