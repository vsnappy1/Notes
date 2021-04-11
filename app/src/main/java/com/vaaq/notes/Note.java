package com.vaaq.notes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note implements Comparable<Note>{

    @PrimaryKey(autoGenerate = true)
    private long id;

    //@ColumnInfo(name = "column_title") /annotationProcessor / by default column name is variable name but by this line we change the column name
    private String title;

    private String description;

    //@Ignore     //This field is ignored while creating column in db
    private int priority;

    public Note(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(Note note) {
        return this.priority < note.priority? 1 : -1;
    }
}
