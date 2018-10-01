package upump.info.questionnaireref.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import upump.info.questionnaireref.R;

/**
 * Created by explo on 26.09.2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "questionnaire.db";
    public static final String TABLE_QUESTION = "QUESTION";
    public static final String TABLE_ANSWER = "ANSWER";
    private static String DB_PATH;
    private Context context;

    public static final String TABLE_KEY_ID = "_id";
    public static final String TABLE_KEY_BODY = "body";
    public static final String TABLE_KEY_IMG = "img";
    public static final String TABLE_KEY_CATEGORY = "category";
    public static final String TABLE_KEY_COMMENT = "comment";
    public static final String TABLE_KEY_RIGHT = "right";
    public static final String TABLE_KEY_ID_QUESTION = "id_question";

    private static DataBaseHelper instance;
    private static final String CREATE_QUESTION_TABLE =
            "CREATE TABLE " + TABLE_QUESTION +
                    "(" +
                    TABLE_KEY_ID + " integer NOT NULL primary key autoincrement, " +
                    TABLE_KEY_BODY + " text, " +
                    TABLE_KEY_CATEGORY + " text, " +
                    TABLE_KEY_IMG + " text, " +
                    TABLE_KEY_COMMENT + " text)";


    private static final String CREATE_ANSWER_TABLE =
            "CREATE TABLE " + TABLE_ANSWER +
                    "(" +
                    TABLE_KEY_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    TABLE_KEY_BODY + " text, " +
                    TABLE_KEY_RIGHT + " INTEGER, " +
                    TABLE_KEY_ID_QUESTION + " INTEGER , " +
                    "FOREIGN KEY(" + TABLE_KEY_ID_QUESTION + ") REFERENCES " + TABLE_QUESTION + "(_id))";


    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        //TODO здесь адрес базы нужно изменить
        DB_PATH = context.getString(R.string.data_base_path) + DATABASE_NAME;
    }

    public static synchronized DataBaseHelper getHelper(Context context) {
        if (instance == null) {
            instance = new DataBaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    public void create_db() {
        InputStream myInput = null;
        OutputStream myOutput = null;

        if (checkBD()) {
            return;
        }

        try {
            File file = new File(DB_PATH);
            if (!file.exists()) {
                //получаем локальную бд как поток в папке assets
                System.out.println("сменить базу");
                this.getReadableDatabase();

                myInput = context.getAssets().open(DATABASE_NAME);

                // Путь к новой бд
                String outFileName = DB_PATH;
                // Открываем пустую бд
                myOutput = new FileOutputStream(outFileName);

                // побайтово копируем данные
                byte[] buffer = new byte[1024];
                int length;

                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
                myOutput.close();
                myInput.close();
                close();

            }
            seVersionDB();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void deleteBD(){
        System.out.println("удалем старую");
        File file = new File(DB_PATH);
        if(file.exists()){
            file.delete();
        }
    }

    private void seVersionDB() {
        SQLiteDatabase sqLiteDatabase;
        try {
            sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.setVersion(DATABASE_VERSION);
            sqLiteDatabase.close();
        }catch (SQLiteException e) {

        }
    }

    private boolean checkBD() {
        SQLiteDatabase sqLiteDatabase;
        try {
            sqLiteDatabase = SQLiteDatabase.openDatabase(DB_PATH,null,  SQLiteDatabase.OPEN_READONLY);
            int version = sqLiteDatabase.getVersion();
            System.out.println("версия базы "+version);
            sqLiteDatabase.close();
            if(version <DATABASE_VERSION){
                deleteBD();
                return false;
            } else return true;
        } catch (SQLiteException e) {
            System.out.println(e);
            return false;
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
     /*   db.execSQL(CREATE_QUESTION_TABLE);
        db.execSQL(CREATE_ANSWER_TABLE);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
      /*  sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);

        onCreate(sqLiteDatabase);*/
    }
}
