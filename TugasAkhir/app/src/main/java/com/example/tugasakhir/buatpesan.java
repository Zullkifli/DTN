package  com.example.tugasakhir;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class buatpesan extends AppCompatActivity {
    String desc="";
    String id_pengguna="";
    Cursor model=null;
    ListTracking.drvAdapter adapter=null;
    dbtracking helper=null;
    dbtemp helper2=null;
    EditText txtdesc,send_data;
    TextView view_data;
    @Override
    public void onDestroy(){
        super.onDestroy();
        helper.close();
        helper2.close();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buatpesan);
        helper=new dbtracking(this);
        helper2=new dbtemp(this);
        Intent oi=getIntent();
        id_pengguna=oi.getStringExtra("id_pengguna");
        txtdesc=(EditText)findViewById(R.id.txtdesc);

        load();


        Button btnsend=(Button)findViewById(R.id.btnsend);
        btnsend.setOnClickListener(view -> {
            InsertMessage();
            });

        send_data = (EditText) findViewById(R.id.editText);
        view_data = (TextView) findViewById(R.id.textView);



    }

    public  void InsertMessage(){

        DateFormat dform = new SimpleDateFormat("dd MMMM yyyy#HH:mm:ss");
        Date obj = new Date();
        String tgl= dform.format(obj);


        String waktu = tgl;
        String pesan= send_data.getText().toString();
        String status ="Proses";
        String keterangan =gab;


        helper2.inserttemp(waktu, pesan,status,id_pengguna,keterangan);
        Toast.makeText(this, "Pesan Berhasil Disimpan, Pesan Akan  dikirim Ketika koneksi tersedia..",Toast.LENGTH_LONG).show();
        //SendMessage();

    }



String gab="";
    private void load(){
        Cursor c=helper.getAll();
         int row = c.getCount();
         c.moveToFirst();
         for (int i=0;i<row;i++) {
           gab+=helper.getwaktu(c)+"%"+helper.getlatitude(c)+"%"+helper.getlongitude(c)+"%"+helper.getketerangan(c)+"%"+helper.gettag(c)+"@";
           c.moveToNext();
       }
       txtdesc.setText(gab);
        c.close();
     //   send_data.setText(waktu+"#"+tag+"#"+latitude+"#"+longitude+"#"+keterangan);
    }

}