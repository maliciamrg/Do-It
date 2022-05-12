package net.penguincoders.doit.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.malicia.mrg.utils.BuildHierarchyTree;
import net.penguincoders.doit.MainActivity;
import net.penguincoders.doit.Model.ParentModel;
import net.penguincoders.doit.Model.TaskModel;
import net.penguincoders.doit.Model.ToDoModel;

import java.util.*;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 4;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String LINK_TABLE = "link";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String ISPROJECT = "isproject";
    private static final String IDPARENT = "idparent";
    private static final String IDCHILD = "idchild";
    private static final String NUMCOLOR = "numcolor";

    private static final String CREATE_LINK_TABLE =
            "CREATE TABLE " + LINK_TABLE + " " +
                    "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + IDPARENT + " INTEGER, "
                    + IDCHILD + " INTEGER)";

    private static final String CREATE_TODO_TABLE =
            "CREATE TABLE " + TODO_TABLE + " " +
                    "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TASK + " TEXT, "
                    + ISPROJECT + " BOOLEAN, "
                    + STATUS + " INTEGER, "
                    + NUMCOLOR + " INTEGER)";

    private static final String ALTER_TODO_TABLE_V2 =
            "ALTER TABLE " + TODO_TABLE + " " +
                    "ADD COLUMN " + ISPROJECT + " BOOLEAN ";

    private static final String ALTER_TODO_TABLE_V3 =
            "ALTER TABLE " + TODO_TABLE + " " +
                    "ADD COLUMN " + NUMCOLOR + " INTEGER ";

    private final String SELECT_CHILD_TODO =
            "SELECT t.* FROM " + TODO_TABLE + " t " +
                    " INNER JOIN " + LINK_TABLE + " l " +
                    "  ON t." + ID + "=l." + IDCHILD + " " +
                    " WHERE l." + IDPARENT + "=? " +
                    " ORDER BY t." + TASK + " ASC ";
    private final String SELECT_PARENT_TODO =
            "SELECT t.* FROM " + TODO_TABLE + " t " +
                    " INNER JOIN " + LINK_TABLE + " l " +
                    "  ON t." + ID + "=l." + IDPARENT + " " +
                    " WHERE l." + IDCHILD + "=? " +
                    " ORDER BY t." + TASK + " ASC ";

    private final String SELECT_LINK_TODO =
            "SELECT " +
                    "t." + TASK + " , " +
                    "l." + ID + " , " +
                    "t." + ID + " as " + IDCHILD + " , " +
                    "l." + IDPARENT + " " +
                    "FROM " + TODO_TABLE + " t " +
                    " LEFT JOIN " + LINK_TABLE + " l " +
                    "  ON l." + IDCHILD + "=t." + ID + " ";

    private final String SELECT_LINK =
            "SELECT l." + ID + " " +
                    "FROM " + LINK_TABLE + " l " +
                    "  WHERE l." + IDPARENT + "=? " +
                    "    AND l." + IDCHILD + "=? " ;
/*
    private final String SELECT_PARENT_TODO =
            "SELECT " +
                    "t." + ID + " " +
                    "ISNULL(" + IDPARENT + ",'0') as " + STATUS + " " +
                    "t." + TASK + " " +
                    "FROM " + TODO_TABLE + " t " +
                    " LEFT OUTER JOIN " + LINK_TABLE + " l " +
                    "  ON t." + ID + "=l." + IDPARENT + " " +
                    " WHERE l." + IDCHILD + "=? " +
                    " ORDER BY t." + TASK + " ASC ";*/

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
        int upgradeVersion = oldVersion;
        if (upgradeVersion == 1) {
            Log.i(MainActivity.LOG_TAG, "Upgrade database version " + upgradeVersion + " to +1");
            db.execSQL(CREATE_LINK_TABLE);
            upgradeVersion++;
        }
        if (upgradeVersion == 2) {
            Log.i(MainActivity.LOG_TAG, "Upgrade database version " + upgradeVersion + " to +1");
            db.execSQL(ALTER_TODO_TABLE_V2);
            upgradeVersion++;
        }
        if (upgradeVersion == 3) {
            Log.i(MainActivity.LOG_TAG, "Upgrade database version " + upgradeVersion + " to +1");
            db.execSQL(ALTER_TODO_TABLE_V3);
            upgradeVersion++;
        }

        if (newVersion > upgradeVersion) {
            Log.i(MainActivity.LOG_TAG, "Recreate database version to " + newVersion);
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

    public void insertTask(String task, Integer[] parent, Boolean isProject, int mDefaultColor) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        cv.put(STATUS, false);
        cv.put(ISPROJECT, isProject);
        cv.put(NUMCOLOR, mDefaultColor);
        long id = db.insert(TODO_TABLE, null, cv);
        if (id > -1) {
            deleteLink((int) id);
            addlink((int) id, parent);
        }
    }

    public HashMap<Integer,ToDoModel> getAllTasks() {
        HashMap<Integer,ToDoModel> taskList = new LinkedHashMap<>();

        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.query(TODO_TABLE, null, null, null, null, null, TASK, null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        int curId = cur.getInt(cur.getColumnIndex(ID));
                        ToDoModel task = new ToDoModel(curId,
                                cur.getString(cur.getColumnIndex(TASK)),
                                cur.getInt(cur.getColumnIndex(ISPROJECT)) > 0,
                                cur.getInt(cur.getColumnIndex(STATUS)) > 0,
                                cur.getInt(cur.getColumnIndex(NUMCOLOR)) );
                        taskList.put(task.getId(),task);
                    }
                    while (cur.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }

        return taskList;
    }

    public List<TaskModel> getAllParentTasks(int id) {
        List<TaskModel> parentTaskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.rawQuery(SELECT_PARENT_TODO, new String[]{String.valueOf(id)});
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        TaskModel task = new TaskModel(cur.getInt(cur.getColumnIndex(ID)),
                                cur.getString(cur.getColumnIndex(TASK)),
                                cur.getInt(cur.getColumnIndex(ISPROJECT)) > 0,
                                cur.getInt(cur.getColumnIndex(STATUS)) > 0,
                                cur.getInt(cur.getColumnIndex(NUMCOLOR)) ,
                                new ArrayList<TaskModel>(),
                                new ArrayList<TaskModel>());
                        parentTaskList.add(task);
                    }
                    while (cur.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return parentTaskList;
    }

    public List<TaskModel> getAllChildTasks(int id) {
        List<TaskModel> childTaskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.rawQuery(SELECT_CHILD_TODO, new String[]{String.valueOf(id)});
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        TaskModel task = new TaskModel(cur.getInt(cur.getColumnIndex(ID)),
                                cur.getString(cur.getColumnIndex(TASK)),
                                cur.getInt(cur.getColumnIndex(ISPROJECT)) > 0,
                                cur.getInt(cur.getColumnIndex(STATUS)) > 0,
                                cur.getInt(cur.getColumnIndex(NUMCOLOR)) ,
                                new ArrayList<TaskModel>(),
                                new ArrayList<TaskModel>());
                        childTaskList.add(task);
                    }
                    while (cur.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return childTaskList;
    }

    /*
    public List<ToDoModel> getPotentialParentTasks(int id) {
        List<ToDoModel> parentList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.rawQuery(SELECT_PARENT_TODO, new String[]{String.valueOf(id)});
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        ToDoModel task = new ToDoModel(cur.getInt(cur.getColumnIndex(ID)), cur.getString(cur.getColumnIndex(TASK)), cur.getInt(cur.getColumnIndex(STATUS)) > 0, new ArrayList<ToDoModel>());
                        task.setParent(cur.getInt(cur.getColumnIndex(IDCHILD))==id && cur.getString(cur.getColumnIndex(IDPARENT))!=null);
                        parentList.add(task);
                    }
                    while (cur.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return parentList;
    }*/

    public void updateStatus(int id, boolean status) {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[]{String.valueOf(id)});
//        deleteLink(id);
//        addlink(id,parent);
    }

    public void updateTask(int id, String task, Integer[] parent, Boolean isProject, int mDefaultColor) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        cv.put(ISPROJECT, isProject);
        cv.put(NUMCOLOR, mDefaultColor);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[]{String.valueOf(id)});
        deleteLinkChild(id);
        addlink(id, parent);
    }

    public void deleteTask(int id) {
        db.delete(TODO_TABLE, ID + "= ?", new String[]{String.valueOf(id)});
        deleteLink(id);
    }

    public void deleteLink(int id) {
        deleteLinkParent(id);
        deleteLinkChild(id);
    }

    public void deleteLinkChild(int id) {
        db.delete(LINK_TABLE, IDCHILD + "= ?", new String[]{String.valueOf(id)});
    }

    public void deleteLinkParent(int id) {
        db.delete(LINK_TABLE, IDPARENT + "= ?", new String[]{String.valueOf(id)});
    }

    public void addlink(int childId, Integer[] parent) {
        for (int element : parent) {
            if (childId != element) {
                ContentValues cv = new ContentValues();
                cv.put(IDCHILD, childId);
                cv.put(IDPARENT, element);
                Cursor cur = db.rawQuery(SELECT_LINK, new String[]{String.valueOf(element), String.valueOf(childId)});
                if (cur.getCount()==0) {
                    db.insert(LINK_TABLE, null, cv);
                }
            }
        }
    }

    public List<ParentModel> getAllLinks() {
        List<ParentModel> linkList = new ArrayList<>();

        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.rawQuery(SELECT_LINK_TODO, new String[]{});
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        ParentModel linkModel = new ParentModel(
                                cur.getInt(cur.getColumnIndex(ID)),
                                cur.getInt(cur.getColumnIndex(IDCHILD)),
                                cur.getString(cur.getColumnIndex(TASK)),
                                cur.getInt(cur.getColumnIndex(IDPARENT)));
                        linkList.add(linkModel);
                    }
                    while (cur.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }


        return linkList;
    }
}
