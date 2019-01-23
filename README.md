# HW5  

## 第五次作业 ##

## 作业一：一个简单的TO-DO list app普通版+进阶 ##
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

MainActivity:在onCreate（）里面实例化一个TodoDbHelper打开数据库，在onDestroy（）释放资源  

查询数据库，更新UI：将每一个元祖对应到一个note里面，得到一个list，根据list更新ui。 

        if(db==null){
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
                    TodoContract.TodoEntry.TABLE_PRIORITY+" DESC"  //根据优先级排序


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
根据查询结果更新ui  

   
    public void bind(final Note note) {
        contentText.setText(note.getContent());
        dateText.setText(SIMPLE_DATE_FORMAT.format(note.getDate()));
        
       //根据优先级修改权限
        if(note.getPriority().equals("1"))
            itemView.setBackgroundColor(0x70FAFAD2);
        else if(note.getPriority().equals("2"))
            itemView.setBackgroundColor(0x70FFD700);
        else
            itemView.setBackgroundColor(0x70EE6363);
        
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(note.getState() == State.DONE);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                note.setState(isChecked ? State.DONE : State.TODO);
                operator.updateNote(note);
                change(note);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operator.deleteNote(note);
            }
        });
        change(note);
    }


对数据库的更新，删除方式参照pdf。  


NoteActivity：在onCreate（）里面实例化一个TodoDbHelper打开数据库，在onDestroy（）释放资源  

增加布局：一个说明的textview  和一个下拉列表 Spinner
   
插入数据的方法如pdf。    

## 作业二：文件读写 ##  


                //如果有sd卡
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) try {
                    file = new File(Environment.getExternalStorageDirectory() + "/test.txt");
                    fos = new FileOutputStream(file);
                    byte[] strs = content.getBytes("UTF8");
                    fos.write(strs);
                    fos.close();
                    fis = new FileInputStream(file);
                    strs = new byte[fis.available()];
                    fis.read(strs);
                    fileText.setText(new String(strs,"UTF-8"));
                    fis.close();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                else {

                    file = new File(Environment.getDataDirectory() + "");
                    try {
                        fos = new FileOutputStream(file);
                        byte[] strs = content.getBytes("UTF8");
                        fos.write(strs);
                        fos.close();
                        fis = new FileInputStream(file);
                        strs = new byte[fis.available()];
                        fis.read(strs);
                        fileText.setText(new String(strs,"UTF-8"));
                        fis.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }







