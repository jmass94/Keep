package com.jalenmassey.keep;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

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

    public NotesActivityFragment() {
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
        noteTitles.remove(pos);
        noteTexts.remove(pos);
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
        View rootView = inflater.inflate(R.layout.fragment_notes, container, false);

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

        GridView allNotes = (GridView)rootView.findViewById(R.id.notesLV);
        allNotes.setAdapter(adapter);

        return rootView;
    }

    /**
     * Custom adapter for GridView.
     * Shows a card with a title and part of the notes contents
     */
    public class NoteAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public NoteAdapter() {
            inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

            /**
             * TODO
             * On long click, user should be able to batch select notes for archiving
             */
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    return false;
                }
            });

            return v;
        }
    }
}
