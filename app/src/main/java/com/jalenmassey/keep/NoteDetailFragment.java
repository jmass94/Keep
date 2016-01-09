package com.jalenmassey.keep;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class NoteDetailFragment extends Fragment {

    private static EditText noteField, titleField;
    private static int pos = -1;

    public NoteDetailFragment() {
    }

    /**
     * Get text from both EditTexts and send data to Main Page
     */
    public void recordData() {
        if(titleField != null
                &&
           noteField != null) {
            String title = titleField.getText().toString();
            String note = noteField.getText().toString();

            //String to be sent
            String[] combine = new String[2];
            if(title.trim().length() == 0
                    &&
               note.trim().length() == 0) {
                //Discard empty note
            }
            else {
                if(title.trim().length() > 0) {
                    combine[0] = title;
                    combine[1] = note;
                }
                else if(title.trim().length() == 0) {
                    combine[0] = "Untitled";
                }
                NotesActivityFragment naf = new NotesActivityFragment();
                naf.addNote(combine, pos);
            }
        }
        else {
            Log.e("GetText", "Failed to getText() on null EditText");
        }
    }

    /**
     * Remove note from grid
     */
    public void deleteNote() {
        NotesActivityFragment naf = new NotesActivityFragment();
        naf.deleteNote(pos);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final int TITLE = 0;
        final int TEXT  = 1;
        final int OVER  = 2;
        View view = inflater.inflate(R.layout.fragment_note_detail, container, false);

        noteField = (EditText)view.findViewById(R.id.detail_note);
        titleField = (EditText)view.findViewById(R.id.detail_title);

        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra("DATA")) {
            String[] data = intent.getStringArrayExtra("DATA");

            noteField.setText(data[TEXT]);
            titleField.setText(data[TITLE]);
            pos = Integer.parseInt(data[OVER]);
        }
        else {
            pos = -1;
        }

        return view;
    }
}
