package com.example.tugasakhir;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tugasakhir.callbacks.SelectDeviceCallback;
import com.example.tugasakhir.entities.ChatMessage;
import com.example.tugasakhir.utilities.BluetoothChatService;
import com.example.tugasakhir.utilities.Constants;
import com.example.tugasakhir.utilities.DividerItemDecoration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SelectDeviceCallback {
    SessionManager session;
    String id_pengguna = "", nama_pengguna = "", mac_address="";
    String myLati = "-6.353370";
    String myLongi = "106.832349";
    String myPosisi = "Default";
    Button btnget;
    dbtemp helper=null;
    dbtracking helper2=null;

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    String ip="";
    private static final String TAG_SUKSES = "sukses";
    private static final String TAG_record = "record";


    private static final int MY_PERMISSION_REQUEST = 1;

    private int REQUEST_ENABLE_BLUETOOTH=100;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothChatService mChatService = null;
    private DiscoveredDeviceListAdapter deviceListAdapter,pairedListAdapter;
    private ArrayList<String> items = new ArrayList<>();
    private ArrayList<String> pairedItems = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    private List<ChatMessage> messages = new ArrayList<>();
    private EditText txtpesan;
    TextView tvConnectedName;

    String deviceName="";
    String deviceAddress="";
    private Dialog dialog;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkBluetoothSupport();
        helper=new dbtemp(this);
        helper2=new dbtracking(this);
        ip=jsonParser.getIP();

        deviceListAdapter = new DiscoveredDeviceListAdapter(MainActivity.this,items);
        pairedListAdapter = new DiscoveredDeviceListAdapter(MainActivity.this,pairedItems);
        ImageView ivBluetooth = (ImageView)findViewById(R.id.ivBluetooth);
        tvConnectedName = (TextView)findViewById(R.id.tvConnectedName);
        ivBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDevices();
            }
        });


        session = new SessionManager(getApplicationContext());
        session.checkLogin();


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Boolean Registered = sharedPref.getBoolean("Registered", false);
        if (!Registered) {
            finish();
        } else {
            id_pengguna = sharedPref.getString("id_pengguna", "");
            nama_pengguna = sharedPref.getString("nama_pengguna", "");
            mac_address = sharedPref.getString("mac_address", "");
        }

        Log.v("seskod", id_pengguna);
        Log.v("sesnam", nama_pengguna);
        Log.v("sesmac", mac_address);


        CardView profilCard = (CardView) findViewById(R.id.profilCard);
        profilCard.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, ListUser.class);
            i.putExtra("pk", id_pengguna);
            startActivity(i);
        });

        ImageView btnpair = (ImageView) findViewById(R.id.ivBluetooth2);
        btnpair.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, MenuBluetooth.class);
            i.putExtra("pk", id_pengguna);
            startActivity(i);
        });

        CardView trackingCard = (CardView) findViewById(R.id.trackingCard);
        trackingCard.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, ListTracking.class);
            i.putExtra("myLati", myLati);
            i.putExtra("myLongi", myLongi);
            i.putExtra("myPosisi", myPosisi);

            startActivity(i);
        });


        CardView pesanCard = (CardView) findViewById(R.id.pesanCard);
        pesanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, buatpesan.class);
                i.putExtra("id_pengguna", id_pengguna);
                i.putExtra("myLati", myLati);
                i.putExtra("myLongi", myLongi);
                i.putExtra("myPosisi", myPosisi);
                startActivity(i);
            }
        });

        CardView historiCard = (CardView) findViewById(R.id.historiCard);
        historiCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ListTemp.class);
                i.putExtra("id_pengguna", "");
                i.putExtra("myLati", myLati);
                i.putExtra("myLongi", myLongi);
                i.putExtra("myPosisi", myPosisi);

                startActivity(i);
            }
        });

        Button btnGet = (Button) findViewById(R.id.btngetMsg);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
    load();
            }
        });



//        CardView menu6Card = (CardView) findViewById(R.id.menu6Card);
//        menu6Card.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                session.logout();
//                finish();
//            }
//        });





        LocationManager locationManager;
        String context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(context);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);

        //meminta izin penyimpanan
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST);
            }
        } else {
            //DO NOTHING
        }


        Location location = locationManager.getLastKnownLocation(provider);
        updateWithNewLocation(location);

        locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider){
            updateWithNewLocation(null);
        }

        public void onProviderEnabled(String provider){ }
        public void onStatusChanged(String provider, int status,
                                    Bundle extras){ }
    };

    private void load(){
        Cursor c=helper.getAll();
        c.moveToLast();
        String waktu=helper.getwaktu(c);
        String pesan=helper.getpesan(c);
        String status=helper.getstatus(c);
        String id_pengguna=helper.getid_pengguna(c);
        String keterangan=helper.getketerangan(c);
        c.close();

        txtpesan.setText(waktu+"%"+pesan+"%"+status+"%"+id_pengguna+"##"+keterangan);
    }

    private void updateWithNewLocation(Location location) {
        double latitude=Double.parseDouble(myLati);
        double longitude=Double.parseDouble(myLongi);
        String addressString = "No address found";

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Geocoder gc = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);

                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                        sb.append(address.getAddressLine(i)).append("\n");

                    sb.append(address.getLocality()).append("\n");
                    sb.append(address.getPostalCode()).append("\n");
                    sb.append(address.getCountryName());
                }
                addressString = sb.toString();
            } catch (IOException e) {}
        } else {
            myLati="-6.353370";
            myLongi="106.832349";
            addressString="Lp2m Aray Jkt";
        }

        myPosisi=addressString;
        myLati=String.valueOf(latitude);
        myLongi=String.valueOf(longitude);


        TextView txtMarquee=(TextView)findViewById(R.id.txtMarquee);
        txtMarquee.setSelected(true);
        String kata="Posisi Anda :"+myLati+"/"+myLongi+" "+myPosisi+"#";
        String kalimat=String.format("%1$s", TextUtils.htmlEncode(kata));
        txtMarquee.setText(Html.fromHtml(kalimat+kalimat+kalimat));
    }


    public void keluar(){
        new AlertDialog.Builder(this)
                .setTitle("Menutup Aplikasi")
                .setMessage("Terimakasih... Anda Telah Menggunakan Aplikasi Ini")
                .setNeutralButton("Tutup", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {
                        finish();
                    }})
                .show();
    }
    public void keluarYN(){
        AlertDialog.Builder ad=new AlertDialog.Builder(MainActivity.this);
        ad.setTitle("Konfirmasi");
        ad.setMessage("Apakah benar ingin keluar?");

        ad.setPositiveButton("OK",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                session.logout();
            }});

        ad.setNegativeButton("No",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface arg0, int arg1) {
            }});

        ad.show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            keluarYN();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void enableBlluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }else if (mChatService == null) {
            setupChat();
        }
    }

    private void checkBluetoothSupport() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            enableBlluetooth();
        }
    }
    private void setupChat() {
        // Initialize the array adapter for the conversation thread
        messageAdapter = new MessageAdapter(MainActivity.this,messages);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        Button mSendButton = (Button)findViewById(R.id.btn_chat_send);
        txtpesan = (EditText)findViewById(R.id.txtpesan);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
        recyclerView.setAdapter(messageAdapter);

        // Initialize the compose field with a listener for the return key
        txtpesan.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                    String message = txtpesan.getText().toString();
                    sendMessage(message);

            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(MainActivity.this, mHandler);

    }
    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private EditText.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(MainActivity.this,"Device is not connected.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
            txtpesan.setText("");
        }
    }
    private void showDevices() {
        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.devices_list_layout);
        RecyclerView rlPairedDevices = (RecyclerView)dialog.findViewById(R.id.rlPairedDevices);
        RecyclerView rlDevices = (RecyclerView)dialog.findViewById(R.id.rlDevices);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rlPairedDevices.setLayoutManager(mLayoutManager);
        rlPairedDevices.addItemDecoration(new DividerItemDecoration(MainActivity.this));
        rlPairedDevices.setAdapter(pairedListAdapter);

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        rlDevices.setLayoutManager(mLayoutManager1);
        rlDevices.addItemDecoration(new DividerItemDecoration(MainActivity.this));
        rlDevices.setAdapter(deviceListAdapter);

        Button dialogButton = (Button) dialog.findViewById(R.id.btnClose);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void searchDevices() {
        items.clear();
        pairedItems.clear();
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryFinishReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryFinishReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedItems.add(device.getName() + "\n" + device.getAddress());
                pairedListAdapter.notifyDataSetChanged();
               // pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            pairedItems.clear();
            pairedItems.add("Nothing found");
            pairedListAdapter.notifyDataSetChanged();
           // pairedDevicesAdapter.add(getString(R.string.none_paired));
        }
        showDevices();
    }
    @Override
    public void onDeviceSelected(String name, String address) {
        deviceName=name;
        deviceAddress = address;
        // Get the BluetoothDevice object
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(MainActivity.this, mHandler);
        // Attempt to connect to the device
        mChatService.connect(device, false);
        dialog.dismiss();
    }


    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    items.add(device.getName() + "\n" + device.getAddress());
                    deviceListAdapter.notifyDataSetChanged();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (deviceListAdapter.getItemCount() == 0) {
                    items.clear();
                    items.add("Nothing found");
                    deviceListAdapter.notifyDataSetChanged();
                }
            }
        }
    };
    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Activity activity = MainActivity.this;
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(deviceName);
                          //  mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus("connecting");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus("not connected");
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    ChatMessage message = new ChatMessage(writeMessage,true);
                    messages.add(message);
                    if(messageAdapter==null){
                        setupChat();
                    }else{
                        messageAdapter.notifyDataSetChanged();
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                // Call smooth scroll
                                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                            }
                        });
                    }
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    String[] arpesan;
                    arpesan=readMessage.split("##");
                        String datapesan=arpesan[0];
                        String datatrack=arpesan[1];
                    String[] ardatapesan=datapesan.split("%");
                        String lwaktu=ardatapesan[0];
                        String lpesan=ardatapesan[1];
                        String lstatus=ardatapesan[2];
                        String lid_pengguna=ardatapesan[3];

                        String lketerangan="-";

                    Cursor c=helper.gCount2(lid_pengguna,lstatus);
                    c.moveToFirst();
                    int row = c.getCount();
                    String jum=c.getString(0);
                    Log.d("JUMLAH",jum+" zz  "+ String.valueOf(row)+" ID PENGGUNA "+lid_pengguna);
                    if (Integer.parseInt(jum)==0) {// Insert ke pesan

                        helper.inserttemp(lwaktu, lpesan, lstatus, lid_pengguna, lketerangan);
                        Toast.makeText(MainActivity.this, "Pesan Berhasil Ditambahkan, Pesan Akan  dikirim Ketika koneksi tersedia..", Toast.LENGTH_LONG).show();
                        int jd = 0;
                        String[] ardatatracking = datatrack.split("@");
                        jd = ardatatracking.length;

                        for (int i = 0; i < jd; i++) {
                            String[] tracking = ardatatracking[i].split("%");
                            String data1 = tracking[0];
                            String data2 = tracking[1];
                            String data3 = tracking[2];
                            String data4 = tracking[3];


                            helper2.insertkoordinat(data1, data4, data2, data3, "-");

                        }
                    }

                    c=helper.getAll();
                     row = c.getCount();
                    c.moveToFirst();
                    for (int i=0;i<row;i++) {

                        waktu=helper.getwaktu(c);
                        String[] arwaktu=waktu.split("#");
                        tanggal=arwaktu[0];
                        jam=arwaktu[1];
                        pesan=helper.getpesan(c);
                        status=helper.getstatus(c);
                        id_pengguna0=helper.getid_pengguna(c);
                        keterangan=datatrack;
                        new save().execute();
                        c.moveToNext();
                    }
                    c.close();



                    Toast.makeText(MainActivity.this,"Msg:"+readMessage,Toast.LENGTH_LONG).show();
                    ChatMessage message1 = new ChatMessage(readMessage,false);
                    messages.add(message1);
                    if(messageAdapter==null){
                        setupChat();
                    }else{
                        messageAdapter.notifyDataSetChanged();
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                // Call smooth scroll
                                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                            }
                        });
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    deviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + deviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


    private void setStatus(String subTitle) {
        Activity activity =  MainActivity.this;
        if (null == activity) {
            return;
        }
        tvConnectedName.setText(subTitle);

    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
            helper.close();
        }
    }
    String gab;
    String waktu="";
    String tanggal="";
    String jam="";
    String pesan="";
    String status="";
    String id_pengguna0="";
    String keterangan="";


    void isConnectedToServer(String url, int timeout) {
        try{
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();





        } catch (Exception e) {
         //   nointernet();
        }
    }

    class save extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
        }
        int sukses;

        @SuppressLint("WrongThread")
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_pengguna", id_pengguna0));
            params.add(new BasicNameValuePair("waktu", waktu));
            params.add(new BasicNameValuePair("tanggal", tanggal));
            params.add(new BasicNameValuePair("jam", jam));
            params.add(new BasicNameValuePair("pesan", pesan));
            params.add(new BasicNameValuePair("keterangan", keterangan));
            params.add(new BasicNameValuePair("status", status));
            String url=ip+"pesan/pesan_add.php";
            Log.v("add",url);
            JSONObject json = jsonParser.makeHttpRequest(url,"POST", params);
            Log.d("add", json.toString());
            try {
                sukses= json.getInt(TAG_SUKSES);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }protected void onPostExecute(String file_url) {

        }
    }

}
