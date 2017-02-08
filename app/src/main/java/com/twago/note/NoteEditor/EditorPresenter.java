package com.twago.note.NoteEditor;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.widget.ImageView;
import android.widget.TextView;

import com.twago.note.Constants;
import com.twago.note.Note;
import com.twago.note.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;

class EditorPresenter implements EditorContract.UserActionListener {
    private int noteId;
    private String task = "";
    private long currentNoteDate = Calendar.getInstance().getTimeInMillis();
    private Activity activity;
    private EditorContract.View noteEditFragmentView;

    EditorPresenter(Activity activity, EditorContract.View noteEditFragmentView) {
        this.activity = activity;
        this.noteEditFragmentView = noteEditFragmentView;
        noteId = noteEditFragmentView.getNoteId();
    }

    @Override
    public void inflateOldData() {
        if (!isNoteNew())
            inflateNoteData(noteId);
    }

    private void inflateNoteData(int noteId) {
        Note note = getNoteWithId(noteId);
        inflateNoteData(note);
    }

    private void inflateNoteData(Note note) {
        if (note != null) {
            noteEditFragmentView.getTitleNoteEditText().setText(note.getTitle());
            noteEditFragmentView.getTextNoteEditText().setText(note.getText());
            task = note.getTask();
            currentNoteDate = note.getDate();
        }
    }



    @Override
    public void saveNoteToDatabase() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (isNoteNew()) {
                    if (!isNoteEmpty())
                        createNewNote(realm);
                } else
                    updateNote(realm);
            }
        });
    }

    private void createNewNote(Realm realm) {
        Note note = new Note(generateNewId(realm), noteEditFragmentView.getTitleNote(),
                noteEditFragmentView.getTextNote(), task, currentNoteDate);
        realm.copyToRealm(note);
    }

    private void updateNote(Realm realm) {
        Note note = realm.where(Note.class).equalTo(Note.ID, noteId).findFirst();
        if (note != null) {
            note.setTitle(noteEditFragmentView.getTitleNote());
            note.setText(noteEditFragmentView.getTextNote());
            note.setTask(task);
            note.setDate(currentNoteDate);
        }
    }

    private boolean isNoteNew() {
        return noteId == Constants.NEW_NOTE_ID;
    }

    private boolean isNoteEmpty() {
        return noteEditFragmentView.getTitleNote().equals("") && noteEditFragmentView.getTextNote().equals("");
    }

    private int generateNewId(Realm realm) {
        RealmResults<Note> results = realm.where(Note.class).findAll();
        if (results.isEmpty()) return 0;
        return results.max(Note.ID).intValue() + 1;
    }

    private Note getNoteWithId(int noteId) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Note.class).equalTo(Note.ID, noteId).findFirst();
    }



    @Override
    public void pickDate(EditorFragment editorFragment) {
        Note note = getNoteWithId(noteId);
        Calendar calendar = Calendar.getInstance();

        if (note != null)
            calendar.setTimeInMillis(note.getDate());

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        currentNoteDate = calendar.getTimeInMillis();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(activity.getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void pickTaskNote(ImageView taskIcon) {

        switch (taskIcon.getId()) {
            case R.id.main_task_note_editor:
                task = Constants.MAIN_TASK;
                break;
            case R.id.part_task_note_editor:
                task = Constants.PART_TASK;
                break;
            case R.id.skills_task_note_editor:
                task = Constants.SKILLS_TASK;
                break;
            case R.id.unimportant_task_note_editor:
                task = Constants.UNIMPORTANT_TASK;
                break;
        }

    }

}
