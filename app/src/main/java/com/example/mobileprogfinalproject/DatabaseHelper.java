package com.example.mobileprogfinalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.IOException;


public class DatabaseHelper extends SQLiteOpenHelper {

    private ContentValues contentValues;
    private Cursor res;
    private SQLiteDatabase db;

    private static final String DATABASE_NAME = "Accounts.db";
    private static final String TABLE_NAME = "Accounts_table";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "USERNAME";
    private static final String COL_3 = "PASSWORD";
    private static final String COL_4 = "EMAIL";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (ID integer primary key autoincrement,USERNAME TEXT not null, PASSWORD text, EMAIL text not null)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private static final String SQL_SELECT_TABLE = "SELECT * FROM " + TABLE_NAME;

    private Accounts account;




    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(SQL_CREATE_ENTRIES);
        }catch(Exception e){
            try{
                throw new IOException(e);
            }catch(IOException e1){
                e1.printStackTrace();
            }
        }
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public boolean createAccount(Accounts account){
        try{
            db = this.getWritableDatabase();
            contentValues = new ContentValues();
            contentValues.put(COL_2,account.username);
            contentValues.put(COL_3,account.password);
            contentValues.put(COL_4,account.email);

            long result = db.insert(TABLE_NAME,null,contentValues);
            if(result == -1){
                return false;
            }else{
                db.close();
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }


    }

    public Cursor getData(){
        db = this.getWritableDatabase();
        res = db.rawQuery(SQL_SELECT_TABLE,null);
        return res;
    }

    public Integer deleteData(String username){
        db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,"USERNAME = ?", new String[] {username});
    }

    public boolean checkEmail(String email){
        db = this.getWritableDatabase();
        res = db.rawQuery("select * from Accounts_table where email=?", new String[]{email});
        if(res.getCount() > 0){
            return false;
        }else{
            return true;
        }
    }

    public boolean checkUsername(String username){
        db = this.getWritableDatabase();
        res = db.rawQuery("select * from Accounts_table where username=?",new String[]{username});
        if(res.getCount() > 0){
            return false;
        }else{
            return true;
        }
    }

    public Accounts validateLogin(Accounts account){
        db = this.getWritableDatabase();
        res = db.rawQuery(SQL_SELECT_TABLE +" where username = ? and password = ?", new String[] {account.username, account.password});

        if(res != null && res.moveToFirst() && res.getCount() > 0){
            Accounts newAccount = new Accounts(res.getString(0),res.getString(1),res.getString(2),res.getString(3));

            if(account.password.equals(newAccount.password)){
                return newAccount;
            }
        }
        return null;
    }
}

