package com.chien.mysqlite;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    TextView edId;
    EditText edName, edSex, edScore;
    ListView lv;
    MyOpenHelper helper;
    AlertDialog dialog;
    Cursor cursor;
    String fid = "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edId = findViewById(R.id.sId);
        edName = findViewById(R.id.sName);
        edSex = findViewById(R.id.sSex);
        edScore = findViewById(R.id.sScore);
        lv = findViewById(R.id.lv);
        helper = new MyOpenHelper(this);
        //顯示所有資料
        showAllData();
    }

    //按鍵監聽(第一排)
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add:
                insertData();
                break;
            case R.id.show:
                showAllData();
                break;
            case R.id.find:
                queryDate();
                break;
            case R.id.find2:
                queryData2();
                break;
            case R.id.update:
                updateData();
                break;
            case R.id.delete:
                deleteData();
                break;
        }
    }

    //自訂方法 顯示所有資料 (重新整理)
    private void showAllData() {
        //資料獲取 沒有規則
        cursor = helper.getReadableDatabase().query("classA",
                null,null,null,
                null,null,null);
        //adapter轉換
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.item,
                cursor,
                new String[]{"_id","name","sex","score"},//資料表欄位名
                new int[]{R.id.itemId, R.id.itemName, R.id.itemSex, R.id.itemScore}
        );
        //adapter set listview
        lv.setAdapter(adapter);
    }

    //新增資料
    void insertData(){
        SQLiteDatabase db = helper.getReadableDatabase();
        String name = edName.getText().toString().trim();
        String sex = edSex.getText().toString().trim();
        String score = edScore.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("sex", sex);
        values.put("score", score);

        long lowId = db.insert("classA", null, values);
        if(lowId >0){
            Toast.makeText(MainActivity.this, "新增成功", Toast.LENGTH_SHORT);
        } //lowId >0 | !=-1 都會顯示 (這指的是資料 ID 自動加 1 的部分)
        db.close();
        showAllData();

        edName.setText("");
        edSex.setText("");
        edScore.setText("");
    }

    //查詢跳窗2 : 以姓名為條件
    private void queryData2(){
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_view2, null);
        final EditText idTxt = view.findViewById(R.id.idTxt);

        dialog = new AlertDialog.Builder(this).setView(view)
                .setPositiveButton("確定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fid = idTxt.getText().toString().trim();
                        findData2(fid);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fid="-1";
                    }
                })
                .create();
        dialog.show();
    }

    //查詢跳窗2 : 以姓名為條件
    private void findData2(String fid) {
        //送指令給 SQLite
        Cursor cursor = helper.getReadableDatabase().query("classA",
                null,"name LIKE?", new String[]{"%"+fid+"%"},
                null,null,null);

        //adapter轉換
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.item,
                cursor,
                new String[]{"_id","name","sex","score"},//資料表欄位名
                new int[]{R.id.itemId, R.id.itemName, R.id.itemSex, R.id.itemScore}
        );

        //adapter set listview
        lv.setAdapter(adapter);
    }

    //查詢跳窗 : 以id 為條件 獲得id 送給 findData()
    private void queryDate(){
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_view, null);
        final EditText idTxt = view.findViewById(R.id.idTxt);

        dialog = new AlertDialog.Builder(this).setView(view)
                                .setPositiveButton("確定", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        fid = idTxt.getText().toString().trim();
                                        findData(fid);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        fid="-1";
                                    }
                                })
                                .create();
        dialog.show();
    }

    //查詢跳窗 ：以id為條件 獲得資料顯示
    private void findData(String fid) {
        //送指令給 SQLite
        Cursor cursor = helper.getReadableDatabase().query("classA",
                null,"_id=?", new String[]{fid},
                null,null,null);

        //如果有資料 往前移到第一個
        if(cursor.moveToFirst()){
            String name2 = cursor.getString(cursor.getColumnIndex("name"));
            String sex2 = cursor.getString(cursor.getColumnIndex("sex"));
            String score2 = cursor.getString(cursor.getColumnIndex("score"));
            //放到上面的欄位
            edName.setText(name2);
            edSex.setText(sex2);
            edScore.setText(score2);
            edId.setText(fid);
        }else{
            edName.setText("No Data");
            edSex.setText("No Data");
            edScore.setText("No Data");
            edId.setText(fid);
        }
    }

    //更新
    private void updateData() {
        String newId = cursor.getString(cursor.getColumnIndex("_id")); //獲得_id 以_id為條件去做修改 因為_id不可以改
        String newName = edName.getText().toString().trim();
        String newSex = edSex.getText().toString().trim();
        String newScore = edScore.getText().toString().trim();

        SQLiteDatabase db = helper.getReadableDatabase(); //先進入資料庫
        ContentValues values = new ContentValues(); //容器
        values.put("name",newName); //逐步放入
        values.put("sex",newSex);
        values.put("score",newScore);

        //確認有沒有更新資料
        int line = db.update("classA", values,
                            "_id=?",
                            new String[]{fid});

        if(line > 0) {
            Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "更新失敗", Toast.LENGTH_SHORT).show();
        }
        db.close(); //有開就要關
        showAllData();
    }

    //刪除
    private void deleteData(){
        SQLiteDatabase db = helper.getReadableDatabase();
        int line = db.delete("classA","_id=?",new String[]{fid});

        if(line > 0){
            Toast.makeText(MainActivity.this, "刪除成功", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(MainActivity.this, "刪除失敗", Toast.LENGTH_SHORT).show();
        }
        db.close();  //有開就要關
        showAllData();
    }

    //按鍵監聽(第二排)
    public void check(View view) {
        switch(view.getId()) {
            case R.id.first:
                //第一筆資料
                if (cursor.moveToFirst()) {
                    display();
                }
                break;
            case R.id.prev:
                //上一筆資料
                if(cursor.moveToPrevious()){
                    display();
                }
                break;
            case R.id.next:
                //下一筆資料
                if(cursor.moveToNext()){
                    display();
                }
                break;
            case R.id.last:
                //最後一筆資料
                if(cursor.moveToLast()){
                    display();
                }
                break;
        }        
    }

    //顯示指定的內容
    private void display() {
        String sId = cursor.getString(cursor.getColumnIndex("_id"));
        String sName = cursor.getString(cursor.getColumnIndex("name"));
        String sSex = cursor.getString(cursor.getColumnIndex("sex"));
        String sScore = cursor.getString(cursor.getColumnIndex("score"));
        //要送入 id 條件 否則無法修改喔!!
        fid = sId;
        findData(fid);

        edId.setText(sId);
        edName.setText(sName);
        edSex.setText(sSex);
        edScore.setText(sScore);
    }
}