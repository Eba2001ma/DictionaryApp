package com.example.elelleedictionary;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private Context mContext;
    public static final String DATABASE_NAME = "elellee_dic.db";
    public static final int DATABASE_VERSION = 1;

    private final String TBL_ENG_ORO = "english";
    private final String TBL_ORO_ENG = "oromo";
    private final String TBL_BOOKMARK = "bookmark";
    private final String COL_KEY = "key";
    private final String COL_VALUE = "value";


    private String DATABASE_LOCATION = "";
    public String DATABASE_FULL_PATH = "";
    public SQLiteDatabase mDB;


    public DBHelper(Context context){
 super(context,DATABASE_NAME,null,DATABASE_VERSION);
    mContext=context;

    DATABASE_LOCATION = "data/data/" +mContext.getPackageName() +"/database/";
    DATABASE_FULL_PATH = DATABASE_LOCATION + DATABASE_NAME;

        if (!isExistingDB()){
        try {
            // create directory before copy database
            File dbLocation = new File(DATABASE_LOCATION);
            dbLocation.mkdirs();

            extractAssetToDatabaseDirectory(DATABASE_NAME);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    mDB = SQLiteDatabase.openOrCreateDatabase(DATABASE_FULL_PATH,null);
        //mDB = SQLiteDatabase.openDatabase(DATABASE_FULL_PATH, null, SQLiteDatabase.OPEN_READWRITE);

}
    boolean isExistingDB(){
        File file = new File(DATABASE_FULL_PATH);
        return file.exists();
    }

    public void extractAssetToDatabaseDirectory(String fileName)
            throws IOException {
        // this used for copy database from asset folder
        int length;
        InputStream sourceDatabase = this.mContext.getAssets().open(fileName);
        File destinationPath = new File(DATABASE_FULL_PATH);
        OutputStream destination = new FileOutputStream(destinationPath);

        byte[] buffer = new byte[4096];
        while ((length = sourceDatabase.read(buffer)) > 0){
            destination.write(buffer, 0, length);
        }
        sourceDatabase.close();
        destination.flush();
        destination.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /*
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Refresh the existing table
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS eng_oro;");
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS eng_oro ('key' VARCHAR, value TEXT);");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS oro_eng;");
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS oro_eng ('key' VARCHAR, value TEXT);");
        }
    }
*/
    public ArrayList<String> getWord(int dicType){
        String tableName = getTableName(dicType);
        String q = "SELECT * FROM " + tableName;
        Cursor result = mDB.rawQuery(q,null);

        ArrayList<String> source = new ArrayList<>();
        while (result.moveToNext()){
            source.add(result.getString(0));
        }
        result.close();

        return source;
    }

    public Word getWord(String key, int dicType){
        String tableName = getTableName(dicType);
        String q = "SELECT * FROM " + tableName + " WHERE upper([key]) = upper(?)";
        Cursor result = mDB.rawQuery(q, new String[]{key});

        Word word = new Word();
        while (result.moveToNext()){
            word.key = result.getString(0);
            word.value = result.getString(1);
        }
        result.close();
        return word;
    }

    public void addBookmark(Word word){
        try {
            String q = "INSERT INTO bookmark([" + COL_KEY + "], [" + COL_VALUE + "]) values (?, ?);";
            mDB.execSQL(q, new Object[]{word.key, word.value});

        }catch (SQLException ex){

        }
    }
    public void removeBookmark(Word word){
        try {
            String q = "DELETE FROM bookmark WHERE upper(["+ COL_KEY +"]) = upper(?) AND ["+ COL_VALUE +"] = ?;";
            mDB.execSQL(q, new Object[]{word.key, word.value});

        }catch (SQLException ex){

        }
    }
    public ArrayList<String> getAllWordFromBookmark(){
        String q = "SELECT * FROM bookmark ORDER BY [date] DESC;";
        Cursor result = mDB.rawQuery(q, null);

        ArrayList<String> source = new ArrayList<>();
        while (result.moveToNext()){
            source.add(result.getString(0));

        }
        result.close();
        return source;
    }
    public boolean isWordMark(Word word){
        String q = "SELECT * FROM bookmark WHERE upper([key]) = upper(?)";
        Cursor result =  mDB.rawQuery(q, new String[]{word.key});
        result.close();
        return result.getCount()>0;
    }
    public Word getWordFromBookmark(String key){
        String q = "SELECT * FROM bookmark WHERE upper([key]) = upper(?)";
        //Cursor result =  mDB.rawQuery(q,null);
        Cursor result =  mDB.rawQuery(q, new String[]{key});

        Word word = null;
        while (result.moveToNext()){
            word = new Word();
            word.key = result.getString(0);
            word.value = result.getString(1);
        }
        result.close();
        return word;

    }

    public String getTableName(int dicType){
        String tableName = "";
        if (dicType == R.id.action_eng_oro){
            tableName = TBL_ENG_ORO;
        } else if (dicType == R.id.action_oro_eng) {
            tableName = TBL_ORO_ENG;
        }
        return tableName;
    }
}
