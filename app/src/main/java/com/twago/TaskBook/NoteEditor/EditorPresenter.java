package com.twago.TaskBook.NoteEditor;

import com.twago.TaskBook.Module.Constants;
import com.twago.TaskBook.Module.Note;
import com.twago.TaskBook.NoteList.ListContract;
import com.twago.TaskBook.NoteMain.NoteMainActivity;
import com.twago.TaskBook.TaskBook;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;

class EditorPresenter implements EditorContract.UserActionListener {
    private static final String TAG = EditorPresenter.class.getSimpleName();
    private int existNoteId;
    private NoteMainActivity activity;
    private EditorContract.View noteEditFragmentView;
    EditorPresenter(NoteMainActivity activity, EditorContract.View noteEditFragmentView) {
        this.activity = activity;
        this.noteEditFragmentView = noteEditFragmentView;
        this.existNoteId = noteEditFragmentView.getEditedNoteId();
    }

    @Override
    public void inflateExistNoteData() {
        if (!isNoteNew()) {
            Note existNote = getNoteWithId(existNoteId);
            inflateExistNoteData(existNote);
        }
    }

    private void inflateExistNoteData(Note note) {
        if (note != null) {
            noteEditFragmentView.setTitleNoteEditText(note.getTitle());
            noteEditFragmentView.setTextNoteEditText(note.getText());
            TaskBook.getInstance().setTimeStamp(note.getDate());
        }
    }

    @Override
    public void saveNoteToDatabase() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (isNoteNew()) {
                    int newNoteId = generateNewId(realm);
                    createNewNote(realm, newNoteId);
                    noteEditFragmentView.notifyItemAdded(newNoteId);
                }
                else
                    updateExistNote();
            }
        });
    }

    private void createNewNote(Realm realm, int newNoteId) {
        if (!isNoteEmpty()) {
            Note note = new Note(newNoteId, noteEditFragmentView.getTitleNote(),
                    noteEditFragmentView.getTextNote(), TaskBook.getInstance().getTimeStamp(), false);
            realm.copyToRealm(note);
        }
    }

    private int generateNewId(Realm realm) {
        RealmResults<Note> results = realm.where(Note.class).findAll();
        if (results.isEmpty()) return 0;
        return results.max(Note.ID).intValue() + 1;
    }

    private void updateExistNote() {
        Note chosenNote = getNoteWithId(existNoteId);
        chosenNote.setTitle(noteEditFragmentView.getTitleNote());
        chosenNote.setText(noteEditFragmentView.getTextNote());
        chosenNote.setDate(TaskBook.getInstance().getTimeStamp());
    }


    private Note getNoteWithId(int noteId) {
        return Realm.getDefaultInstance().where(Note.class).equalTo(Note.ID, noteId).findFirst();
    }

    private boolean isNoteNew() {
        return existNoteId == Constants.NEW_NOTE_ID;
    }

    private boolean isNoteEmpty() {
        return noteEditFragmentView.getTitleNote().equals("") && noteEditFragmentView.getTextNote().equals("");
    }

    @Override
    public void setCurrentNoteDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(TaskBook.getInstance().getTimeStamp());

        Note chosenNote = getNoteWithId(existNoteId);
        if (chosenNote != null)
            calendar.setTimeInMillis(chosenNote.getDate());

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        TaskBook.getInstance().setTimeStamp(calendar.getTimeInMillis());
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(activity.getFragmentManager(), "Datepickerdialog");
    }
}
