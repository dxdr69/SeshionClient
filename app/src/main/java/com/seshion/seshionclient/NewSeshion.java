package com.seshion.seshionclient;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.crypto.Cipher;

public class NewSeshion extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_seshion);

        final TextView seshionNameTextView = (TextView) findViewById(R.id.seshionnametextview);
        final TextView descriptionTextView = (TextView) findViewById(R.id.descriptiontextview);
        final TextView latTopLeftTextView = (TextView) findViewById(R.id.latitudetoplefttextview);
        final TextView longTopLeftTextView = (TextView) findViewById(R.id.longitudetoplefttextview);
        final TextView latBotRightTextView = (TextView) findViewById(R.id.latitudebottomrighttextview);
        final TextView longBotRightTextView = (TextView) findViewById(R.id.longitudebottomrighttextview);
        final CheckBox privacyCheckBox = (CheckBox) findViewById(R.id.privacycheckbox);
        final Button createSeshionButton = (Button) findViewById(R.id.createseshionbutton);

        createSeshionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = getIntent();
                String testUserNameString = intent.getStringExtra("USER_NAME");
                UserAccount loggedInUser = new UserAccount(testUserNameString, "test");

                /* obtain the time in the proper format */
                LocalTime time = LocalTime.now();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                String timeText = time.format(dtf);//LocalTime.parse(timeText, dtf)


                String test = seshionNameTextView.getText().toString();
                System.out.println("test = " + test);

                if( !TextUtils.isEmpty(seshionNameTextView.getText().toString())
                        && !TextUtils.isEmpty(latTopLeftTextView.getText().toString())
                        && !TextUtils.isEmpty(longTopLeftTextView.getText().toString())
                        && !TextUtils.isEmpty(latBotRightTextView.getText().toString())
                        && !TextUtils.isEmpty(longBotRightTextView.getText().toString()) ) {

                    System.out.println("LocalDate.now():" + LocalDate.now());
                    //System.out.println("LocalTime.now().parse():" + LocalTime.parse(formattedLocalTime));

                    double latTopLeft, longTopLeft, latBotRight, longBotRight;
                    latTopLeft = Double.parseDouble( latTopLeftTextView.getText().toString() );
                    longTopLeft = Double.parseDouble( longTopLeftTextView.getText().toString() );
                    latBotRight = Double.parseDouble( latBotRightTextView.getText().toString() );
                    longBotRight = Double.parseDouble( longBotRightTextView.getText().toString() );

                    UserSession newSeshion = new UserSession(
                            seshionNameTextView.getText().toString(),
                            loggedInUser.getUserName(),
                            descriptionTextView.getText().toString(),
                            latTopLeft,
                            longTopLeft,
                            latBotRight,
                            longBotRight,
                            LocalDate.now(), null,
                            LocalTime.now(), null,
                            privacyCheckBox.isChecked(), null);

                    System.out.println("new seshion obj:" + newSeshion.toString());

                    CreateSeshion createSeshion = new CreateSeshion();
                    createSeshion.execute(newSeshion);

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

    class CreateSeshion extends AsyncTask<UserSession, Void, Boolean> {

        ProgressDialog progressDialog;

        @Override
        protected Boolean doInBackground(UserSession... userSessions) {

            /* response from server */
            boolean seshionCreated = false;

            /* print seshion info */
            System.out.println("credentials:" + userSessions[0].getName());


            /* connect to server with the specified SERVER_IP address and port (socket obj is client socket)*/
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
//                dataBufferedNetInputStream.readFully(keyBytes);
//                dataBufferedNetInputStream.readFully(keyBytes, 0, publicKeyLength);
//                dataBufferedNetInputStream.read(keyBytes);
//                dataBufferedNetInputStream.read(keyBytes, 0, publicKeyLength);

//                dataNetInputStream.readFully(keyBytes);
                dataNetInputStream.readFully(keyBytes, 0, publicKeyLength);
//                dataNetInputStream.read(keyBytes);
//                dataNetInputStream.read(keyBytes, 0, publicKeyLength);
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
                /* pass the object to the actions class which creates the json and adds
                action as first element in array */
                String json = Actions.createNewSeshionJson(userSessions[0]);

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
                String response = new String(decryptedMessage);
                System.out.println("response:" + response);

                /* response is just a "1, or -1" */
                if(response.equals("\"1\"") || response.startsWith("[\"1\"")){
                    seshionCreated = true;
                }

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

            return seshionCreated;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(
                    NewSeshion.this,
                    "Creating Seshion...",
                    "Please Wait...");
        }

        @Override
        protected void onPostExecute(Boolean seshionCreated){

            if(seshionCreated == true) {

                /* leave activity and return to seshions screen */
                Toast.makeText(getApplicationContext(), "Seshion Created!!!!!!!!!", Toast.LENGTH_LONG).show();
                finish();

            } else {

                /* dismiss the alert */
                progressDialog.dismiss();

                /* show little message to the user */
                Toast.makeText(getApplicationContext(), "Seshion could not be created. Perhaps the name was already taken", Toast.LENGTH_LONG).show();

            }

        }

    }

}

