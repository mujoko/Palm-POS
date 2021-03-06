package com.HarmanInternational.foodie.foodie;

/**
 * Created by Nithin on 6/14/2017.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by manojkulkarni on 6/13/17.
 */

public class DBAdapter extends ContentProvider {

    public static long rowID;
    public static final String PROVIDER_NAME="com.HarmanInternational.foodie.foodie.DBAdapter";

    static final String PROVIDER_URL = "content://" + PROVIDER_NAME + "/cpcontent";
    static final Uri CONTENT_URL = Uri.parse(PROVIDER_URL);

    public static final String PRIMARY_KEY="key";
    public static final String CREATE_PASSWORD="password1";
    public static final String CONFIRM_PASSWORD="password2";
    static final int uriCode = 1;

    private static HashMap<String, String> values;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "cpcontent", uriCode);
    }

    private SQLiteDatabase sqlDB;
    public static final String DATABASE_NAME="passwordcheck";
    public static final int DATABASE_VERSION=2;
    public static final String DATABASE_TABLE_NAME="passwordchecktable";


    public static final String CREATE_DATABASE="create table if not exists passwordchecktable (key integer primary key autoincrement, "
            +"password1 VARCHAR not null,password2 VARCHAR not null);";




    @Override
    public boolean onCreate() {


        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        sqlDB = dbHelper.getWritableDatabase();
        if (sqlDB != null) {
            return true;
        }
        return false;

    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DATABASE_TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case uriCode:

                queryBuilder.setProjectionMap(values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor cursor = queryBuilder.query(sqlDB, projection, selection, selectionArgs, null,
                null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {

            // vnd.android.cursor.dir/cpcontacts states that we expect multiple pieces of data
            case uriCode:
                return "vnd.android.cursor.dir/cpcontacts";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        rowID = sqlDB.insert(DATABASE_TABLE_NAME, null, values);

        if (rowID > 0) {

            Uri _uri = ContentUris.withAppendedId(CONTENT_URL, rowID);

            getContext().getContentResolver().notifyChange(_uri, null);

            return _uri;
        }
        Toast.makeText(getContext(), "Row Insert Failed", Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        sqlDB.execSQL("delete from "+DATABASE_TABLE_NAME);
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqlDB) {
            sqlDB.execSQL(CREATE_DATABASE);
        }

        // Recreates the table when the database needs to be upgraded
        @Override
        public void onUpgrade(SQLiteDatabase sqlDB, int oldVersion, int newVersion) {
            sqlDB.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME);
            onCreate(sqlDB);
        }
    }


}
