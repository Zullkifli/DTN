package com.example.tugasakhir;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class Login extends Activity {

		private static final String TAG = Login.class.getName();
	private static final String FILENAME = "myFiles.txt";

   dbuser helper=null;
	String mac_address="";
	SessionManager session;
	EditText txtusername,txtpassword;
	String ip="";
	int sukses;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();

	private static final String TAG_SUKSES = "sukses";
	private static final String TAG_record = "record";

	String id_pengguna="",nama_pengguna,status,username,password;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		ip=jsonParser.getIP();
		helper=new dbuser(this);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		session = new SessionManager(getApplicationContext());
		txtusername=(EditText)findViewById(R.id.txtusername);
		txtpassword=(EditText)findViewById(R.id.txtpassword);


		Button btnLogin= (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String pengguna=txtusername.getText().toString();
				String pass=txtpassword.getText().toString();
				if(pengguna.length()<1){lengkapi("username");}
				else if(pass.length()<1){lengkapi("Password");}
				else{
//					Intent i=new Intent(Login.this,Menu_utama.class);
//					startActivity(i);
					isConnectedToServer(ip,200);
				}

			}

		});

		Button btnRegistrasi= (Button) findViewById(R.id.btndaftar);
		btnRegistrasi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent i = new Intent(Login.this, Registrasi.class);
				i.putExtra("pk", "");
				startActivity(i);
			}

		});

//	Button btnLupa= (Button) findViewById(R.id.btnLupa);
//	btnLupa.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent i = new Intent(Login.this, Lupa_pass.class);
//				i.putExtra("pk", "");
//				startActivity(i);
//			}
//	});
	}

	void isConnectedToServer(String url, int timeout) {
		try{
			URL myUrl = new URL(url);
			URLConnection connection = myUrl.openConnection();
			connection.setConnectTimeout(timeout);
			connection.connect();
			new ceklogin().execute();
		} catch (Exception e) {
			nointernet();
		}
	}
	public void gagal(){
		new AlertDialog.Builder(this)
				.setTitle("Gagal Login")
				.setMessage("Silakan Cek Account Anda Kembali")
				.setNeutralButton("Tutup", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {

					}})
				.show();
	}


	class ceklogin extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Proses Login...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		protected String doInBackground(String... params) {

					username=txtusername.getText().toString().trim();
					password=txtpassword.getText().toString().trim();

				List<NameValuePair> myparams = new ArrayList<NameValuePair>();
				myparams.add(new BasicNameValuePair("username", username));
				myparams.add(new BasicNameValuePair("password", password));

				String url=ip+"pengguna/pengguna_login.php";
				Log.v("detail",url);

				try{
					JSONObject json = jsonParser.makeHttpRequest(url, "GET", myparams);
					Log.d("detail", json.toString());
					sukses = json.getInt(TAG_SUKSES);
					if (sukses == 1) {
					JSONArray myObj = json.getJSONArray(TAG_record); // JSON Array
					final JSONObject myJSON = myObj.getJSONObject(0);
					runOnUiThread(new Runnable() {
						public void run() {

							try {
								id_pengguna=myJSON.getString("id_pengguna");
								nama_pengguna=myJSON.getString("nama_pengguna");
								status=myJSON.getString("status");
								mac_address=myJSON.getString("mac_address");
							} catch (JSONException e) {
								e.printStackTrace();
							}
							}

						});}

				}catch (Exception e) {

				}



			return null;
		}
		@SuppressLint("NewApi")
		protected void onPostExecute(String file_url) {

			pDialog.dismiss();
			Log.v("SUKSES",id_pengguna);

			if(sukses==1){
				session.createLoginSession(id_pengguna,nama_pengguna);
				final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Login.this);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putBoolean("Registered", true);
				editor.putString("id_pengguna", id_pengguna);
				editor.putString("nama_pengguna", nama_pengguna);
				editor.putString("status", status);
				editor.putString("mac_address", mac_address);
				editor.apply();

				Intent i = new Intent(getApplicationContext(),MainActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
			}
			else{gagal("Login");	}
		}
	}




	public void lengkapi(String item){
		new AlertDialog.Builder(this)
				.setTitle("Lengkapi Data")
				.setMessage("Silakan lengkapi data "+item)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {
					}})
				.show();
	}



	public void gagal(String item){
		new AlertDialog.Builder(this)
				.setTitle("Gagal Login")
				.setMessage("Login "+item+" ,, Gagal")
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {
					}})
				.show();
	}

	public void nointernet(){
        int jum;
        username=txtusername.getText().toString();
      try {
		   Cursor c = helper.getLogin(username);
		   c.moveToFirst();
		   id_pengguna = helper.getid(c);
		   nama_pengguna = helper.getnama_pengguna(c);
		   status = helper.getstautus(c);
		   String username = helper.getusername(c);
		   c.close();
		  Toast.makeText(this,"Username"+username,Toast.LENGTH_LONG).show();
		   Log.v("SUKSES", id_pengguna);
		   Log.v("SUKSES", nama_pengguna);


		   session.createLoginSession(id_pengguna, nama_pengguna);
		   final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Login.this);
		   SharedPreferences.Editor editor = sharedPref.edit();
		   editor.putBoolean("Registered", true);
		   editor.putString("id_pengguna", id_pengguna);
		   editor.putString("nama_pengguna", nama_pengguna);
		   editor.putString("status", "");
		   editor.putString("mac_address", "");
		   editor.apply();

			Intent i = new Intent(getApplicationContext(), MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			finish();
			startActivity(i);
	   }catch (Exception ee){
      	Toast.makeText(this,"Gagal Login",Toast.LENGTH_LONG).show();
		}


	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
