package com.seshion.seshionclient;

import androidx.appcompat.app.AppCompatActivity;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Dash;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.crypto.Cipher;

public class DashboardActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem friendZoneTab, seshionsTab, mapTab;
    public PageAdapter pagerAdapter;//this is the class that needs to be overridden to get the behavior we want

    /* vars */
    //private final String SEND_COORDS = "NEW_COORDS";
    private static StateInfo stateInfo;
    private static Socket socket;
    private static DataOutputStream dataBufferedNetOutputStream;/* data stream to send/receive byte arrays */
    private static DataInputStream dataBufferedNetInputStream;
    private static DataInputStream dataNetInputStream;//no buffer
    private static DataOutputStream dataNetOutputStream;//no buffer
    private static BufferedOutputStream bufferedOutputStream;/* buffered streams adds memory, and makes stream communication asynchronous. */
    private static BufferedInputStream bufferedInputStream;
    private Context context;

    /* ONE OF TWO LOGGED IN USER OBJECTS - this one holds their coordinates and logged in seshions
    *  We keep it light so that we aren't sending a ton of un-necessary information each coord check */
    private static UserAccount loggedInUser;//= new UserAccount("Twizzy", "nunyabiz");

    private TextView commsTextView;
    String message = "";
    private static String SERVER_IP = "134.209.38.86";
    private static AES aes;

    /* for storing the responses of the coordinate transfers */
    byte[] decryptedMessage = null;

    /* for receiving location */
    private IntentFilter myIntentFilter;

    public static final String mBroadcastStringAction = "com.truiton.broadcast.string";
    public static final String mBroadcastIntegerAction = "com.truiton.broadcast.integer";
    public static final String mBroadcastArrayListAction = "com.truiton.broadcast.arraylist";

    private static Location polledLocation = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        /* controls */
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        friendZoneTab = (TabItem) findViewById(R.id.friendzonetab);
        seshionsTab = (TabItem) findViewById(R.id.seshionstab);
        mapTab = (TabItem) findViewById(R.id.maptab);
        viewPager = findViewById(R.id.viewpager);

        /* obtain data from previous activity */
        Intent intent = getIntent();

        String loginResponse = intent.getStringExtra("Response");
        loggedInUser = (UserAccount) intent.getSerializableExtra("UserAccount");
        UserAccount usersData = new UserAccount("Twizzy");
        System.out.println("loginResponse\n" + loginResponse);
        Actions.packUserInfo(loginResponse, usersData);

        /* set title of dashboard to users name */
        this.setTitle(loggedInUser.getUserName());

        /* set the joined seshions of the loggedInUser */
        loggedInUser.setJoinedSessions(usersData.getJoinedSessions());

        /* for the broadcast receiver */
        myIntentFilter = new IntentFilter("SEND_COORDS");

        pagerAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),
                usersData.getAllFriends(),
                usersData.getOwnedGroups(),
                usersData.getAllMessages(),
                usersData.getAllOpenSessions());
        viewPager.setAdapter(pagerAdapter);

        //SendCoords sc = new SendCoords(loggedInUser);
        //sc.start();

        /* this will start the location service */
        Intent locationServiceIntent = new Intent(this, LocationService.class);
        startService(locationServiceIntent);

//        IntentFilter filter = new IntentFilter("SEND_COORDS");

        /* this will start the update seshions service */
//        Intent updateSeshionServiceIntent = new Intent(this, SeshionService.class);
//        startService(updateSeshionServiceIntent);

//        IntentFilter filter = new IntentFilter("SEND_COORDS");


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) {
                    pagerAdapter.notifyDataSetChanged();
                } else if (tab.getPosition() == 1) {
                    pagerAdapter.notifyDataSetChanged();
                } else if (tab.getPosition() == 2) {
                    pagerAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(locationReceiver, myIntentFilter);
    }

    @Override
    public void onPause() {
        unregisterReceiver(locationReceiver);
        super.onPause();
    }

    private BroadcastReceiver locationReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {

            System.out.println("intent.getAction():" + intent.getAction());
            if(intent.getAction().equals("SEND_COORDS")) {
                final double latitude = intent.getDoubleExtra("Latitude", 0.00);
                final double longitude = intent.getDoubleExtra("Longitude", 0.00);
                String provider = intent.getStringExtra("Provider");
                polledLocation = new Location(provider);
                polledLocation.setLatitude(latitude);
                polledLocation.setLongitude(longitude);

                loggedInUser.setLatitude(latitude);
                loggedInUser.setLongitude(longitude);
                System.out.println("from setUsersLocation - users latitude:" + loggedInUser.getLatitude());
                System.out.println("from setUsersLocation - users longitude:" + loggedInUser.getLongitude());

                SendCoords sendCoords = new SendCoords();
                sendCoords.execute(loggedInUser);

                /* for stopping the service, but we don't want to for as long as the application is open */
                Intent stopIntent = new Intent(DashboardActivity.this, LocationService.class);
                //stopService(stopIntent);
            }
        }
    };

    class SendCoords extends AsyncTask<UserAccount, Void, String> implements LocationListener
    {

        private String resp;
        ProgressDialog progressDialog;
        private UserAccount user;
        private Location location;
        private UserSession theOneWellCheckInto;
        private String departedSeshName = "";

//        public SendCoords(UserAccount userAccount){
//            this.user = userAccount;
//        }



        @Override
        protected String doInBackground(UserAccount... users)  {

            String response = "";
            try {

                try{
                    Thread.sleep(5000);
                } catch (InterruptedException ie){
                    ie.printStackTrace();
                }
                /* looper for creating a handler inside a thread - must be the sole (Singleton)
                 looper, turns out one gets created for this thread already so...*/
//                if (Looper.myLooper() == null)
//                    Looper.prepare();

                /* connect to server with the specified SERVER_IP address and port (socket obj is client socket)*/
                socket = new Socket(SERVER_IP, 8090);

                /* streams */
                bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
                dataBufferedNetOutputStream = new DataOutputStream(bufferedOutputStream);
                dataNetOutputStream = new DataOutputStream(socket.getOutputStream());

                bufferedInputStream = new BufferedInputStream(socket.getInputStream());
                dataBufferedNetInputStream = new DataInputStream(bufferedInputStream);
                dataNetInputStream = new DataInputStream(socket.getInputStream());

                /* obtain public RSA key from server */
                System.out.println("attempting to obtain key length...");
                int publicKeyLength = dataNetInputStream.readInt();
                System.out.println("public key length:" + publicKeyLength);
                byte[] keyBytes = new byte[publicKeyLength];
                System.out.println("attempting to read ");
                dataNetInputStream.readFully(keyBytes, 0, publicKeyLength);
                System.out.println("Received public RSA key from server");

                /* recover the key (decode it, public uses X.509, private uses PKCS8 */
                PublicKey rsaPublicKey = null;
                Cipher asymmetricCipher = null;
                RSA rsa = new RSA();
                //RSA rsa = RSA.getInstance();
                rsaPublicKey = rsa.decodePublicKey(keyBytes);
                rsa.setPublicKey(rsaPublicKey);
                System.out.println("Public Key Decoded: \n" + rsaPublicKey.toString());

                /* AES symmetric encryption key */
                aes = new AES();
                //AES aes = AES.getInstance();
                byte[] aesKey = aes.getSymmetricKey();
                byte[] encryptedAESKey = null;

                /* encrypt symmetric (AES) key with RSA and send to server */
                encryptedAESKey = rsa.encrypt(aesKey);
                System.out.println("RSA Encrypted AES key" + encryptedAESKey);
                System.out.println("RSA Encrypted AES key size" + encryptedAESKey.length);
                dataNetOutputStream.write(encryptedAESKey);//sends 16 byte key, may need the offset and length method

                /* make a json byte array containing the action: setusercoords and the user object containing them */
                Gson gson = new Gson();
                Collection col = new ArrayList();
                col.add("setcoordinates");
                col.add(loggedInUser);
                String coordsJson = gson.toJson(col);
                byte[] coordsBytes = coordsJson.getBytes();
                byte[] encryptedCoordsBytes = aes.encrypt(coordsBytes);
                int encArraySize = encryptedCoordsBytes.length;

                /* first send the size of the array then send the array */
                dataNetOutputStream.writeInt(encArraySize);
                dataNetOutputStream.write(encryptedCoordsBytes);

                /* receive the response */
                int responseSize = dataNetInputStream.readInt();
                byte[] responseArray = new byte[responseSize];
                System.out.println("response size:" + responseSize);
                dataNetInputStream.read(responseArray);

                /* now to decrypt the message */
                decryptedMessage = aes.decrypt(responseArray);

                /* now we have to separate the response */
                response = new String(decryptedMessage);
                System.out.println("response:" + response);

                /* check if response has just the one in it, if so we just checked out */
                if(response.equals("[\"1\"]")){
                    /* the user's object should have a joined seshion */
                    departedSeshName = loggedInUser.getJoinedSessions().get(0).getName();
                    System.out.println("DEPARTED SESHION NAME: " + departedSeshName);
                    loggedInUser.checkOutOfSeshion();


                }else{

                    /* investigate whether we checked into anything*/
                    theOneWellCheckInto = Actions.fetchSeshionCheckIn(response, theOneWellCheckInto);

                    /* if the user was just checked out or if the user wasn't checked in: */
                    try{
                        if(theOneWellCheckInto!=null ){
                            System.out.println("checked in seshion id:" + theOneWellCheckInto.getID().toString());
                            loggedInUser.checkIntoSeshion(theOneWellCheckInto);
                        }
                    }catch (NullPointerException npe){
                        System.out.println("SESHION ID NULL");
                    }

                }

            } catch (EOFException eofe){
                /* print stack trace */
                eofe.printStackTrace();

            } catch(IOException ioe){
                /* print stack trace */
                ioe.printStackTrace();
            }finally {


                /* try to flush the bufferedOutputStream */
                try {
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();

                    /* flush and close the dataBufferedNetOutputStream */
                    dataNetInputStream.close();
                    dataBufferedNetOutputStream.flush();
                    dataBufferedNetOutputStream.close();

                    /* disconnect the client */
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return response;

        }

        @Override
        protected void onPostExecute(String response){
            /* Display the dialog fragment that the user has checked in or checked out if so */

            /* checked out */
            if(!departedSeshName.equals("")){
                // DialogFragment.show() will take care of adding the fragment
                // in a transaction.  We also want to remove any currently showing
                // dialog, so make our own transaction and take care of that here.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment newFragment = MyDialogFragment.newInstance(1, 2, "You've just departed from the " + departedSeshName + " seshion!");
                newFragment.show(ft, "dialog");
            }

            /* checked in */
            if(theOneWellCheckInto!=null){

                String messageStr = "You've been checked into the " + theOneWellCheckInto.getName() + " seshion!";

                // DialogFragment.show() will take care of adding the fragment
                // in a transaction.  We also want to remove any currently showing
                // dialog, so make our own transaction and take care of that here.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment newFragment = MyDialogFragment.newInstance(1, 2, messageStr);
                newFragment.show(ft, "dialog");

//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException tie){
//                    tie.printStackTrace();
//                }
            }

        }


        @Override
        public void onLocationChanged(Location location) {

            this.location = location;
            Looper.myLooper().quit();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.d("Latitude","disable");
            Log.d("Latitude","status");
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.d("Latitude","enable");
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.d("Latitude","disable");
        }


    }


}
