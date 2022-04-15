package net.penguincoders.doit.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.penguincoders.doit.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 2;
    private static final String NAME = "toDoListDatabase";
    
    private static final String TODO_TABLE = "todo";
    private static final String LINK_TABLE = "link";
    
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String IDPARENT = "idparent";
    private static final String IDCHILD = "idchild";
    
    private static final String CREATE_LINK_TABLE = 
        "CREATE TABLE " + LINK_TABLE + 
        "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
        + IDPARENT + " INTEGER, " 
        + IDCHILD + " INTEGER)";

    private static final String CREATE_TODO_TABLE = 
        "CREATE TABLE " + TODO_TABLE + 
        "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
        + TASK + " TEXT, " 
        + STATUS + " INTEGER)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
        db.execSQL(CREATE_LINK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion==1 && newVersion==2) {
            db.execSQL(CREATE_LINK_TABLE);
        } else {
            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + LINK_TABLE);
            // Create tables again
            onCreate(db);
        }
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        db.insert(TODO_TABLE, null, cv);
        
        deleteLink(id);
        addlink(id,parent);
    }

    public List<ToDoModel> getAllTasks(){
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(TODO_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        ToDoModel task = new ToDoModel();
                        task.setId(cur.getInt(cur.getColumnIndex(ID)));
                        task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        taskList.add(task);
                    }
                    while(cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return taskList;
    }

    public void updateStatus(int id, int status,String|] parent){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
        deleteLink(id);
        addlink(id,parent);
    }

    public void updateTask(int id, String task,String|] parent) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
        deleteLink(id);
        addlink(id,parent);
    }

    public void deleteTask(int id){
        db.delete(TODO_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
        deleteLink(id);
    }

    public void deleteLink(int id){
        db.delete(LINK_TABLE, IDPARENT + "= ?", new String[] {String.valueOf(id)});
        db.delete(LINK_TABLE, IDCHILD + "= ?", new String[] {String.valueOf(id)});
    }
    public void addlink(int id,String|] parent){
        for (String element : parent) {
            ContentValues cv = new ContentValues();
            cv.put(IDCHILD, id);
            cv.put(IDPARENT, element);
            db.insert(LINK_TABLE, null, cv);
        }
    }
}
