package com.camp.bit.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public class TodoDbHelper extends SQLiteOpenHelper {

    // TODO 定义数据库名、版本；创建数据库

    //数据库名称
    public  static  final  String DATABASE_NAME ="todo.db";

    //定义一个当前的版本号和一个我们要升级的版本号
    public  static  final  int FIRST_DATABASE_VERSION =1;
    public  static  final  int DATABASE_VERSION =2;

    public TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库,如果当前版本号不是最新版本，执行onUpgrade（）对数据库进行更新操作
        Log.d("SQL_CREATE_ENTRIES:",TodoContract.SQL_CREATE_ENTRIES);
        db.execSQL(TodoContract.SQL_CREATE_ENTRIES);
        onUpgrade(db, FIRST_DATABASE_VERSION, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //数据库更新
        Log.d("SQL_ALTER_PRIORITY:",TodoContract.SQL_ALTER_PRIORITY);
        for (int i=oldVersion;i<newVersion;i++){
            switch (i){
                case 1:db.execSQL(TodoContract.SQL_ALTER_PRIORITY);
                Log.d("alter:",TodoContract.SQL_ALTER_PRIORITY);
                break;
                default:break;
            }
        }

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
