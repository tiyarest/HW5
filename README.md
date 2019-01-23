# HW5  

*第五次作业*

*作业一：一个简单的TO-DO list app普通版+进阶*
数据库设计：  

V1：  

Id（INTEGER PRIMARY KEY AUTOINCREMENT）  content(text)   status(INTEGER)    date(long)

V2：  

Id（INTEGER PRIMARY KEY AUTOINCREMENT）  content(text)   status(INTEGER)    date(long)  
*priority(text)*

数据库实现：  

TodoContract：存放其数据库表的属性名称及其SQL语句。

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
    
TodoDbHelper：定义数据库名、版本；创建数据库。其中我们要实现数据库的向上升级，给表增加priority属性

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

MainActivity:
查询数据库，将每一个元祖对应到一个note里面，得到一个list，根据list更新ui。更新原则是，



