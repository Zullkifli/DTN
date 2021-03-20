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

public class DetailTemp extends Activity{
	String _id=null;
	EditText waktu=null;
	EditText pesan=null;
	EditText status=null;
	EditText id_pengguna=null;
	EditText keterangan=null;
	dbtemp helper=null;
		
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.temp);
		helper=new dbtemp(this);
		waktu=(EditText)findViewById(R.id.txtwaktu);
		pesan=(EditText)findViewById(R.id.txtpesan);
		status=(EditText)findViewById(R.id.txtstatus);
		id_pengguna=(EditText)findViewById(R.id.txtid_pengguna);
		keterangan=(EditText)findViewById(R.id.txtketerangan);
		
		Button save=(Button)findViewById(R.id.btnproses);
		save.setOnClickListener(onSave);
		
		Button delete=(Button)findViewById(R.id.btnhapus);
		delete.setOnClickListener(onDelete);
		
		_id=getIntent().getStringExtra(ListTemp.ID_EXTRA);
		if (_id!=null){load();}		
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
		pesan.setText(helper.getpesan(c));
		status.setText(helper.getstatus(c));
		id_pengguna.setText(helper.getid_pengguna(c));
		keterangan.setText(helper.getketerangan(c));
		c.close();
	}
	
	private View.OnClickListener onSave=new View.OnClickListener() {
		public void onClick(View v) {        
			String cekwaktu = waktu.getText().toString();
    		String cekpesan= pesan.getText().toString();
    		String cekstatus = status.getText().toString();
    		String cekid_pengguna= id_pengguna.getText().toString();
    			if (cekwaktu.trim().length() ==0||cekpesan.trim().length() ==0||cekstatus.trim().length() ==0||cekid_pengguna.trim().length() ==0){	
    				setAlert();
    			}	
    			else if (_id==null){
		    		helper.inserttemp(waktu.getText().toString(), pesan.getText().toString(),status.getText().toString(),id_pengguna.getText().toString(),keterangan.getText().toString());				
		    		finish();
				}
				else{
					helper.updatetemp(_id, waktu.getText().toString(), pesan.getText().toString(), status.getText().toString(),id_pengguna.getText().toString(),keterangan.getText().toString());
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
