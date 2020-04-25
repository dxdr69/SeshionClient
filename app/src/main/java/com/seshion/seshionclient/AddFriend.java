package com.seshion.seshionclient;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AddFriend extends AppCompatActivity {

    private String name;
    String owner;
    double latitudeTopLeft;
    double longitudeTopLeft;
    double latitudeTopRight;
    double longitudeTopRight;
    double latitudeBottomLeft;
    double longitudeBottomLeft;
    double latitudeBottomRight;
    double longitudeBottomRight;
    LocalDate startDate;
    LocalTime startTime;
    boolean isSessionPrivate;

    private static Socket socket;
    private static DataOutputStream dataBufferedNetOutputStream;/* data stream to send/receive byte arrays */
    private static DataInputStream dataBufferedNetInputStream;
    private static DataInputStream dataNetInputStream;//no buffer
    private static DataOutputStream dataNetOutputStream;//no buffer
    private static BufferedOutputStream bufferedOutputStream;/* buffered streams adds memory, and makes stream communication asynchronous. */
    private static BufferedInputStream bufferedInputStream;

    private static String SERVER_IP = "134.209.38.86";
    private static AES aes;

    RecyclerView recyclerView;
    FriendsRVAdapter myAdapter;

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        context = this;

        final TextView searchBar = (TextView) findViewById(R.id.searchbar);
        final Button searchButton = (Button) findViewById(R.id.searchbutton);

//        //            /* our recycler view */
//        recyclerView = findViewById(R.id.recyclerview);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = getIntent();
                String userNameString = intent.getStringExtra("USER_NAME");

                if( !TextUtils.isEmpty( searchBar.getText().toString() ) ) {

                    String friend = searchBar.getText().toString();
                    LookupFriend lookupFriend = new LookupFriend();
                    lookupFriend.execute(userNameString, friend);

                }
            }
        });


        //UserSession newSeshion = new UserSession()

//        UserSession(String name, String owner,
//        double latitudeTopLeft, double longitudeTopLeft,
//        double latitudeTopRight, double longitudeTopRight,
//        double latitudeBottomLeft, double longitudeBottomLeft,
//        double latitudeBottomRight, double longitudeBottomRight,
//        LocalDate startDate, LocalTime startTime,
//        boolean isSessionPrivate )
    }

    class LookupFriend extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;



        @Override
        protected String doInBackground(String... homies) {

            //context = getContext

            /* response from server */
            List<UserAccount> returnedFriends = null;

            /* print seshion info */
            System.out.println(homies[0] + " is looking for " + homies[1]);

            String response = "";

            /* connect to server with the specified SERVER_IP address and port (socket obj is client socket) */
            try {
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

                /* obtain AES (symmetric) key information from client */
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

                /* connection and encryption done */

//                /* make json string using googles Gson library */
//                Gson gson = new Gson();
//                String json = gson.toJson(users[0]);//pass first and only user obj passed
//                System.out.println("json:\n" + json);
//
                /* get search for a friend json*/
                String json = Actions.searchForFriend(homies[1]);

                /* make byte array */
                byte[] createSeshionBytes = json.getBytes();

                /* encrypt the data */
                byte[] encryptedByteArray = aes.encrypt(createSeshionBytes);

                /* now wed send the encryptedByteArray over the dataBufferedNetInputStream.
                 * First we'll send the length of the byte array we're sending,
                 * then the data.*/
                dataNetOutputStream.writeInt(encryptedByteArray.length);
                dataNetOutputStream.write(encryptedByteArray);

                /* receive response from server */
                int responseSize = dataNetInputStream.readInt();
                System.out.println("Receieved length: " + responseSize);
                byte[] responseArray = new byte[responseSize];
                dataNetInputStream.readFully(responseArray, 0, responseSize);
                System.out.println("Receieved responseArray: " + responseArray.toString());

                /* now to decrypt the message */
                byte[] decryptedMessage = aes.decrypt(responseArray);

                /* now we have to separate the response */
                response = new String(decryptedMessage);
                System.out.println("response:" + response);

                /* display the response */
                //returnedFriends = Actions.fetchFriends(response);




            } catch (EOFException eofe) {
                /* print stack trace */
                eofe.printStackTrace();

            } catch (IOException ioe) {
                /* print stack trace */
                ioe.printStackTrace();
            } finally {

                try {

                    /* try to flush the bufferedOutputStream */
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();

                    /* flush and close the dataBufferedNetOutputStream */
                    dataNetInputStream.close();
                    dataBufferedNetOutputStream.flush();
                    dataBufferedNetOutputStream.close();

                    /* disconnect the client */
                    socket.close();
                } catch (EOFException eofe) {
                    /* print stack trace */
                    eofe.printStackTrace();

                } catch (IOException ioe) {
                    /* print stack trace */
                    ioe.printStackTrace();

                }

            }

            return response;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(
                    AddFriend.this,
                    "Searching for users...",
                    "Please Wait...");
        }

        @Override
        protected void onPostExecute(String returnedFriends){


//            /* display the results in the recycler viewer */
//            for(int i = 0; i < returnedFriends.size(); i++){
//                System.out.println("User name: " + returnedFriends.get(i).getUserName());
//            }

            /* display returned friends in on post execute*/
//            myAdapter = new FriendsRVAdapter(context, (ArrayList) returnedFriends);
//            recyclerView.setAdapter(myAdapter);

            /* leave activity and return to seshions screen */
            //

            /* put into gson */
//            Gson gson = new Gson();
//            String jsonFriendsArray = gson.toJson(returnedFriends);
            if(returnedFriends.equals("[\"0\"])")) {
                /* make a label that says no friends */
                Toast.makeText(getApplicationContext(), "Could not find user, try another name!", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(context, DisplayFriendSearch.class);
                intent.putExtra("ListOfFriends", returnedFriends);
                startActivity(intent);

            }

            /* dismiss the alert */
            progressDialog.dismiss();

            /* show little message to the user */




        }

    }
}
