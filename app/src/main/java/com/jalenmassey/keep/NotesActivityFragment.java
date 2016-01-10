package com.jalenmassey.keep;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.CheckedInputStream;

/**
 * A placeholder fragment containing a simple view.
 */
public class NotesActivityFragment extends Fragment implements Serializable {

    private static NoteAdapter adapter;
    private static ArrayList<String> noteTexts;
    private static ArrayList<String> noteTitles;
    private final String FILE_NAMET = "priv_serial_notesT";
    private final String FILE_NAMEI = "priv_serial_notesI";
    private static FragmentActivity context;
    private static GridView allNotes;
    private View rootView;

    private static ActionMode mActionMode;
    private ActionMode mode;

    public NotesActivityFragment() {
    }

    public void selectAll() {
        for(int i = allNotes.getChildCount()-1; i >= 0; i--) {
            View v = allNotes.getChildAt(i);
            CheckBox cb = (CheckBox)v.findViewById(R.id.check_note);
            cb.setChecked(true);
            cb.setVisibility(View.VISIBLE);
        }
        mActionMode = context.startActionMode(mActionModeCallback);
    }

    public void addNote(String[] note, int pos) {
        if(pos >= 0) {
            noteTitles.remove(pos); noteTitles.add(pos, note[0]);
            noteTexts.remove(pos); noteTexts.add(pos, note[1]);
        }
        else {
            noteTitles.add(0, note[0]);
            noteTexts.add(0, note[1]);
        }
        save();
    }

    public void deleteNote(int pos) {
        if(pos >= 0) {
            noteTitles.remove(pos);
            noteTexts.remove(pos);
        }
        Toast t = Toast.makeText(context, R.string.success_delete, Toast.LENGTH_LONG);
        t.show();
        save();
    }

    /**
     * Output notes to files
     */
    private void save() {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAMET, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(noteTitles);
            oos.flush();
            oos.close();
            fos.close();
            fos = context.openFileOutput(FILE_NAMEI, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(noteTexts);
            oos.flush();
            oos.close();
            fos.close();
        }
        catch (Exception e) {
            Log.e("InternalStorage", e.getMessage());
        }
        adapter.notifyDataSetChanged();
    }


    /**
     * Read notes from saved files
     */
    private void read() {
        FileInputStream fis;
        try {
            fis = context.openFileInput(FILE_NAMET);
            ObjectInputStream ois = new ObjectInputStream(fis);
            @SuppressWarnings("unchecked")
            ArrayList<String> readIn = (ArrayList<String>)ois.readObject();
            noteTitles = readIn;
            ois.close();
            fis = context.openFileInput(FILE_NAMEI);
            ois = new ObjectInputStream(fis);
            @SuppressWarnings("unchecked")
            ArrayList<String> readIn2 = (ArrayList<String>)ois.readObject();
            noteTexts = readIn2;
            ois.close();
        }
        catch (Exception e) {
            Log.e("InternalStorage", e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notes, container, false);

        context = getActivity();

        noteTitles = new ArrayList<>();
        noteTexts = new ArrayList<>();
        String init1 = "Welcome!";
        String init2 = "This is a sample note. A placeholder, really. Getting started is really simple." +
                "Just press the 'add' button on the main screen to jot down a new note. It's easy!";
        noteTitles.add(init1);
        noteTexts.add(init2);
        read();

        adapter = new NoteAdapter();

        allNotes = (GridView)rootView.findViewById(R.id.notesLV);
        allNotes.setAdapter(adapter);

        return rootView;
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(context.getResources().getColor(R.color.colorAccentDark));
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.menu_notes_selecting, menu);
            mode = actionMode;
            mode.setTitle("Selection Mode");
            FloatingActionButton fab = (FloatingActionButton)context.findViewById(R.id.fab);
            fab.setVisibility(View.GONE);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            Log.v("CAB", "Prep CAB");
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            Log.v("CAB", String.valueOf(allNotes.getChildCount()));
            uncheck();
            mActionMode = null;
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(context.getResources().getColor(R.color.colorPrimaryDark));
            FloatingActionButton fab = (FloatingActionButton)context.findViewById(R.id.fab);
            fab.setVisibility(View.VISIBLE);
        }

        private void uncheck() {
            for(int i = allNotes.getChildCount()-1; i >= 0; i--) {
                View v = allNotes.getChildAt(i);
                CheckBox cb = (CheckBox)v.findViewById(R.id.check_note);
                cb.setChecked(false);
                cb.setVisibility(View.GONE);
            }
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            Log.v("CAB", "Clicked CAB");
            switch(menuItem.getItemId()) {
                case R.id.delete_notes_selected: {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.confirm_delete_selected)
                            .setNegativeButton(R.string.confirm_delete, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    for(int y = allNotes.getChildCount()-1; y >= 0; y--) {
                                        View v = allNotes.getChildAt(y);
                                        CheckBox cb = (CheckBox)v.findViewById(R.id.check_note);
                                        if(cb.isChecked()) {
                                            noteTitles.remove(y);
                                            noteTexts.remove(y);
                                            save();
                                        }
                                        cb.setChecked(false);
                                    }
                                    //uncheck();
                                }
                            })
                            .setNeutralButton(R.string.cancel_close, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });

                    builder.create().show();
                }
            }
            return true;


        }
    };

    /**
     * Custom adapter for GridView.
     * Shows a card with a title and part of the notes contents
     */
    public class NoteAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public NoteAdapter() {
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if(noteTitles != null)
                return noteTitles.size();
            else return 0;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public class Hold {
            CheckBox checkBox;
            TextView title;
            TextView sub;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            Hold hold = new Hold();
            if(view == null) {
                view = inflater.inflate(R.layout.note_text, viewGroup, false);
            }
            View v = view;
            hold.title = (TextView)v.findViewById(R.id.title_text);
            hold.sub = (TextView)v.findViewById(R.id.info_text);
            hold.checkBox = (CheckBox)v.findViewById(R.id.check_note);
            final CheckBox cB = hold.checkBox;
            if(mActionMode == null)
                cB.setVisibility(View.GONE);
            try {
                hold.title.setText(noteTitles.get(i));
                hold.sub.setText(noteTexts.get(i));
            }
            catch(IndexOutOfBoundsException e) {
                Log.e("IndexingEditText", e.getMessage());
            }

            /**
             * Open note
             */
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), NoteDetail.class);
                    String[] send = {noteTitles.get(i), noteTexts.get(i), String.valueOf(i)};
                    intent.putExtra("DATA", send);
                    startActivity(intent);
                }
            });

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(mActionMode == null) {
                        mActionMode = context.startActionMode(mActionModeCallback);
                        showChecks();
                    }
                    cB.setChecked(true);
                    return true;
                }
            });

            return v;
        }

        private void showChecks() {
            View v;
            for(int i = 0; i < allNotes.getChildCount(); i++) {
                v = allNotes.getChildAt(i);
                CheckBox cB = (CheckBox)v.findViewById(R.id.check_note);
                cB.setVisibility(View.VISIBLE);
            }
        }
    }
}
