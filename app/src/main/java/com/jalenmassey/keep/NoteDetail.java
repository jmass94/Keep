package com.jalenmassey.keep;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

public class NoteDetail extends AppCompatActivity {

    private NoteDetailFragment ndf = new NoteDetailFragment();
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        catch (NullPointerException e) {
            Log.e("DisplayHomeAsUp", e.getMessage());
        }

        activity = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_detail, menu);
        return true;
    }

    private void navigateUp() {
        NavUtils.navigateUpFromSameTask(activity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home: {
                ndf.recordData();
                navigateUp();
                return true;
            }
            case R.id.delete_note: {
                verifyDelete();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            ndf.recordData();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Prompt user to ask if they want to discard changes, save, or cancel the exit operation
     */
    private void verifyDelete() {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        try {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        catch (NullPointerException e) {
            Log.e("GetWindowToken", e.getMessage());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.close_note)
                .setNegativeButton(R.string.confirm_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ndf.deleteNote();
                        NavUtils.navigateUpFromSameTask(activity);
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
