package com.vitalgroup;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper{
	
	// database version
	private static final int DATABASE_VERSION = 3;
	
	// database name
	private static final String DATABASE_NAME = "vital";
	
	// forms table name
	private static final String TABLE_FORMS = "form";
	
	// forms table column name
	private static final String FORM_ID    = "form_id";
	private static final String STORE_NAME = "store_name";
	private static final String BRAND_NAME = "brand_name";
	private static final String CITY	   = "city";
	private static final String NOTES      = "notes";
	private static final String IMAGE_DATA = "image_data";
	private static final String LATITUDE   = "latitude";
	private static final String LONGITUDE  = "longitude";
	private static final String USER_ID    = "user_id";
	
	public DatabaseHandler(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		
		// create tables
		String createFormTable = "CREATE TABLE " + TABLE_FORMS + "(" +
				FORM_ID + " INTEGER PRIMARY KEY," +
				STORE_NAME + " CHAR(255), " +
				BRAND_NAME + " CHAR(255), " +
				CITY + " CHAR(255), " +
				NOTES + " TEXT, " +
				IMAGE_DATA + " BLOB NOT NULL, " +
				LATITUDE + " CHAR(255), " + 
				LONGITUDE + " CHAR(255), " +
				USER_ID + " INT )";
		db.execSQL(createFormTable);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		
		// drop older table if exist
		if(oldVersion != newVersion)
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORMS);
		
		// create table again
		onCreate(db);
	}
	
	// adding new form
	public void addNewForm(FormRecords form){
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(STORE_NAME, form.getStoreName());
		values.put(BRAND_NAME, form.getBrandName());
		values.put(CITY, form.getCity());
		values.put(NOTES, form.getNotes());
		values.put(IMAGE_DATA, form.getImageData());
		values.put(LATITUDE, form.getLatitude());
		values.put(LONGITUDE, form.getLongitude());
		values.put(USER_ID, form.getUserId());
		
		// inserting row
		db.insert(TABLE_FORMS, null, values);
		
		// close database  connection
		db.close();
	}
	
	// getting single form
	public FormRecords getForm(String id){
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		String[] columns = new String[]{FORM_ID, STORE_NAME, BRAND_NAME, CITY, NOTES, IMAGE_DATA, LATITUDE, LONGITUDE, USER_ID};
		String[] whereArgs = new String[]{id};
		Cursor cursor = db.query(TABLE_FORMS, columns, FORM_ID + " = ?", whereArgs, null, null, null, null);
		
		if(cursor != null){
			cursor.moveToFirst();
		}
		
		FormRecords form = new FormRecords();
		form.setFormId(cursor.getString(0));
		form.setStoreName(cursor.getString(1));
		form.setBrandName(cursor.getString(2));
		form.setCity(cursor.getString(3));
		form.setNotes(cursor.getString(4));
		form.setImageData(cursor.getString(5));
		form.setLatitude(cursor.getString(6));
		form.setLongitude(cursor.getString(7));
		form.setUserId(cursor.getString(8));
		
		// return form
		return form;
	}
	
	// getting all forms
	public List<FormRecords> getAllForms(){
		
		List<FormRecords> forms = new ArrayList<FormRecords>();
		
		// select all query
		String selectQuery = "SELECT * FROM " + TABLE_FORMS;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		// looping through all rows and adding to forms list
		if(cursor.moveToFirst()){
			do{
				FormRecords form = new FormRecords();
				form.setFormId(cursor.getString(cursor.getColumnIndex(FORM_ID)));
				form.setStoreName(cursor.getString(cursor.getColumnIndex(STORE_NAME)));
				form.setBrandName(cursor.getString(cursor.getColumnIndex(BRAND_NAME)));
				form.setCity(cursor.getString(cursor.getColumnIndex(CITY)));
				form.setNotes(cursor.getString(cursor.getColumnIndex(NOTES)));
				form.setImageData(cursor.getString(cursor.getColumnIndex(IMAGE_DATA)));
				form.setLatitude(cursor.getString(cursor.getColumnIndex(LATITUDE)));
				form.setLongitude(cursor.getString(cursor.getColumnIndex(LONGITUDE)));
				form.setUserId(cursor.getString(cursor.getColumnIndex(USER_ID)));
				
				// adding form to forms list
				forms.add(form);
			}while(cursor.moveToNext());
		}
		
		// close connection
		cursor.close();
		
		// return forms list
		return forms;
	}
	
	// updating single form
	public int updateForm(FormRecords form){
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(STORE_NAME, form.getStoreName());
		values.put(BRAND_NAME, form.getBrandName());
		values.put(CITY, form.getCity());
		values.put(NOTES, form.getNotes());
		values.put(IMAGE_DATA, form.getImageData());
		values.put(LATITUDE, form.getLatitude());
		values.put(LONGITUDE, form.getLongitude());
		values.put(USER_ID, form.getUserId());
		
		String[] whereArgs = new String[]{form.getFormId()};
		// updating row
		return db.update(TABLE_FORMS, values, FORM_ID + " = ?", whereArgs);
	}
	
	// delete single form
	public void deleteForm(String form_id){
		
		SQLiteDatabase db = this.getWritableDatabase();
		String[] whereArgs = new String[]{form_id};
		db.delete(TABLE_FORMS, FORM_ID + " = ?", whereArgs);
		db.close();
	}
	
	// getting forms count
	public int getFormsCount(){
		
		String countQuery = "SELECT * FROM " + TABLE_FORMS;
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		
		// return count
		int total_forms = cursor.getCount();
		cursor.close();
		
		return total_forms;
	}
}
