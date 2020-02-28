package com.example.mobileprogfinalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import androidx.core.content.res.ComplexColorCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    private ContentValues contentValues;
    private Cursor res;
    private SQLiteDatabase db;
    private List<Passwords> passwordsList;

    private static final String PASSWORDS_TABLE = "Password_table";
    private static final String PASSWORDS_TITLE = "Title";
    private static final String PASSWORDS_ACCOUNT = "ACCOUNT";
    private static final String PASSWORDS_USERNAME = "USERNAME";
    private static final String PASSWORDS_PASSWORD = "PASSWORD";
    private static final String PASSWORDS_WEBSITE = "WEBSITE";
    private static final String PASSWORDS_NOTES = "NOTES";
    private static final String ACCOUNTS_ACCOUNT_ID = "ACCOUNT_ID";
    private static final String SQL_CREATE_PASSWORD_TABLE = "CREATE TABLE " + PASSWORDS_TABLE + " (PASSWORD_ID integer primary key autoincrement, TITLE TEXT, USERNAME TEXT not null," +
            " PASSWORD TEXT not null, WEBSITE TEXT not null, NOTES not null, ACCOUNT_ID integer, FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNTS_TABLE (ACCOUNT_ID))";



    private static final String DATABASE_NAME = "Accounts.db";
    private static final String ACCOUNTS_TABLE = "Accounts_table";
    private static final String ACCOUNTS_ID = "ACCOUNT_ID";
    private static final String ACCOUNTS_USERNAME = "ACCOUNT_USERNAME";
    private static final String ACCOUNTS_FULLNAME = "ACCOUNT_FULLNAME";
    private static final String ACCOUNTS_PASSWORD = "ACCOUNT_PASSWORD";
    private static final String ACCOUNTS_EMAIL = "ACCOUNT_EMAIL";
    private static final String SQL_CREATE_ACCOUNTS_TABLE = "CREATE TABLE " + ACCOUNTS_TABLE + " (ACCOUNT_ID integer primary key autoincrement,ACCOUNT_USERNAME TEXT not null, ACCOUNT_FULLNAME TEXT NOT NULL," +
            " ACCOUNT_PASSWORD text not null, ACCOUNT_EMAIL text not null)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ";
    private static final String SQL_SELECT_TABLE = "SELECT * FROM ";






    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(SQL_CREATE_ACCOUNTS_TABLE);
            db.execSQL(SQL_CREATE_PASSWORD_TABLE);
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
        db.execSQL(SQL_DELETE_ENTRIES + ACCOUNTS_TABLE);
        db.execSQL(SQL_DELETE_ENTRIES + PASSWORDS_TABLE);
        onCreate(db);
    }

    public List<Passwords> getPasswordAccounts(){
        passwordsList = new ArrayList<>();
        try{
            db = this.getWritableDatabase();
            res = db.rawQuery(SQL_SELECT_TABLE + PASSWORDS_TABLE, null);

            if(res.moveToFirst()){
                do{
                    Passwords passwords = new Passwords();
                    passwords.setID(res.getInt(0));
                    passwords.setTitle(res.getString(1));
                    passwords.setAccount(res.getString(2));
                    passwords.setUsername(res.getString(3));
                    passwords.setPassword(res.getString(4));
                    passwords.setWebsite(res.getString(5));
                    passwords.setNotes(res.getString(6));


                    passwordsList.add(passwords);
                }while(res.moveToNext());
            }
        }catch(Exception e){
            try{
                throw new IOException(e);
            }catch(IOException e1){
                e1.printStackTrace();
            }
        }

        return passwordsList;
    }

    public boolean createPasswordAccount(Accounts account, Passwords accountPassword){
        try{
            db = this.getWritableDatabase();
            contentValues = new ContentValues();
            contentValues.put(PASSWORDS_TITLE,accountPassword.getTitle());
            contentValues.put(PASSWORDS_USERNAME,accountPassword.getUsername());
            contentValues.put(PASSWORDS_PASSWORD,accountPassword.getPassword());
            contentValues.put(PASSWORDS_WEBSITE,accountPassword.getWebsite());
            contentValues.put(PASSWORDS_NOTES,accountPassword.getNotes());
            this.contentValues.put(ACCOUNTS_ACCOUNT_ID,account.getID());
            long result = db.insert(PASSWORDS_TABLE,null,contentValues);
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

    public boolean createAccount(Accounts account){
        try{
            db = this.getWritableDatabase();
            contentValues = new ContentValues();
            contentValues.put(ACCOUNTS_USERNAME,account.getUsername());
            contentValues.put(ACCOUNTS_FULLNAME,account.getFullname());
            contentValues.put(ACCOUNTS_PASSWORD,account.getPassword());
            contentValues.put(ACCOUNTS_EMAIL,account.getEmail());

            long result = db.insert(ACCOUNTS_TABLE,null,contentValues);
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

    public boolean checkEmail(String email){
        db = this.getWritableDatabase();
        res = db.rawQuery("select * from Accounts_table where account_email=?", new String[]{email});
        if(res.getCount() > 0){
            return false;
        }else{
            return true;
        }
    }

    public boolean checkUsername(String username){
        db = this.getWritableDatabase();
        res = db.rawQuery("select * from Accounts_table where account_username = ?",new String[]{username});
        if(res.getCount() > 0){
            return false;
        }else{
            return true;
        }
    }

    public Accounts validateLogin(Accounts account){
        db = this.getWritableDatabase();
        res = db.rawQuery(SQL_SELECT_TABLE +ACCOUNTS_TABLE+" where account_username = ? and account_password = ?", new String[] {account.getUsername(), account.getPassword()});

        if(res != null && res.moveToFirst() && res.getCount() > 0){
            Accounts newAccount = new Accounts(res.getInt(0),res.getString(1),res.getString(2),res.getString(3),res.getString(4));

            if(account.getPassword().equals(newAccount.getPassword())){
                return newAccount;
            }
        }
        return null;
    }


}

