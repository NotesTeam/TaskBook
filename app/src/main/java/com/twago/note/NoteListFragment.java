package com.twago.note;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import io.realm.Realm;
import io.realm.RealmResults;

public class NoteListFragment extends Fragment implements NoteListAdapterInterface, DialogInterface.OnDismissListener {

    private Button createNote;
    private Button deleteNote;
    private View view;
    private RecyclerView recyclerView;
    private NoteListAdapter noteListAdapter;


    public static NoteListFragment newInstance() {
        Bundle args = new Bundle();

        NoteListFragment fragment = new NoteListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_note_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.note_list_recycler_view);
        createNote = (Button) view.findViewById(R.id.button_create_note);
        deleteNote = (Button) view.findViewById(R.id.button_delete_note);

        createNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogFragment(Constants.NEW_NOTE_ID);
            }
        });

        deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        inflateRecyclerView();
        return view;
    }

    private void inflateRecyclerView() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Note> results = realm.where(Note.class).findAll();
        noteListAdapter = new NoteListAdapter(this,results);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(noteListAdapter);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        inflateRecyclerView();
    }

    @Override
    public void openDialogFragment(int id) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        DialogFragment newFragment = NoteEditorFragment.newInstance(id);
        newFragment.setStyle(DialogFragment.STYLE_NORMAL,R.style.FullScreenDialog);
        newFragment.show(fragmentTransaction, "");
    }

    @Override
    public void unblockDeleteButton() {
        deleteNote.setVisibility(View.VISIBLE);
        createNote.setVisibility(View.INVISIBLE);
    }

    @Override
    public void unblockCreateButton() {
        deleteNote.setVisibility(View.INVISIBLE);
        createNote.setVisibility(View.VISIBLE);
    }

}