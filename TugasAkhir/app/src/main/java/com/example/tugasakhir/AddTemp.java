package com.example.tugasakhir;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddTemp extends Activity{
	String _id=null;
	EditText pesan=null;
	dbtemp helper=null;
		
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addtemp);
		helper=new dbtemp(this);
		pesan=(EditText)findViewById(R.id.txtpesan);

		Button save=(Button)findViewById(R.id.btnproses);
		save.setOnClickListener(onSave);
		
		Button delete=(Button)findViewById(R.id.btnhapus);
		delete.setOnClickListener(onDelete);
		

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
    

	
	private View.OnClickListener onSave=new View.OnClickListener() {
		public void onClick(View v) {

			DateFormat dform = new SimpleDateFormat("dd MMM yyyy#HH:mm:ss");
			Date obj = new Date();
			String tgl= dform.format(obj);


			String cwaktu = tgl;
    		String cpesan= pesan.getText().toString();
			String cstatus ="Proses";
			String cketerangan ="-";
    		String cid_pengguna= _id;
    			if (cpesan.trim().length() ==0){
    				setAlert();
    			} else{
		    		helper.inserttemp(cwaktu, pesan.getText().toString(),cstatus,cid_pengguna,cketerangan);
		    		finish();
				}
		}
	};
	private View.OnClickListener onDelete=new View.OnClickListener() {
		public void onClick(View v) {			
				helper.deletetemp(_id);
				finish();
		}
	};
	
	private void setAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
        builder.setMessage("waktu, pesan, status dan id_pengguna harap diisi")
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
