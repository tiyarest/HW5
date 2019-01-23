package com.camp.bit.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.camp.bit.todolist.beans.Note;
import com.camp.bit.todolist.beans.State;
import com.camp.bit.todolist.db.TodoContract;
import com.camp.bit.todolist.db.TodoDbHelper;
import com.camp.bit.todolist.debug.DebugActivity;
import com.camp.bit.todolist.ui.NoteListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;
    private TodoDbHelper todoDbHelper;
    private List<Note> list;
    private SQLiteDatabase db;
    private String TAG = "sql_result:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        todoDbHelper = new TodoDbHelper(getBaseContext());
        db = todoDbHelper.getReadableDatabase();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        todoDbHelper.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans
        if(db==null)
        {
            return Collections.emptyList();
        }
        String[] projection ={
                BaseColumns._ID,
                TodoContract.TodoEntry.TABLE_CONTENT,
                TodoContract.TodoEntry.TABLE_STATE,
                TodoContract.TodoEntry.TABLE_DATA,
                TodoContract.TodoEntry.TABLE_PRIORITY,
        };
        Cursor cursor = null;
        try{
            cursor = db.query(
                    TodoContract.TodoEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    TodoContract.TodoEntry.TABLE_PRIORITY+" DESC"


            );
            list= new ArrayList<>();
            while(cursor.moveToNext()){
                String content = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.TABLE_CONTENT));
                int state = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.TABLE_STATE));
                String pri = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.TABLE_PRIORITY));
                long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoEntry._ID));
                long dateMs= cursor.getLong(cursor.getColumnIndexOrThrow(TodoContract.TodoEntry.TABLE_DATA));
                Note n = new Note(id);
                n.setPriority(pri);
                Log.d("pri:",pri);
                n.setContent(content);
                n.setDate(new Date(dateMs));
                n.setState(State.from(state));
                list.add(n);
            }
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
            }
        }

        return list;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        Log.d(TAG, "deleteNote() called with: note = [" + note + "]");
        String selection = TodoContract.TodoEntry._ID +"="+note.getId();
        int deletedRows = db.delete(TodoContract.TodoEntry.TABLE_NAME,selection,null);
        notesAdapter.refresh(loadNotesFromDatabase());
        Log.d(TAG, "deleteNote() called with: deletedRows = [" + deletedRows + "]");


    }

    private void updateNode(Note note) {
        // 更新数据
        ContentValues values = new ContentValues();
        values.put(TodoContract.TodoEntry.TABLE_STATE,note.getState().intValue);
        String selection = TodoContract.TodoEntry._ID +"="+note.getId();

        notesAdapter.refresh(loadNotesFromDatabase());
        int count = db.update(TodoContract.TodoEntry.TABLE_NAME,values,selection,null);

    }

}
