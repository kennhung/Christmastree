package net.ddns.kennhuang.christmastree;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 12/16/2017.
 */

public class MyDBHelper extends SQLiteOpenHelper {

    private final static int _DBVersion = 2;
    private final static String _DBName = "ChristmasTree.db";
    private static SQLiteDatabase database;

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new MyDBHelper(context, _DBName,
                    null, _DBVersion).getWritableDatabase();
        }

        return database;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PresentDataBase.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + PresentDataBase.TABLE_NAME);
        onCreate(db);
    }
}
