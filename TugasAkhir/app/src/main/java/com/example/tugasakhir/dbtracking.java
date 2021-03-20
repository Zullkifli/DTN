package com.example.tugasakhir;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbtracking extends SQLiteOpenHelper {
	private static final String DATABASE_NAME="dbtracking.db";
	private static final int SCHEMA_VERSION=1;	
	public dbtracking(Context context){super(context, DATABASE_NAME, null, SCHEMA_VERSION);}
	

//----------------------------------------------------------------------------------------------------------------------COPY	
	private static final String tb_koordinat="tbbk";
	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL("CREATE TABLE "+tb_koordinat+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, waktu TEXT, tag TEXT, latitude TEXT, longitude TEXT, keterangan TEXT)");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){}
	public Cursor getAll(){
		return (getReadableDatabase().rawQuery("SELECT _id, waktu, tag, latitude, longitude, keterangan FROM "+tb_koordinat+" order by waktu asc", null));
	}	
	public Cursor gCount(){
		return (getReadableDatabase().rawQuery("SELECT COUNT(*) as `row` FROM "+tb_koordinat+"", null));		
	}
	public Cursor getBy_id(String id){
		String[] args={id};
		return (getReadableDatabase().rawQuery("SELECT _id, waktu, tag, latitude, longitude, keterangan FROM "+tb_koordinat+" WHERE _id=?", args));
	}
	//insertkoordinat
	public void insertkoordinat(String waktu, String tag, String latitude, String longitude, String keterangan){
		ContentValues cv=new ContentValues();
		cv.put("waktu", waktu);
		cv.put("tag", tag);
		cv.put("latitude", latitude);
		cv.put("longitude", longitude);
		cv.put("keterangan", keterangan);
		getWritableDatabase().insert(tb_koordinat,"waktu", cv);
	}
	public void updatekoordinat (String id, String waktu, String tag, String latitude, String longitude, String keterangan){
		ContentValues cv=new ContentValues();
		String[] args={id};
		cv.put("waktu", waktu);
		cv.put("tag", tag);
		cv.put("latitude", latitude);
		cv.put("longitude", longitude);
		cv.put("keterangan", keterangan);
		getWritableDatabase().update(tb_koordinat,cv,"_id=?", args);
	}
	public void deletekoordinat (String id){
		String[] args={id};
		getWritableDatabase().delete(tb_koordinat,"_id=?", args);
	}
	public String getwaktu(Cursor c){return(c.getString(1));}
	public String gettag(Cursor c){return(c.getString(2));}
	public String getlatitude(Cursor c){return(c.getString(3));}
	public String getlongitude(Cursor c){return(c.getString(4));}
	public String getketerangan(Cursor c){return(c.getString(5));}
//-------------------------------------------------------------------------------------------------------	COPY
	
	
}
