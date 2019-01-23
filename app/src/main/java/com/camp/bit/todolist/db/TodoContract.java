package com.camp.bit.todolist.db;

import android.provider.BaseColumns;

import com.camp.bit.todolist.beans.Note;



/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量

    //创建表操作
    public  static final String SQL_CREATE_ENTRIES ="CREATE TABLE "+TodoEntry.TABLE_NAME+"("
            +TodoEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +TodoEntry.TABLE_CONTENT+" TEXT,"
            +TodoEntry.TABLE_STATE+" INTEGER,"+TodoEntry.TABLE_DATA+" LONG)";
    //删除表操作
    public static  final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXIST "+ TodoEntry.TABLE_NAME;

    //升级更改数据库表操作
    public static  final  String SQL_ALTER_PRIORITY ="ALTER TABLE "+TodoEntry.TABLE_NAME+" ADD "+TodoEntry.TABLE_PRIORITY+" TEXT";

    private TodoContract() {
    }
    public static class TodoEntry implements BaseColumns {
        //表名list，属性：date，state，content,priority
        public static final String TABLE_NAME = "list";
        public static final String TABLE_DATA= "date";
        public static final String TABLE_STATE = "state";
        public static final String TABLE_CONTENT= "content";
        public static final String TABLE_PRIORITY= "priority";
    }


}
