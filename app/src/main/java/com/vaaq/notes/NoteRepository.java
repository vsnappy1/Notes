package com.vaaq.notes;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.disposables.DisposableContainer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NoteRepository {

    private final String TAG = NoteRepository.class.getSimpleName();

    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;
    long insertedItemId = -1;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public NoteRepository(Application application) {
        NoteDatabase database = NoteDatabase.getInstance(application);
        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes();
    }

    public long insert(Note note) {
        insertedItemId  = -1;
        insertNoteInBackground(note);
        while (insertedItemId == -1){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return insertedItemId;
    }

    public void update(Note note) {
        updateNoteInBackground(note);
    }

    public void delete(Note note) {
        deleteNoteInBackground(note);
    }

    public void deleteAll() {
        deleteAllNotesInBackground();
    }

    public LiveData<List<Note>> getAllNotes() {  //Live data is automatically handled in background thread
        return allNotes;
    }


    private void insertNoteInBackground(Note note) {

        Observable
                .create(new ObservableOnSubscribe<Note>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Note> emitter) throws Throwable {

                        insertedItemId  = noteDao.insert(note);
                        emitter.onComplete();

                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Note>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                        Log.d(TAG, "insert note onSubscribe called");
                    }

                    @Override
                    public void onNext(@NonNull Note note) {
                        Log.d(TAG, "insert note onNext called");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "insert note onError called");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "insert note onComplete called");
                    }
                });


    }

    private void deleteNoteInBackground(Note note) {

        Observable
                .create(new ObservableOnSubscribe<Note>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Note> emitter) throws Throwable {

                        noteDao.delete(note);
                        emitter.onComplete();

                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Note>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                        Log.d(TAG, "delete note onSubscribe called");
                    }

                    @Override
                    public void onNext(@NonNull Note note) {
                        Log.d(TAG, "delete note onNext called");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "delete note onError called");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "delete note onComplete called");
                    }
                });


    }

    private void updateNoteInBackground(Note note) {

        Observable
                .create(new ObservableOnSubscribe<Note>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Note> emitter) throws Throwable {

                        noteDao.update(note);
                        emitter.onComplete();

                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Note>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                        Log.d(TAG, "update note onSubscribe called");
                    }

                    @Override
                    public void onNext(@NonNull Note note) {
                        Log.d(TAG, "update note onNext called");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "update note onError called");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "update note onComplete called");
                    }
                });


    }

    private void deleteAllNotesInBackground() {

        Observable
                .create(new ObservableOnSubscribe<Note>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Note> emitter) throws Throwable {

                        noteDao.deleteAll();
                        emitter.onComplete();

                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Note>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                        Log.d(TAG, "deleteAll note onSubscribe called");
                    }

                    @Override
                    public void onNext(@NonNull Note note) {
                        Log.d(TAG, "deleteAll note onNext called");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "deleteAll note onError called");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "deleteAll note onComplete called");
                    }
                });


    }

    public void dispose(){
        compositeDisposable.dispose();
    }
}

