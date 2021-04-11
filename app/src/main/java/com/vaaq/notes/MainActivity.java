package com.vaaq.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;

    RecyclerView recyclerView;
    FloatingActionButton floatingActionButtonAddNote;
    NoteAdapter adapter;

    boolean onlyOnce = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                // update recyclerview
                if(onlyOnce){
//                    Toast.makeText(MainActivity.this, "onChanged", Toast.LENGTH_SHORT).show();
                    adapter.setNotes(notes);
                    onlyOnce = false;
                }
//                Toast.makeText(MainActivity.this, "onChanged", Toast.LENGTH_SHORT).show();
//                adapter.setNotes(notes);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButtonAddNote = findViewById(R.id.floatingActionButtonAddNote);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new NoteAdapter(MainActivity.this);
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note, int position) {
                showEditNoteDialog(note, position);
            }
        });
        recyclerView.setAdapter(adapter);

        floatingActionButtonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNoteDialog();
            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
               deleteNote(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.actinDeleteAllNotes) {
            showDeleteAllAlertDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void showAddNoteDialog(){
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_add_note);

        EditText editTextTitle = dialog.findViewById(R.id.editTextTitle);
        EditText editTextDescription = dialog.findViewById(R.id.editTextDescription);
        NumberPicker numberPicker = dialog.findViewById(R.id.numberPicker);
        Button button = dialog.findViewById(R.id.ButtonAddNote);

        numberPicker.setMaxValue(10);
        numberPicker.setValue(1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = editTextTitle.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();
                int priority = numberPicker.getValue();

                if (title.isEmpty()) {
                    editTextTitle.setError("Please enter title");
                    return;
                }

                if (description.isEmpty()) {
                    editTextDescription.setError("Please enter description");
                    return;
                }

                Note note = new Note(title, description, priority);
                long id = noteViewModel.insert(note);
                note.setId(id);
                adapter.insertNote(note);
                Toast.makeText(MainActivity.this, "New note added", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        dialog.show();
    }

    void showEditNoteDialog(Note note, int position){
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_add_note);

        EditText editTextTitle = dialog.findViewById(R.id.editTextTitle);
        EditText editTextDescription = dialog.findViewById(R.id.editTextDescription);
        NumberPicker numberPicker = dialog.findViewById(R.id.numberPicker);
        Button button = dialog.findViewById(R.id.ButtonAddNote);

        editTextTitle.setText(note.getTitle());
        editTextDescription.setText(note.getDescription());

        numberPicker.setMaxValue(10);
        numberPicker.setValue(note.getPriority());
        button.setText("Save");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = editTextTitle.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();
                int priority = numberPicker.getValue();

                if (title.isEmpty()) {
                    editTextTitle.setError("Please enter title");
                    return;
                }

                if (description.isEmpty()) {
                    editTextDescription.setError("Please enter description");
                    return;
                }

                Note updatedNote = new Note(title, description, priority);
                updatedNote.setId(note.getId());

                noteViewModel.update(updatedNote);
                adapter.updateNote(position, updatedNote);
                Toast.makeText(MainActivity.this, "Note updated", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        dialog.show();
    }

    void deleteNote(int position){
        noteViewModel.delete(adapter.getNoteAt(position));
        adapter.removeNote(position);
        Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
    }

    void showDeleteAllAlertDialog(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete all notes")
                .setMessage("Are you sure you want to delete all notes?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        noteViewModel.deleteAllNotes();
                        adapter.removeAllNotes();
                        Toast.makeText(MainActivity.this, "All notes deleted", Toast.LENGTH_SHORT).show();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}