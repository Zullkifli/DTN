package com.example.tugasakhir;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbtemp extends SQLiteOpenHelper {
	private static final String DATABASE_NAME="dbtempz.db";
	private static final int SCHEMA_VERSION=1;	
	public dbtemp(Context context){super(context, DATABASE_NAME, null, SCHEMA_VERSION);}
	

//----------------------------------------------------------------------------------------------------------------------COPY	
	private static final String tb_temp="tbtemp";
	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL("CREATE TABLE "+tb_temp+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, waktu TEXT, pesan TEXT, status TEXT, id_pengguna TEXT, keterangan TEXT)");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){}
	public Cursor getAll(){
		return (getReadableDatabase().rawQuery("SELECT _id, waktu, pesan, status, id_pengguna, keterangan FROM "+tb_temp+" order by waktu asc", null));
	}	
	public Cursor gCount(){
		return (getReadableDatabase().rawQuery("SELECT COUNT(*) as `row` FROM "+tb_temp+"", null));		
	}


	public Cursor gCount2(String idp,String st){
		String[] args={idp,st};
		return (getReadableDatabase().rawQuery("SELECT COUNT(*) as `row` FROM "+tb_temp+" WHERE id_pengguna=? and status=?", args));
	}
	public Cursor getBy_id(String id){
		String[] args={id};
		return (getReadableDatabase().rawQuery("SELECT _id, waktu, pesan, status, id_pengguna, keterangan FROM "+tb_temp+" WHERE _id=?", args));
	}
	//inserttemp
	public void inserttemp(String waktu, String pesan, String status, String id_pengguna, String keterangan){
		ContentValues cv=new ContentValues();
		cv.put("waktu", waktu);
		cv.put("pesan", pesan);
		cv.put("status", status);
		cv.put("id_pengguna", id_pengguna);
		cv.put("keterangan", keterangan);
		getWritableDatabase().insert(tb_temp,"waktu", cv);
	}
	public void updatetemp (String id, String waktu, String pesan, String status, String id_pengguna, String keterangan){
		ContentValues cv=new ContentValues();
		String[] args={id};
		cv.put("waktu", waktu);
		cv.put("pesan", pesan);
		cv.put("status", status);
		cv.put("id_pengguna", id_pengguna);
		cv.put("keterangan", keterangan);
		getWritableDatabase().update(tb_temp,cv,"_id=?", args);
	}
	public void deletetemp (String id){
		String[] args={id};
		getWritableDatabase().delete(tb_temp,"_id=?", args);
	}
	public String getwaktu(Cursor c){return(c.getString(1));}
	public String getpesan(Cursor c){return(c.getString(2));}
	public String getstatus(Cursor c){return(c.getString(3));}
	public String getid_pengguna(Cursor c){return(c.getString(4));}
	public String getketerangan(Cursor c){return(c.getString(5));}
//-------------------------------------------------------------------------------------------------------	COPY
	
	
}
