package com.cdac.kp.androidchat.toolBox;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ANKUR on 20-07-2016.
 */
public class StorageManipulator extends SQLiteOpenHelper
{
    public static final String Database_name = "AndroidChat.db";
    public static final int Database_version = 1;

    public static final String _ID = "id";
    public static final String Table_name_message = "tabel_message";
    public static final String Message_Receiver = "receiver";
    public static final String Message_Sender = "sender";
    public static final String Message_Message = "message";

    private static final String TABLE_MESSAGE_CREATE = "CREATE TABLE"+Table_name_message+"("+_ID +"INTEGER PRIMARY KEY AUTOINCREMENT,"
            +Message_Receiver +"VARCHAR(25)," +Message_Sender +"VARCHAR(25)";

    public static final String Table_Message_Drop = "DROP TABLE IF EXITS"+ Table_name_message;

    public StorageManipulator(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }



    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(TABLE_MESSAGE_CREATE);
    }

    public void insert(String Sender, String Receiver, String Message)
    {
        long rowId;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Message_Receiver, Receiver);
            contentValues.put(Message_Sender, Sender);
            contentValues.put(Message_Message, Message);
            rowId = db.insert(Table_name_message, null, contentValues);
        }
        catch (Exception e)
        {

        }

    }

    public Cursor get(String sender, String Receiver)
    {
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = "SELECT * FROM" + Table_name_message + "WHERE" + Message_Sender + "LIKE" + sender + "AND" + Message_Receiver + "LIKE" + Receiver + "ORDER BY" + _ID + "ASC";

        return null;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(Table_Message_Drop);
    }
}
