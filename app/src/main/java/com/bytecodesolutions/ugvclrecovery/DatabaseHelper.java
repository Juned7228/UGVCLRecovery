package com.bytecodesolutions.ugvclrecovery;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bytecodesolutions.ugvclrecovery.model.Consumer;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    Context context;

    private static final String DATABASE_NAME = "ugvcl.db";
    private static final int DATABASE_VERSION = 1;

    // Define your table schema and other necessary methods here

    // Constructor
    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create your table(s) here
        String createTableQuery = "CREATE TABLE " + ConsumerData.MyTable.TABLE_NAME + " (" +
                ConsumerData.MyTable.COLUMN_ID + " INTEGER PRIMARY KEY autoincrement," +
                ConsumerData.MyTable.COLUMN_CONNO + " TEXT," +
                ConsumerData.MyTable.COLUMN_NAME + " TEXT," +
                ConsumerData.MyTable.COLUMN_ADDRESS1 + " TEXT," +
                ConsumerData.MyTable.COLUMN_TARIF + " TEXT," +
                ConsumerData.MyTable.COLUMN_METERNO + " TEXT," +
                ConsumerData.MyTable.COLUMN_ROOTCODE + " TEXT," +
                ConsumerData.MyTable.COLUMN_AMOUNT + " NUMERIC," +
                ConsumerData.MyTable.COLUMN_MOBILE + " TEXT," +
                ConsumerData.MyTable.COLUMN_TYPE + " TEXT," +
                ConsumerData.MyTable.COLUMN_REMARKS + " TEXT" +
                ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade your database if needed
    }

    public void deleteAllDataFromTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ConsumerData.MyTable.TABLE_NAME, null, null);
        db.close();
    }
    public void insertData(Consumer consumer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ConsumerData.MyTable.COLUMN_CONNO, consumer.getNum());
        values.put(ConsumerData.MyTable.COLUMN_NAME, consumer.getName());
        values.put(ConsumerData.MyTable.COLUMN_ADDRESS1, consumer.getAddress1());
        values.put(ConsumerData.MyTable.COLUMN_TARIF, consumer.getTarif());
        values.put(ConsumerData.MyTable.COLUMN_METERNO, consumer.getMeterno());
        values.put(ConsumerData.MyTable.COLUMN_ROOTCODE, consumer.getRootcode());
        values.put(ConsumerData.MyTable.COLUMN_AMOUNT, consumer.getAmount());
        values.put(ConsumerData.MyTable.COLUMN_MOBILE, consumer.getMobile());
        values.put(ConsumerData.MyTable.COLUMN_TYPE, consumer.getType());


        long newRowId = db.insert(ConsumerData.MyTable.TABLE_NAME, null, values);

        if (newRowId == -1) {
            // Handle insertion failure
        } else {
            // Insertion was successful
        }

        db.close();
    }

    public List<Consumer> getAllItemsByFilter(String num,String name,String tarif,String meter,String amt_from,String amt_to,String type,String order) {
        List<Consumer> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql="SELECT *" +
                "  FROM "+ConsumerData.MyTable.TABLE_NAME+"" +
                "  Where" +
                "  "+ConsumerData.MyTable.COLUMN_CONNO+" like '"+num+"%'" +
                "  and" +
                "  "+ConsumerData.MyTable.COLUMN_NAME+" like '%"+name+"%'" +
                "  and" +
                "  "+ConsumerData.MyTable.COLUMN_TARIF+" like '%"+tarif+"%'" +
                "  and" +
                "  "+ConsumerData.MyTable.COLUMN_METERNO+" like '%"+meter+"%'" +
                "  and" +
                "  "+ConsumerData.MyTable.COLUMN_AMOUNT+" between "+amt_from+" and "+amt_to+
                "  and" +
                "  "+ConsumerData.MyTable.COLUMN_TYPE+" like '%"+type+"%'" +
                "  order by "+order.replace("Amount",ConsumerData.MyTable.COLUMN_AMOUNT).replace("Rootcode",ConsumerData.MyTable.COLUMN_ROOTCODE).replace("A->Z","asc").replace("Z->A","desc")+
                "  ;";
        System.out.println(sql);
        String query = "SELECT * FROM "+ConsumerData.MyTable.TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {

                @SuppressLint("Range") Consumer consumer=new Consumer(
                        cursor.getInt(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_CONNO)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_ADDRESS1)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_TARIF)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_METERNO)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_ROOTCODE)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_AMOUNT)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_MOBILE)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_TYPE)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_REMARKS))
                );

                itemList.add(consumer);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemList;
    }
    public String getAllItemsAbstractByFilter(String num,String name,String tarif,String meter,String amt_from,String amt_to,String type,String order) {
        List<Consumer> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String value="";
        String sql="SELECT count(*),sum("+ ConsumerData.MyTable.COLUMN_AMOUNT +")" +
                "  FROM "+ConsumerData.MyTable.TABLE_NAME+"" +
                "  Where" +
                "  "+ConsumerData.MyTable.COLUMN_CONNO+" like '"+num+"%'" +
                "  and" +
                "  "+ConsumerData.MyTable.COLUMN_NAME+" like '%"+name+"%'" +
                "  and" +
                "  "+ConsumerData.MyTable.COLUMN_TARIF+" like '%"+tarif+"%'" +
                "  and" +
                "  "+ConsumerData.MyTable.COLUMN_METERNO+" like '%"+meter+"%'" +
                "  and" +
                "  "+ConsumerData.MyTable.COLUMN_AMOUNT+" between "+amt_from+" and "+amt_to+
                "  and" +
                "  "+ConsumerData.MyTable.COLUMN_TYPE+" like '%"+type+"%'" +
                "  order by "+order.replace("Amount",ConsumerData.MyTable.COLUMN_AMOUNT).replace("Rootcode",ConsumerData.MyTable.COLUMN_ROOTCODE).replace("A->Z","asc").replace("Z->A","desc")+
                "  ;";
        System.out.println(sql);
        String query = "SELECT * FROM "+ConsumerData.MyTable.TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            value="Total :- Nos:- "+cursor.getInt(0)+" Amount:-Rs. "+cursor.getInt(1);

        }
        System.out.println(value);
        cursor.close();
        db.close();
        return value;
    }
    public List<Consumer> getAllItems() {
        List<Consumer> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM "+ConsumerData.MyTable.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                @SuppressLint("Range") Consumer consumer=new Consumer(
                        cursor.getInt(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_CONNO)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_ADDRESS1)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_TARIF)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_METERNO)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_ROOTCODE)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_AMOUNT)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_MOBILE)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_TYPE)),
                        cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_REMARKS))
                );

                itemList.add(consumer);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemList;
    }

    public Consumer getConsumerById(int id){

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM "+ConsumerData.MyTable.TABLE_NAME+" WHERE "+ConsumerData.MyTable.COLUMN_ID+"="+id+";";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        @SuppressLint("Range") Consumer consumer=new Consumer(
                cursor.getInt(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_CONNO)),
                cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_ADDRESS1)),
                cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_TARIF)),
                cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_METERNO)),
                cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_ROOTCODE)),
                cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_AMOUNT)),
                cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_MOBILE)),
                cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_TYPE)),
                cursor.getString(cursor.getColumnIndex(ConsumerData.MyTable.COLUMN_REMARKS))
        );
        cursor.close();
        db.close();
        return consumer;
    }

    public int updateRemarks(int id,String remarks){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(ConsumerData.MyTable.COLUMN_REMARKS,remarks);
        int rowsUpdated = db.update(ConsumerData.MyTable.TABLE_NAME, values, ""+ConsumerData.MyTable.COLUMN_ID+"=?", new String[]{"" + id});
        db.close();
        if (rowsUpdated > 0) {
            // Update was successful
            return rowsUpdated;
        } else {
            // Update failed
            return 0;
        }

    }

}

