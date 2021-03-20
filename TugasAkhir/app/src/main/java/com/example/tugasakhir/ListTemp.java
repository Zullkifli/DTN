package com.example.tugasakhir;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ListTemp extends ListActivity {
	public final static String ID_EXTRA="OK";
	Cursor model=null;
	drvAdapter adapter=null;
	dbtemp helper=null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewlist);
        helper=new dbtemp(this);
       // cekDatabase();
        
        model=helper.getAll();        
        startManagingCursor(model);
        adapter=new drvAdapter(model);
        setListAdapter(adapter);
    }
	@Override
	public void onDestroy(){
		super.onDestroy();
		helper.close();
	}
	@Override
	public void onListItemClick(ListView list, View view, int position, long id){
		Intent i=new Intent(ListTemp.this, DetailTemp.class);
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
		    		startActivity(new Intent (this, DetailTemp.class));
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
	}
	

	class drvAdapter extends CursorAdapter{
		drvAdapter (Cursor c){super(ListTemp.this, c);}
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
		void populateFrom(Cursor c, dbtemp helper){
			txtnamalkp.setText(helper.getwaktu(c));
			txtdeskripsilkp.setText(helper.getpesan(c)+","+helper.getstatus(c));
			icon.setImageResource(R.drawable.history);
		}
	}	
	
	
	public void cekDatabase(){
		helper=new dbtemp(this);        
		Cursor c=helper.getAll();
		c.moveToFirst();
		int row = c.getCount();
		if (row==0){
			//helper.inserttemp(waktu.getText().toString(), tag.getText().toString(),pesan.getText().toString(),status.getText().toString(),keterangan.getText().toString());				
		//	helper.inserttemp("20 Desember 2020 13:00", "Butuh Bantuan", "Proses","USR01","-");
		}
	}
	
}


