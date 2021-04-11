package com.vaaq.notes;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    private static String TAG = NoteDatabase.class.getSimpleName();

    private static NoteDatabase instance;

    public abstract NoteDao noteDao();

    public static synchronized NoteDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){


        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            populateDatabaseInBackground(instance);
        }
    };

    static void populateDatabaseInBackground(NoteDatabase noteDatabase) {

        NoteDao noteDao = noteDatabase.noteDao();
        Observable
                .create(new ObservableOnSubscribe<Note>() {
                    @Override
                    public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Note> emitter) throws Throwable {

                        noteDao.insert(new Note("title 1", "description 1", 1));
                        noteDao.insert(new Note("title 2", "description 2", 2));
                        noteDao.insert(new Note("title 3", "description 3", 3));

                        emitter.onComplete();

                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Note>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d(TAG, "insert note onSubscribe called");
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Note note) {
                        Log.d(TAG, "insert note onNext called");
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d(TAG, "insert note onError called");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "insert note onComplete called");
                    }
                });


    }
}
