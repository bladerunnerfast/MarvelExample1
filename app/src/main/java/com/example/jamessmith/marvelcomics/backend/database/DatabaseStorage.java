package com.example.jamessmith.marvelcomics.backend.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.jamessmith.marvelcomics.comics.ComicModel;
import com.example.jamessmith.marvelcomics.description.DescriptionModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by James on 10/08/2017.
 */

public class DatabaseStorage extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME  = "marvelDb";
    private static final String TABLENAME = "mavelCache";

    private static final String id = "id";
    private static final String title = "title";
    private static final String image = "image";
    private static final String description = "description";
    private static final String price = "price";
    private static final String thumbnail = "thumbnail";
    private static final String pageCount = "pageCount";
    private static final String author = "author";

    protected Cursor cursor;
    protected SQLiteDatabase database;
    private Context context;

    private static final String sql = "CREATE TABLE " + TABLENAME +"(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            title + " TEXT,"+ image + " TEXT," + description + " TEXT," + price + " FLOAT," + thumbnail + " TEXT," +
            pageCount + " INTEGER," + author + " TEXT)";

    public DatabaseStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void dropTable(){//For Testing purposes only.
        context.deleteDatabase(DATABASE_NAME);;
    }

    public boolean isTableExists() {
        boolean isExist = false;
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + TABLENAME + "'", null);

        if(database.isOpen()) {
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    isExist = true;
                }
                cursor.close();
            }
        }
        return isExist;
    }

    public int countRows() {

        String SQLQuery = "SELECT COUNT(" + id + ") FROM " + TABLENAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SQLQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    public long getLastModified(){
        File dbpath = context.getDatabasePath(DATABASE_NAME);
        return dbpath.lastModified();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.v(DatabaseStorage.class.getName(), sql);
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onCreate(sqLiteDatabase);
    }

    public boolean insertEntry(DatabaseModel databaseModel){

        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        final String selectSql = "SELECT * FROM " + TABLENAME + " WHERE " + title + "='"+ databaseModel.getTitle() + "'" +
                "AND " + description + "='" + databaseModel.getDescription() + "'";

        if(database.isOpen()){
            cursor = database.rawQuery(selectSql, null);
            if(cursor != null) {
                if (cursor.isLast()) {
                    cursor.moveToFirst();
                }

                if (database.isReadOnly()) {
                    database = this.getWritableDatabase();
                }

                contentValues.put(title, databaseModel.getTitle());
                contentValues.put(image, databaseModel.getImageURL());
                contentValues.put(description, databaseModel.getDescription());
                contentValues.put(price, databaseModel.getPrice());
                contentValues.put(thumbnail, databaseModel.getThumbnail());
                contentValues.put(pageCount, databaseModel.getPageCount());
                contentValues.put(author, databaseModel.getAuthor());
                long result = database.insert(TABLENAME, null, contentValues);

                cursor.close();
                database.close();
                contentValues.clear();

                if(result == -1) {
                    return false;
                }
                else {
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<ComicModel> getComicData() {
        ComicModel comicModel;
        ArrayList<ComicModel> parcelables = new ArrayList<>();

        database = this.getReadableDatabase();

        if (database.isOpen()) {

            final String getDataSql = "SELECT " + thumbnail + ", " + title + "," + price +  " FROM " + TABLENAME;
            cursor = database.rawQuery(getDataSql, null);

            if (cursor != null) {

                if (cursor.isLast()) {
                    cursor.moveToFirst();
                }

                if (!cursor.moveToFirst() || cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        comicModel = new ComicModel(cursor.getString(cursor.getColumnIndex(thumbnail)),
                                cursor.getString(cursor.getColumnIndex(title)),
                                cursor.getDouble(cursor.getColumnIndex(price)));
                        parcelables.add(comicModel);
                    }

                    cursor.close();
                    database.close();
                }
            }
        }
        return parcelables;
    }

    public DescriptionModel getDescriptionData(int indexValue) {
        DescriptionModel descriptionModel = null;

        database = this.getReadableDatabase();

        if (database.isOpen()) {

            final String getDataSql = "SELECT " + thumbnail + ", " + title + "," + description + "," + price + "," + pageCount +
                    "," + author + " FROM " + TABLENAME + " WHERE id='" + indexValue + "'";
            cursor = database.rawQuery(getDataSql, null);

            if (cursor != null) {

                if (cursor.isLast()) {
                    cursor.moveToFirst();
                }

                if (!cursor.moveToFirst() || cursor.getCount() > 0) {
                    descriptionModel = new DescriptionModel(cursor.getString(cursor.getColumnIndex(thumbnail)),
                            cursor.getString(cursor.getColumnIndex(title)),
                            cursor.getString(cursor.getColumnIndex(description)),
                            cursor.getDouble(cursor.getColumnIndex(price)),
                            cursor.getInt(cursor.getColumnIndex(pageCount)),
                            cursor.getString(cursor.getColumnIndex(author)));

                    cursor.close();
                    database.close();
                }
            }
        }
        return descriptionModel;
    }
}
