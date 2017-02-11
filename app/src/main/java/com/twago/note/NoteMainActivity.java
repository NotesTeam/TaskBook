package com.twago.note;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.twago.note.NoteEditor.EditorContract;
import com.twago.note.NoteEditor.EditorFragment;
import com.twago.note.NoteList.ListContract;
import com.twago.note.NoteList.ListFragment;
import com.twago.note.NoteList.ListPresenter;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import io.realm.Realm;

public class NoteMainActivity extends AppCompatActivity {
    private static final String TAG = NoteMainActivity.class.getSimpleName();
    private ListFragment noteListFragment;
    private ListContract.UserActionListener userActionListener;

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        noteListFragment.toggleDrawer();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_main);
        setTitle(R.string.tasks);
        Realm.init(this);

        initMenuButton();
        noteListFragment = ListFragment.newInstance();
        initSetUp();
    }

    private void initMenuButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
    }

    private void initSetUp() {
        noteListFragment = new ListFragment();
        userActionListener = new ListPresenter(this, noteListFragment);
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_layout, noteListFragment)
                .commit();
    }

    public void choseDate(MenuItem item) {
    }
}
