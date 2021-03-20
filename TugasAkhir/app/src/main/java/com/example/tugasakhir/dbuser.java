package com.example.tugasakhir;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbuser extends SQLiteOpenHelper {
	private static final String DATABASE_NAME="dbuserz.db";
	private static final int SCHEMA_VERSION=1;	
	public dbuser(Context context){super(context, DATABASE_NAME, null, SCHEMA_VERSION);}
	

//----------------------------------------------------------------------------------------------------------------------COPY	
	private static final String tb_user="tbbkz";
	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL("CREATE TABLE "+tb_user+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, nama_pengguna TEXT, email TEXT, telepon TEXT, username TEXT, password TEXT, status TEXT, id_pengguna TEXT)");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){}
	public Cursor getAll(){
		return (getReadableDatabase().rawQuery("SELECT _id, nama_pengguna, email, telepon, username, password, status, id_pengguna FROM "+tb_user+" order by nama_pengguna asc", null));
	}	
	public Cursor gCount(){
		return (getReadableDatabase().rawQuery("SELECT COUNT(*) as `row` FROM "+tb_user+"", null));		
	}
	public Cursor getBy_id(String id){
		String[] args={id};
		return (getReadableDatabase().rawQuery("SELECT _id, nama_pengguna, email, telepon, username,password,status, id_pengguna FROM "+tb_user+" WHERE _id=?", args));
	}

	public Cursor getLogin(String id){
		String[] args={id};
		return (getReadableDatabase().rawQuery("SELECT _id, nama_pengguna, email, telepon, username,password,status, id_pengguna FROM "+tb_user+" WHERE username=?", args));
	}
	//insertuser
	public void insertuser(String nama_pengguna, String email, String telepon, String username,  String password,  String status, String id_pengguna){
		ContentValues cv=new ContentValues();
		cv.put("nama_pengguna", nama_pengguna);
		cv.put("email", email);
		cv.put("telepon", telepon);
		cv.put("username", username);
		cv.put("password", password);
		cv.put("status", status);
		cv.put("id_pengguna", id_pengguna);
		getWritableDatabase().insert(tb_user,"nama_pengguna", cv);
	}
	public void updateuser (String id, String nama_pengguna, String email, String telepon, String username,String password,String status, String id_pengguna){
		ContentValues cv=new ContentValues();
		String[] args={id};
		cv.put("nama_pengguna", nama_pengguna);
		cv.put("email", email);
		cv.put("telepon", telepon);
		cv.put("username", username);
		cv.put("password", password);
		cv.put("status", status);
		cv.put("id_pengguna", id_pengguna);
		getWritableDatabase().update(tb_user,cv,"_id=?", args);
	}
	public void deleteuser (String id){
		String[] args={id};
		getWritableDatabase().delete(tb_user,"_id=?", args);
	}
	public String getid(Cursor c){return(c.getString(0));}
	public String getnama_pengguna(Cursor c){return(c.getString(1));}
	public String getemail(Cursor c){return(c.getString(2));}
	public String gettelepon(Cursor c){return(c.getString(3));}
	public String getusername(Cursor c){return(c.getString(4));}
	public String getpassword(Cursor c){return(c.getString(5));}
	public String getstautus(Cursor c){return(c.getString(6));}
	public String getid_pengguna(Cursor c){return(c.getString(7));}

	//public String getjum(Cursor c){return(c.getString(8));}
//-------------------------------------------------------------------------------------------------------	COPY
	
	
}
