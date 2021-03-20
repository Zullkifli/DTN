package com.example.tugasakhir;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailTracking extends Activity{
	String _id=null;
	EditText waktu=null;
	EditText tag=null;
	EditText latitude=null;
	EditText longitude=null;
	EditText keterangan=null;
	dbtracking helper=null;

	String myLati = "-6.353370";
	String myLongi = "106.832349";
	String myPosisi = "Default";
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tracking);
		helper=new dbtracking(this);

		Intent i=getIntent();
		myLati=i.getStringExtra("myLati");
		myLongi=i.getStringExtra("myLongi");
		myPosisi=i.getStringExtra("myPosisi");

		waktu=(EditText)findViewById(R.id.txtwaktu);
		tag=(EditText)findViewById(R.id.txttag);
		latitude=(EditText)findViewById(R.id.txtlatitude);
		longitude=(EditText)findViewById(R.id.txtlongitude);
		keterangan=(EditText)findViewById(R.id.txtketerangan);
		
		Button save=(Button)findViewById(R.id.btnproses);
		save.setOnClickListener(onSave);
		
		Button delete=(Button)findViewById(R.id.btnhapus);
		delete.setOnClickListener(onDelete);
		
		_id=getIntent().getStringExtra(ListTracking.ID_EXTRA);
		if (_id!=null){load();}else{

			DateFormat dform = new SimpleDateFormat("dd MMMM yyyy#HH:mm:ss");
			Date obj = new Date();
			String tgl= dform.format(obj);

			latitude.setText(myLati);
			longitude.setText(myLongi);
			waktu.setText(tgl);
		}
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		helper.close();
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.info:
        	info();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    
	private void load(){
		Cursor c=helper.getBy_id(_id);
		c.moveToFirst();
		waktu.setText(helper.getwaktu(c));
		tag.setText(helper.gettag(c));
		latitude.setText(helper.getlatitude(c));
		longitude.setText(helper.getlongitude(c));
		keterangan.setText(helper.getketerangan(c));
		c.close();
	}
	private View.OnClickListener onSave=new View.OnClickListener() {
		public void onClick(View v) {        
			String cekwaktu = waktu.getText().toString();
    		String cektag= tag.getText().toString();
    		String ceklatitude = latitude.getText().toString();
    		String ceklongitude= longitude.getText().toString();
    			if (cekwaktu.trim().length() ==0||cektag.trim().length() ==0||ceklatitude.trim().length() ==0||ceklongitude.trim().length() ==0){	
    				setAlert();
    			}	
    			else if (_id==null){
		    		helper.insertkoordinat(waktu.getText().toString(), tag.getText().toString(),latitude.getText().toString(),longitude.getText().toString(),keterangan.getText().toString());				
		    		finish();
				}
				else{
					helper.updatekoordinat(_id, waktu.getText().toString(), tag.getText().toString(), latitude.getText().toString(),longitude.getText().toString(),keterangan.getText().toString());
					finish();
				}
		}
	};
	
	private View.OnClickListener onDelete=new View.OnClickListener() {
		public void onClick(View v) {			
				helper.deletekoordinat(_id);
				finish();
		}
	};
	
	private void setAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
        builder.setMessage("waktu, tag, latitude dan longitude harap diisi")
        .setCancelable(false)
        .setNeutralButton("Ok",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	dialog.cancel();
            }
       });
        AlertDialog alert = builder.create();
        alert.show();
	}

	private void info(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
        builder.setMessage("Aplikasi dibuat by Me@2013")
        .setCancelable(false)
        .setNeutralButton("Semangat !",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	dialog.cancel();
            }
       });
        AlertDialog alert = builder.create();
        alert.show();
	}
	
}
