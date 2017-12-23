package net.ddns.kennhuang.christmastree;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 12/19/2017.
 */

public class PresentDataBase {
    public static final String TABLE_NAME = "item";
    public static final String KEY_ID = "_id";

    public static final String SPEAK_COLUMN = "speak";
    public static final String MODE_COLUMN = "mode";
    public static final String SPEAKGROUP_COLUMN = "speakGroup";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SPEAK_COLUMN + " TEXT, " +
                    MODE_COLUMN + " TEXT, " +
                    SPEAKGROUP_COLUMN + " INTEGER );";
    private SQLiteDatabase db;


    public PresentDataBase(Context context) {
        db = MyDBHelper.getDatabase(context);
    }

    public void close() {
        db.close();
    }

    PresentData[][] data = new PresentData[3][];
    /*
    public PresentData[] getData(int group){
        return data[group-1];
    }*/
    /*
    public void init(){
        for(int i=0;i<3;i++){
            data[i] = getDataByGroup(i+1);
        }
    }
    */
    public PresentData[] getDataByGroup(int group){
        PresentData[] sourceData = getAllData();
        int count = 0;
        Map<Integer, PresentData> map = new HashMap<>();
        for (int i=0;i<getNum();i++){
            if(sourceData[i].group == group){
                map.put(count,sourceData[i]);
                count++;
            }
        }
        PresentData[] data = new PresentData[count];

        for (int i=0;i<count;i++){
            data[i] = map.get(i);
        }

        return data;
    }

    public PresentData[] getAllData() {
        PresentData[] data = new PresentData[getNum()];

        for (int i = 0; i < getNum(); i++) {
            String where = KEY_ID + "=" + (i + 1);
            PresentData tempData = null;
            Cursor result = db.query(
                    TABLE_NAME, null, where, null, null, null, null, null);
            int id, group;
            String speak;
            String mode;
            if (result.moveToFirst()) {
                id = result.getInt(0);
                speak = result.getString(1);
                mode = result.getString(2);
                group = result.getInt(3);
                tempData = new PresentData();

                tempData.group = group;
                tempData.id = id;
                tempData.speak = speak;
                tempData.mode = mode;
            }
            data[i] = tempData;
            result.close();
        }

        return data;

    }

    public int getNum() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }

}
