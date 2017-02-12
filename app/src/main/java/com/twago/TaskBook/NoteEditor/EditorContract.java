package com.twago.TaskBook.NoteEditor;

import android.widget.EditText;
import android.widget.ImageView;

public interface EditorContract {
    interface View {
        String getTextNote();

        String getTitleNote();

        EditText getTitleNoteEditText();

        EditText getTextNoteEditText();

        void blockArchivedNoteViews();

        int getNoteId();
    }

    interface UserActionListener {
        void pickTaskNote(ImageView taskIcon);

        void pickDate(EditorFragment editorFragment);

        void inflateOldData();

        void saveNoteToDatabase();
    }
}