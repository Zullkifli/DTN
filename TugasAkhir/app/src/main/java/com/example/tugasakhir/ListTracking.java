package com.example.tugasakhir;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListTracking extends ListActivity {
	public final static String ID_EXTRA="OK";
	Cursor model=null;
	drvAdapter adapter=null;
	dbtracking helper=null;
	String myLati = "-6.353370";
	String myLongi = "106.832349";
	String myPosisi = "Default";
	@Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_listadd);
        helper=new dbtracking(this);
        cekDatabase();

        Intent i=getIntent();
		myLati=i.getStringExtra("myLati");
		myLongi=i.getStringExtra("myLongi");
		myPosisi=i.getStringExtra("myPosisi");

        model=helper.getAll();
        startManagingCursor(model);
        adapter=new drvAdapter(model);
        setListAdapter(adapter);

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), DetailTracking.class);
				i.putExtra("pk", "");
				i.putExtra("myLati", myLati);
				i.putExtra("myLongi", myLongi);
				i.putExtra("myPosisi", myPosisi);
				startActivityForResult(i, 100);
			}
		});
    }
	@Override
	public void onDestroy(){
		super.onDestroy();
		helper.close();
	}
	@Override
	public void onListItemClick(ListView list, View view, int position, long id){
		Intent i=new Intent(ListTracking.this, DetailTracking.class);
		i.putExtra(ID_EXTRA, String.valueOf(id));
		startActivity(i);
	}


	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
        case R.id.add:
        	Cursor c=helper.getAll();
		    c.moveToFirst();
		    int row = c.getCount();
		    	if (row>=10000){
		    		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
		            builder.setMessage("Data sudah banyak..silakan sering2 dibackup dahulu...")
		            .setCancelable(false)
		            .setNeutralButton("Ok",new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int id) {
		                	dialog.cancel();
		                }
		           });
		            AlertDialog alert = builder.create();
		            alert.show();
		    	}
		    	else
		    		startActivity(new Intent (this, DetailTracking.class));
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
	}
	

	class drvAdapter extends CursorAdapter{
		drvAdapter (Cursor c){super(ListTracking.this, c);}
		@Override
		public void bindView(View row, Context ctxt, Cursor c){
			drvHolder holder=(drvHolder)row.getTag();
			holder.populateFrom(c, helper);
		}
		@Override
		public View newView(Context ctxt, Cursor c, ViewGroup parent){
			LayoutInflater inflater=getLayoutInflater();
			View row=inflater.inflate(R.layout.desain_list, parent, false);
			drvHolder holder=new drvHolder(row);
			row.setTag(holder);
			return(row);
		}
	}
	static class drvHolder{
		private TextView txtnamalkp=null;
		private TextView txtdeskripsilkp=null;
		private TextView txtkode_k=null;
		private ImageView icon=null;
		private View row=null;
		
		drvHolder(View row){
			this.row=row;
			txtnamalkp=(TextView)row.findViewById(R.id.txtNamalkp);
			txtdeskripsilkp=(TextView)row.findViewById(R.id.txtDeskripsilkp);
			txtkode_k=(TextView)row.findViewById(R.id.kode_k);

			icon=(ImageView)row.findViewById(R.id.list_imagelkp);
		}
		void populateFrom(Cursor c, dbtracking helper){
			txtnamalkp.setText(helper.getketerangan(c));
			txtdeskripsilkp.setText(helper.getlatitude(c)+","+helper.getlongitude(c)+"\nWaktu : "+helper.getwaktu(c));
			icon.setImageResource(R.drawable.user);
		}
	}	
	
	
	public void cekDatabase(){
		helper=new dbtracking(this);        
		Cursor c=helper.getAll();
		c.moveToFirst();
		int row = c.getCount();
		if (row==0){
			//helper.insertkoordinat(waktu.getText().toString(), tag.getText().toString(),latitude.getText().toString(),longitude.getText().toString(),keterangan.getText().toString());				
		//	helper.insertkoordinat("20 Desembr 2020 13:00", "*", "-2.95869151","104.7698774","-");

		}
		}
	
}


