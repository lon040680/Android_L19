package com.chien.mysqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyOpenHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;

    //建構方法必寫
    public MyOpenHelper(@Nullable Context context) {
        super(context, "students.db", null, VERSION);
    }
    //操作資料表的語法:新增資料表
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE classA(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, sex CHAR(1), score INTEGER)";
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
